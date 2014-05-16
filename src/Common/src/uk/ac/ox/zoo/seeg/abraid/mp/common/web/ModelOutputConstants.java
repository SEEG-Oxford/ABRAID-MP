package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

/**
 * Contains constants relating to the outputs of a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelOutputConstants {
    private ModelOutputConstants() {
    }

    /** The metadata's filename in the model outputs. */
    public static final String METADATA_JSON_FILENAME = "metadata.json";

    /** The mean prediction raster's filename in the model outputs. */
    public static final String MEAN_PREDICTION_RASTER_FILENAME = "mean_prediction.asc";
}
