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
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver.GeoserverRestService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

/**
 * Main model output handler.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainHandler {
    private static final Logger LOGGER = Logger.getLogger(MainHandler.class);
    private static final String LOG_MEAN_PREDICTION_RASTER =
            "Saving mean prediction raster (%s bytes) for model run \"%s\"";
    private static final String LOG_PREDICTION_UNCERTAINTY_RASTER =
            "Saving prediction uncertainty raster (%s bytes) for model run \"%s\"";
    private static final String LOG_VALIDATION_STATISTICS_FILE =
            "Saving validation statistics file (%s bytes) for model run \"%s\"";
    private static final String LOG_RELATIVE_INFLUENCE_FILE =
            "Saving relative influence file (%s bytes) for model run \"%s\"";
    private static final String LOG_EFFECT_CURVES_FILE =
            "Saving effect curves file (%s bytes) for model run \"%s\"";
    private static final String COULD_NOT_SAVE_VALIDATION_STATISTICS =
            "Could not save validation statistics csv for model run \"%s\"";
    private static final String COULD_NOT_SAVE_RELATIVE_INFLUENCE =
            "Could not save relative influence csv for model run \"%s\"";
    private static final String COULD_NOT_SAVE_EFFECT_CURVES =
            "Could not save effect curves csv for model run \"%s\"";
    private static final String COULD_NOT_SAVE_UNCERTAINTY_RASTER =
            "Could not save prediction uncertainty for model run \"%s\"";
    private static final String COULD_NOT_SAVE_PREDICTION_RASTER =
            "Could not save mean prediction raster for model run \"%s\"";
    private static final String RASTER_FILE_ALREADY_EXISTS =
            "Raster file \"%s\" already exists";
    private static final String FAILED_TO_CREATE_DIRECTORY_FOR_OUTPUT_RASTERS =
            "Failed to create directory for output rasters: %s";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final ModelRunService modelRunService;
    private final GeoserverRestService geoserver;
    private final File rasterDir;

    public MainHandler(ModelRunService modelRunService, GeoserverRestService geoserver, File rasterDir) {
        this.modelRunService = modelRunService;
        this.geoserver = geoserver;
        this.rasterDir = rasterDir;
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
        byte[] metadataJson = extract(zipFile, ModelOutputConstants.METADATA_JSON_FILENAME, true);
        String metadataJsonAsString = new String(metadataJson, StandardCharsets.UTF_8);
        ModelRun modelRun = handleMetadataJson(metadataJsonAsString);

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

        // Handle outputs
        handleValidationStatisticsFile(modelRun, validationStatisticsFile);
        handleRelativeInfluenceFile(modelRun, relativeInfluenceFile);
        handleEffectCurvesFile(modelRun, effectCurvesFile);
        handleMeanPredictionRaster(modelRun, meanPredictionRaster);
        handlePredictionUncertaintyRaster(modelRun, predUncertaintyRaster);

        return modelRun;
    }

    private ModelRun handleMetadataJson(String metadataJson) {
        // Parse the JSON and retrieve the model run from the database with the specified name
        JsonModelOutputsMetadata metadata = new JsonParser().parse(metadataJson, JsonModelOutputsMetadata.class);
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
            LOGGER.info(String.format(LOG_VALIDATION_STATISTICS_FILE, file.length, modelRun.getName()));
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
                throw new IOException(String.format(COULD_NOT_SAVE_VALIDATION_STATISTICS, modelRun.getName()), e);
            }
        }
    }

    private void handleRelativeInfluenceFile(final ModelRun modelRun, byte[] file) throws IOException {
        if (file != null) {
            LOGGER.info(String.format(LOG_RELATIVE_INFLUENCE_FILE, file.length, modelRun.getName()));
            try {
                List<CsvCovariateInfluence> csvCovariateInfluence =
                        CsvCovariateInfluence.readFromCSV(new String(file, UTF8));
                List<CovariateInfluence> covariateInfluences = with(csvCovariateInfluence)
                        .convert(new Converter<CsvCovariateInfluence, CovariateInfluence>() {
                            @Override
                            public CovariateInfluence convert(CsvCovariateInfluence csv) {
                                return new CovariateInfluence(csv, modelRun);
                            }
                        });
                modelRun.setCovariateInfluences(covariateInfluences);
                modelRunService.saveModelRun(modelRun);
            } catch (IOException e) {
                throw new IOException(String.format(COULD_NOT_SAVE_RELATIVE_INFLUENCE, modelRun.getName()), e);
            }
        }
    }

    private void handleEffectCurvesFile(final ModelRun modelRun, byte[] file) throws IOException {
        if (file != null) {
            LOGGER.info(String.format(LOG_EFFECT_CURVES_FILE, file.length, modelRun.getName()));
            try {
                List<CsvEffectCurveCovariateInfluence> csvEffectCurveCovariateInfluences =
                        CsvEffectCurveCovariateInfluence.readFromCSV(new String(file, UTF8));
                List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences =
                    with(csvEffectCurveCovariateInfluences)
                        .convert(new Converter<CsvEffectCurveCovariateInfluence, EffectCurveCovariateInfluence>() {
                            @Override
                            public EffectCurveCovariateInfluence convert(CsvEffectCurveCovariateInfluence csv) {
                                return new EffectCurveCovariateInfluence(csv, modelRun);
                            }
                        });
                modelRun.setEffectCurveCovariateInfluences(effectCurveCovariateInfluences);
                modelRunService.saveModelRun(modelRun);
            } catch (IOException e) {
                throw new IOException(String.format(COULD_NOT_SAVE_EFFECT_CURVES, modelRun.getName()), e);
            }
        }
    }

    private void handleMeanPredictionRaster(ModelRun modelRun, byte[] raster) throws IOException {
        if (raster != null) {
            try {
                LOGGER.info(String.format(LOG_MEAN_PREDICTION_RASTER, raster.length, modelRun.getName()));
                File file = saveRaster(modelRun, raster, "mean");
                if (modelRun.getStatus() == ModelRunStatus.COMPLETED) {
                    geoserver.publishGeoTIFF(file);
                }
                modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), raster);
            } catch (Exception e) {
                throw new IOException(String.format(COULD_NOT_SAVE_PREDICTION_RASTER, modelRun.getName()), e);
            }
        }
    }

    private void handlePredictionUncertaintyRaster(ModelRun modelRun, byte[] raster) throws IOException {
        if (raster != null) {
            try {
                LOGGER.info(String.format(LOG_PREDICTION_UNCERTAINTY_RASTER, raster.length, modelRun.getName()));
                File file = saveRaster(modelRun, raster, "uncertainty");
                if (modelRun.getStatus() == ModelRunStatus.COMPLETED) {
                    geoserver.publishGeoTIFF(file);
                }
                modelRunService.updatePredictionUncertaintyRasterForModelRun(modelRun.getId(), raster);
            } catch (Exception e) {
                throw new IOException(String.format(COULD_NOT_SAVE_UNCERTAINTY_RASTER, modelRun.getName()), e);
            }
        }
    }

    private File saveRaster(ModelRun modelRun, byte[] raster, String type) throws IOException {
        String basename = modelRun.getName() + "_" + type;
        final File file = Paths.get(rasterDir.getAbsolutePath(), basename + ".tif").toFile();

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
