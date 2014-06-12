package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelOutputsMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    private ModelRunService modelRunService;

    public MainHandler(ModelRunService modelRunService) {
        this.modelRunService = modelRunService;
    }

    /**
     * Handles the model outputs contained in the specified zip file.
     * @param modelRunZipFile The zip file resulting from the model run.
     * @throws ZipException if a zip-related error occurs
     * @throws IOException if an IO-related error occurs
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleOutputs(File modelRunZipFile) throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(modelRunZipFile);

        // Handle the model run metadata
        byte[] metadataJson = extract(zipFile, ModelOutputConstants.METADATA_JSON_FILENAME, true);
        String metadataJsonAsString = new String(metadataJson, StandardCharsets.UTF_8);
        ModelRun modelRun = handleMetadataJson(metadataJsonAsString);

        boolean areOutputsMandatory = (modelRun.getStatus() == ModelRunStatus.COMPLETED);

        // Handle mean prediction raster
        byte[] meanPredictionRaster =
                extract(zipFile, ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME, areOutputsMandatory);
        handleMeanPredictionRaster(modelRun, meanPredictionRaster);

        // Handle prediction uncertainty raster
        byte[] predUncertaintyRaster =
                extract(zipFile, ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME, areOutputsMandatory);
        handlePredictionUncertaintyRaster(modelRun, predUncertaintyRaster);
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

    private void handleMeanPredictionRaster(ModelRun modelRun, byte[] raster) {
        if (raster != null) {
            LOGGER.info(String.format(LOG_MEAN_PREDICTION_RASTER, raster.length, modelRun.getName()));
            modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), raster);
        }
    }

    private void handlePredictionUncertaintyRaster(ModelRun modelRun, byte[] raster) {
        if (raster != null) {
            LOGGER.info(String.format(LOG_PREDICTION_UNCERTAINTY_RASTER, raster.length, modelRun.getName()));
            modelRunService.updatePredictionUncertaintyRasterForModelRun(modelRun.getId(), raster);
        }
    }

    private byte[] extract(ZipFile zipFile, String fileName, boolean isFileMandatory) throws ZipException, IOException {
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

    private ModelRun getModelRun(String name) {
        ModelRun modelRun = modelRunService.getModelRunByName(name);
        if (modelRun == null) {
            throw new IllegalArgumentException(String.format("Model run with name %s does not exist", name));
        }
        return modelRun;
    }
}
