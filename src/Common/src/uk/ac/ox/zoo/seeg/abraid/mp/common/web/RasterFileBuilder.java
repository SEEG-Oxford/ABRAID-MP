package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.io.File;
import java.nio.file.Paths;

/**
 * Builds a raster file location.
 * Copyright (c) 2014 University of Oxford
 */
public class RasterFileBuilder {
    private File rasterFileDirectory;

    public RasterFileBuilder(File rasterFileDirectory) {
        this.rasterFileDirectory = rasterFileDirectory;
    }

    /**
     * Gets the location of the mean prediction raster file for the specified model run.
     * @param modelRun The model run.
     * @return A mean prediction raster file location.
     */
    public File getMeanPredictionRasterFile(ModelRun modelRun) {
        return getRasterFile(modelRun, "mean");
    }

    /**
     * Gets the location of the prediction uncertainty file for the specified model run.
     * @param modelRun The model run.
     * @return A prediction uncertainty raster file location.
     */
    public File getPredictionUncertaintyRasterFile(ModelRun modelRun) {
        return getRasterFile(modelRun, "uncertainty");
    }

    private File getRasterFile(ModelRun modelRun, String type) {
        String basename = modelRun.getName() + "_" + type;
        return Paths.get(rasterFileDirectory.getAbsolutePath(), basename + ".tif").toFile();
    }
}
