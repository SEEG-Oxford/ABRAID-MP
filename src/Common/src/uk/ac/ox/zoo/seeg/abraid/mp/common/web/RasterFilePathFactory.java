package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.io.File;
import java.nio.file.Paths;

/**
 * Builds a raster file location.
 * Copyright (c) 2014 University of Oxford
 */
public class RasterFilePathFactory {
    private static final String MEAN_PREDICTION_RASTER_TYPE = "mean";
    private static final String PREDICTION_UNCERTAINTY_RASTER_TYPE = "uncertainty";
    private static final String FILENAME_FORMAT = "%s_%s.tif";

    private File rasterFileDirectory;

    public RasterFilePathFactory(File rasterFileDirectory) {
        this.rasterFileDirectory = rasterFileDirectory;
    }

    /**
     * Gets the location of the mean prediction raster file for the specified model run.
     * @param modelRun The model run.
     * @return A mean prediction raster file location.
     */
    public File getMeanPredictionRasterFile(ModelRun modelRun) {
        return getRasterFile(modelRun, MEAN_PREDICTION_RASTER_TYPE);
    }

    /**
     * Gets the location of the prediction uncertainty file for the specified model run.
     * @param modelRun The model run.
     * @return A prediction uncertainty raster file location.
     */
    public File getPredictionUncertaintyRasterFile(ModelRun modelRun) {
        return getRasterFile(modelRun, PREDICTION_UNCERTAINTY_RASTER_TYPE);
    }

    private File getRasterFile(ModelRun modelRun, String type) {
        String fileName = String.format(FILENAME_FORMAT, modelRun.getName(), type);
        return Paths.get(rasterFileDirectory.getAbsolutePath(), fileName).toFile();
    }
}
