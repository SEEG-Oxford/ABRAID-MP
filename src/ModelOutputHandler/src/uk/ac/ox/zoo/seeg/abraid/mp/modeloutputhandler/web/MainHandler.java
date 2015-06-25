package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import ch.lambdaj.function.convert.Converter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvEffectCurveCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvSubmodelStatistic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelOutputsMetadata;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.RasterUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver.GeoserverRestService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

/**
 * Main model output handler.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainHandler {
    private static final Logger LOGGER = Logger.getLogger(MainHandler.class);
    private static final String PREDICTION_RASTER = "prediction raster";
    private static final String UNCERTAINTY_RASTER = "uncertainty raster";
    private static final String EXTENT_RASTER = "extent raster";
    private static final String STATISTICS_CSV = "validation statistics csv";
    private static final String RELATIVE_INFLUENCE_CSV = "relative influence csv";
    private static final String EFFECT_CURVES_CSV = "effect curves csv";
    private static final String LOG_SAVING_FILE =
            "Saving %s (%s bytes) for model run \"%s\"";
    private static final String LOG_COULD_NOT_SAVE =
            "Could not save %s for model run \"%s\"";
    private static final String RASTER_FILE_ALREADY_EXISTS =
            "Raster file \"%s\" already exists";
    private static final String UNKNOWN_COVARIATE_FILE_REFERENCED =
            "Unknown covariate file referenced \"%s\"";
    private static final String FAILED_TO_CREATE_DIRECTORY_FOR_OUTPUT_RASTERS =
            "Failed to create directory for output rasters: %s";
    private static final String UNABLE_TO_DELETE_RASTER =
            "Unable to delete outdated 'full' raster file '%s'. " +
            "Will reattempt deletion when the next model run for disease group '%d' completes.";
    private static final String DELETED_OUTDATED_RASTER =
            "Deleted outdated 'full' raster file '%s'";

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private final ModelRunService modelRunService;
    private final CovariateService covariateService;
    private final GeoserverRestService geoserver;
    private final RasterFilePathFactory rasterFilePathFactory;
    private final ModelOutputRasterMaskingHelper modelOutputRasterMaskingHelper;

    public MainHandler(ModelRunService modelRunService,
                       CovariateService covariateService,
                       GeoserverRestService geoserver,
                       RasterFilePathFactory rasterFilePathFactory,
                       ModelOutputRasterMaskingHelper modelOutputRasterMaskingHelper) {
        this.modelRunService = modelRunService;
        this.covariateService = covariateService;
        this.geoserver = geoserver;
        this.rasterFilePathFactory = rasterFilePathFactory;
        this.modelOutputRasterMaskingHelper = modelOutputRasterMaskingHelper;
    }

    /**
     * Handles the model outputs contained in the specified zip file.
     * @param modelRunZipFile The zip file resulting from the model run.
     * @return The ModelRun object associated with the model run.
     * @throws ZipException if a zip-related error occurs
     * @throws IOException if an IO-related error occurs
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelRun handleOutputs(File modelRunZipFile) throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(modelRunZipFile);

        // Handle the model run metadata
        JsonModelOutputsMetadata metadata = extractMetadata(zipFile);
        ModelRun modelRun = handleModelRunMetadata(metadata);

        boolean areOutputsMandatory = (modelRun.getStatus() == ModelRunStatus.COMPLETED);

        // Extract outputs
        byte[] validationStatisticsFile =
                extract(zipFile, ModelOutputConstants.VALIDATION_STATISTICS_FILENAME, areOutputsMandatory);
        byte[] relativeInfluenceFile =
                extract(zipFile, ModelOutputConstants.RELATIVE_INFLUENCE_FILENAME, areOutputsMandatory);
        byte[] effectCurvesFile =
                extract(zipFile, ModelOutputConstants.EFFECT_CURVES_FILENAME, areOutputsMandatory);
        byte[] meanPredictionRaster =
                extract(zipFile, ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME, areOutputsMandatory);
        byte[] predUncertaintyRaster =
                extract(zipFile, ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME, areOutputsMandatory);
        byte[] extentInputRaster =
                extract(zipFile, ModelOutputConstants.EXTENT_INPUT_RASTER_FILENAME, areOutputsMandatory);

        // Handle outputs
        handleValidationStatisticsFile(modelRun, validationStatisticsFile);
        handleRelativeInfluenceFile(modelRun, relativeInfluenceFile);
        handleEffectCurvesFile(modelRun, effectCurvesFile);
        handleExtentInputRaster(modelRun, extentInputRaster);
        handleMeanPredictionRaster(modelRun, meanPredictionRaster);
        handlePredictionUncertaintyRaster(modelRun, predUncertaintyRaster);

        return modelRun;
    }

    /**
     * Handles the deletion of old model outputs. These are limited to pre-masking raster files that are no longer the
     * newest model run, for the given disease group.
     * @param diseaseGroupId The given disease group's id.
     * @return A boolean which is true if the deletions succeeded,
     *         or is false if one or more files could not be deleted (e.g. are currently in use elsewhere).
     */
    public boolean handleOldRasterDeletion(int diseaseGroupId) {
        ModelRun runToKeep = modelRunService.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId);
        Collection<ModelRun> runsToDelete = modelRunService.getModelRunsForDiseaseGroup(diseaseGroupId);
        runsToDelete.remove(runToKeep);

        boolean result = true;
        for (ModelRun runToDelete : runsToDelete) {
            File[] filesToDelete = new File[] {
                rasterFilePathFactory.getFullMeanPredictionRasterFile(runToDelete),
                rasterFilePathFactory.getFullPredictionUncertaintyRasterFile(runToDelete)
            };
            for (File fileToDelete : filesToDelete) {
                if (fileToDelete.exists()) {
                    if (!fileToDelete.delete()) {
                        result = false;
                        LOGGER.warn(String.format(UNABLE_TO_DELETE_RASTER,
                                                  fileToDelete.getAbsolutePath(), runToDelete.getDiseaseGroupId()));
                    } else {
                        LOGGER.info(String.format(DELETED_OUTDATED_RASTER,
                                                  fileToDelete.getAbsolutePath()));
                    }
                }
            }
        }
        return result;
    }

    private JsonModelOutputsMetadata extractMetadata(ZipFile zipFile) throws ZipException, IOException {
        byte[] metadataJson = extract(zipFile, ModelOutputConstants.METADATA_JSON_FILENAME, true);
        String metadataJsonAsString = new String(metadataJson, UTF8);
        return new JsonParser().parse(metadataJsonAsString, JsonModelOutputsMetadata.class);
    }

    private ModelRun handleModelRunMetadata(JsonModelOutputsMetadata metadata) {
        // Retrieve the model run from the database with the specified name
        ModelRun modelRun = getModelRun(metadata.getModelRunName());

        // Transfer the metadata to the model run from the database
        modelRun.setStatus(metadata.getModelRunStatus());
        modelRun.setResponseDate(DateTime.now());
        modelRun.setOutputText(metadata.getOutputText());
        modelRun.setErrorText(metadata.getErrorText());
        modelRunService.saveModelRun(modelRun);

        return modelRun;
    }

    private void handleValidationStatisticsFile(final ModelRun modelRun, byte[] file) throws IOException {
        if (file != null) {
            LOGGER.info(String.format(LOG_SAVING_FILE, STATISTICS_CSV, file.length, modelRun.getName()));
            try {
                List<CsvSubmodelStatistic> csvSubmodelStatistics =
                        CsvSubmodelStatistic.readFromCSV(new String(file, UTF8));
                List<SubmodelStatistic> submodelStatistics = with(csvSubmodelStatistics)
                        .convert(new Converter<CsvSubmodelStatistic, SubmodelStatistic>() {
                            @Override
                            public SubmodelStatistic convert(CsvSubmodelStatistic csv) {
                                return new SubmodelStatistic(csv, modelRun);
                            }
                        });
                modelRun.setSubmodelStatistics(submodelStatistics);
                modelRunService.saveModelRun(modelRun);
            } catch (IOException e) {
                throw new IOException(String.format(LOG_COULD_NOT_SAVE, STATISTICS_CSV, modelRun.getName()), e);
            }
        }
    }

    private void handleRelativeInfluenceFile(final ModelRun modelRun, byte[] file) throws IOException {
        if (file != null) {
            LOGGER.info(String.format(LOG_SAVING_FILE, RELATIVE_INFLUENCE_CSV, file.length, modelRun.getName()));
            try {
                List<CsvCovariateInfluence> csvCovariateInfluences =
                        CsvCovariateInfluence.readFromCSV(new String(file, UTF8));
                List<CovariateInfluence> covariateInfluences = with(csvCovariateInfluences)
                        .convert(new Converter<CsvCovariateInfluence, CovariateInfluence>() {
                            @Override
                            public CovariateInfluence convert(CsvCovariateInfluence csv) {
                                CovariateFile covariate =
                                        covariateService.getCovariateFileByPath(csv.getCovariateFilePath());
                                if (covariate == null) {
                                    throw new RuntimeException(UNKNOWN_COVARIATE_FILE_REFERENCED);
                                }
                                return new CovariateInfluence(covariate, csv, modelRun);
                            }
                        });
                modelRun.setCovariateInfluences(covariateInfluences);
                modelRunService.saveModelRun(modelRun);
            } catch (Exception e) {
                throw new IOException(String.format(LOG_COULD_NOT_SAVE, RELATIVE_INFLUENCE_CSV, modelRun.getName()), e);
            }
        }
    }

    private void handleEffectCurvesFile(final ModelRun modelRun, byte[] file) throws IOException {
        if (file != null) {
            LOGGER.info(String.format(LOG_SAVING_FILE, EFFECT_CURVES_CSV, file.length, modelRun.getName()));
            try {
                List<CsvEffectCurveCovariateInfluence> csvEffectCurveCovariateInfluences =
                    CsvEffectCurveCovariateInfluence.readFromCSV(new String(file, UTF8));
                List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences =
                    with(csvEffectCurveCovariateInfluences)
                        .convert(new Converter<CsvEffectCurveCovariateInfluence, EffectCurveCovariateInfluence>() {
                            @Override
                            public EffectCurveCovariateInfluence convert(CsvEffectCurveCovariateInfluence csv) {
                                CovariateFile covariate =
                                        covariateService.getCovariateFileByPath(csv.getCovariateFilePath());
                                if (covariate == null) {
                                    throw new RuntimeException(UNKNOWN_COVARIATE_FILE_REFERENCED);
                                }
                                return new EffectCurveCovariateInfluence(covariate, csv, modelRun);
                            }
                        });
                modelRun.setEffectCurveCovariateInfluences(effectCurveCovariateInfluences);
                modelRunService.saveModelRun(modelRun);
            } catch (Exception e) {
                throw new IOException(String.format(LOG_COULD_NOT_SAVE, EFFECT_CURVES_CSV, modelRun.getName()), e);
            }
        }
    }

    private void handleMeanPredictionRaster(ModelRun modelRun, byte[] raster) throws IOException {
        if (raster != null) {
            try {
                LOGGER.info(String.format(LOG_SAVING_FILE, PREDICTION_RASTER, raster.length, modelRun.getName()));

                File fullFile = rasterFilePathFactory.getFullMeanPredictionRasterFile(modelRun);
                saveRaster(fullFile, raster);

                File maskedFile = rasterFilePathFactory.getMaskedMeanPredictionRasterFile(modelRun);
                File maskFile = rasterFilePathFactory.getExtentInputRasterFile(modelRun);
                modelOutputRasterMaskingHelper.maskRaster(maskedFile, fullFile, maskFile, 0);

                if (modelRun.getStatus() == ModelRunStatus.COMPLETED) {
                    geoserver.publishGeoTIFF(maskedFile);
                }
            } catch (Exception e) {
                throw new IOException(String.format(LOG_COULD_NOT_SAVE, PREDICTION_RASTER, modelRun.getName()), e);
            }
        }
    }

    private void handlePredictionUncertaintyRaster(ModelRun modelRun, byte[] raster) throws IOException {
        if (raster != null) {
            try {
                LOGGER.info(String.format(LOG_SAVING_FILE, UNCERTAINTY_RASTER, raster.length, modelRun.getName()));

                File fullFile = rasterFilePathFactory.getFullPredictionUncertaintyRasterFile(modelRun);
                saveRaster(fullFile, raster);

                File maskedFile = rasterFilePathFactory.getMaskedPredictionUncertaintyRasterFile(modelRun);
                File maskFile = rasterFilePathFactory.getExtentInputRasterFile(modelRun);
                modelOutputRasterMaskingHelper.maskRaster(maskedFile, fullFile, maskFile, RasterUtils.UNKNOWN_VALUE);

                if (modelRun.getStatus() == ModelRunStatus.COMPLETED) {
                    geoserver.publishGeoTIFF(maskedFile);
                }
            } catch (Exception e) {
                throw new IOException(String.format(LOG_COULD_NOT_SAVE, UNCERTAINTY_RASTER, modelRun.getName()), e);
            }
        }
    }

    private void handleExtentInputRaster(ModelRun modelRun, byte[] raster) throws IOException {
        if (raster != null) {
            try {
                LOGGER.info(String.format(LOG_SAVING_FILE, EXTENT_RASTER, raster.length, modelRun.getName()));
                File file = rasterFilePathFactory.getExtentInputRasterFile(modelRun);
                saveRaster(file, raster);
            } catch (Exception e) {
                throw new IOException(String.format(LOG_COULD_NOT_SAVE, EXTENT_RASTER, modelRun.getName()), e);
            }
        }
    }

    private File saveRaster(File file, byte[] raster) throws IOException {
        if (file.exists()) {
            throw new IOException(String.format(RASTER_FILE_ALREADY_EXISTS, file));
        }

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException(String.format(
                    FAILED_TO_CREATE_DIRECTORY_FOR_OUTPUT_RASTERS, file.getParentFile().getAbsolutePath()));
        }

        FileUtils.writeByteArrayToFile(file, raster);
        return file;
    }

    private byte[] extract(ZipFile zipFile, String file, boolean isFileMandatory) throws ZipException, IOException {
        // Files in the zip are flattened, so remove the folder prefix if there is one
        String fileName = getFileNameFromPath(file);

        // Extract from zip
        FileHeader header = zipFile.getFileHeader(fileName);
        if (header == null) {
            if (isFileMandatory) {
                // Mandatory file not found - throw exception
                throw new IllegalArgumentException(String.format("File %s missing from model run outputs", fileName));
            } else {
                // Optional file not found - return null
                return null;
            }
        } else {
            try (ZipInputStream inputStream = zipFile.getInputStream(header)) {
                return IOUtils.toByteArray(inputStream);
            }
        }
    }

    private String getFileNameFromPath(String file) {
        return Paths.get(file).getFileName().toString();
    }

    private ModelRun getModelRun(String name) {
        ModelRun modelRun = modelRunService.getModelRunByName(name);
        if (modelRun == null) {
            throw new IllegalArgumentException(String.format("Model run with name %s does not exist", name));
        }
        return modelRun;
    }
}
