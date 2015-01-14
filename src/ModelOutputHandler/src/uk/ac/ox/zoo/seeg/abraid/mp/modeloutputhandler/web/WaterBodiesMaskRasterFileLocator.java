package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import java.io.File;

/**
 * A wrapper of the waterbodies.tif mask raster file, used for testability.
 * Copyright (c) 2014 University of Oxford
 */
public class WaterBodiesMaskRasterFileLocator {
    private final File waterBodiesMaskRasterFile;

    public WaterBodiesMaskRasterFileLocator(File waterBodiesMaskRasterFile) {
        this.waterBodiesMaskRasterFile = waterBodiesMaskRasterFile;
    }

    /**
     * Get the waterbodies.tif mask raster file.
     * @return The water bodies mask raster file.
     */
    public File getFile() {
        return waterBodiesMaskRasterFile;
    }
}
