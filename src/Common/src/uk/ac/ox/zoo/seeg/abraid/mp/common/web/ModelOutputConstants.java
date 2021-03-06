package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

/**
 * Contains constants relating to the outputs of a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class ModelOutputConstants {
    private ModelOutputConstants() {
    }

    /** The metadata's filename in the model outputs. */
    public static final String METADATA_JSON_FILENAME = "metadata.json";

    /** The mean prediction raster's filename in the model outputs. */
    public static final String MEAN_PREDICTION_RASTER_FILENAME = "results/mean_prediction.tif";

    /** The prediction uncertainty raster's filename in the model outputs. */
    public static final String PREDICTION_UNCERTAINTY_RASTER_FILENAME = "results/prediction_uncertainty.tif";

    /** The validation statistics filename in the model outputs. */
    public static final String VALIDATION_STATISTICS_FILENAME = "results/statistics.csv";

    /** The relative influence filename in the model outputs. */
    public static final String RELATIVE_INFLUENCE_FILENAME = "results/relative_influence.csv";

    /** The effect curves filename in the model outputs. */
    public static final String EFFECT_CURVES_FILENAME = "results/effect_curves.csv";

    /** The extent raster generated as an input to the R code. */
    public static final String EXTENT_INPUT_RASTER_FILENAME = "data/extent.tif";
}
