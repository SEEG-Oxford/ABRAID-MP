package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

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
        return modelRun;
    }

    /**
     * Handles the mean prediction raster from the model outputs.
     * @param modelRun The model run.
     * @param raster The mean prediction raster.
     */
    public void handleMeanPredictionRaster(ModelRun modelRun, byte[] raster) {
        modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), raster);
    }

    /**
     * Saves the model run.
     * @param modelRun The model run.
     */
    public void saveModelRun(ModelRun modelRun) {
        modelRunService.saveModelRun(modelRun);
    }

    private ModelRun getModelRun(String name) {
        ModelRun modelRun = modelRunService.getModelRunByName(name);
        if (modelRun == null) {
            throw new IllegalArgumentException(String.format("Model run with name %s does not exist", name));
        }
        return modelRun;
    }
}
