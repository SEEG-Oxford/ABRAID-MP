package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelOutputsMetadata;

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
     * Handles the metadata from the model outputs, which is in JSON format.
     * This results in retrieving the relevant ModelRun object from the database, and updating it.
     * @param metadataJson The metadata JSON.
     * @return The ModelRun object for this model run.
     */
    public ModelRun handleMetadataJson(String metadataJson) {
        JsonModelOutputsMetadata metadata = new JsonParser().parse(metadataJson, JsonModelOutputsMetadata.class);
        ModelRun modelRun = getModelRun(metadata.getModelRunName());
        modelRun.setResponseDate(DateTime.now());
        modelRunService.saveModelRun(modelRun);
        return modelRun;
    }

    /**
     * Handles the mean prediction raster from the model outputs.
     * @param modelRun The model run.
     * @param raster The mean prediction raster.
     */
    public void handleMeanPredictionRaster(ModelRun modelRun, byte[] raster) {
        LOGGER.info(String.format(LOG_MEAN_PREDICTION_RASTER, raster.length, modelRun.getName()));
        modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), raster);
    }

    /**
     * Handles the prediction uncertainty raster from the model outputs.
     * @param modelRun The model run.
     * @param raster The prediction uncertainty raster.
     */
    public void handlePredictionUncertaintyRaster(ModelRun modelRun, byte[] raster) {
        LOGGER.info(String.format(LOG_PREDICTION_UNCERTAINTY_RASTER, raster.length, modelRun.getName()));
        modelRunService.updatePredictionUncertaintyRasterForModelRun(modelRun.getId(), raster);
    }

    private ModelRun getModelRun(String name) {
        ModelRun modelRun = modelRunService.getModelRunByName(name);
        if (modelRun == null) {
            throw new IllegalArgumentException(String.format("Model run with name %s does not exist", name));
        }
        return modelRun;
    }
}
