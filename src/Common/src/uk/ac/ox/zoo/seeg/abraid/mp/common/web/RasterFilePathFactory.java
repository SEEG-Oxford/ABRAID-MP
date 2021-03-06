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
    private static final String EXTENT_INPUT_RASTER_TYPE = "extent";
    private static final String PREDICTION_UNCERTAINTY_RASTER_TYPE = "uncertainty";
    private static final String FULL_FILENAME_FORMAT = "%s_%s_full.tif";
    private static final String MASKED_FILENAME_FORMAT = "%s_%s.tif";
    private static final String EXTENT_FILENAME_FORMAT = "%s_%s.tif";
    private static final String ADMIN_RASTER_FILENAME_FORMAT = "admin%sqc.tif";
    private static final String EXTENT_GAUL_RASTER_FILENAME_FORMAT = "admin_%s.tif";
    private static final String GLOBAL_EXTENT_GAUL_TYPE = "global";
    private static final String TROPICAL_EXTENT_GAUL_TYPE = "tropical";

    private File resultsRasterFileDirectory;
    private File adminRasterFileDirectory;

    public RasterFilePathFactory(File resultsRasterFileDirectory, File adminRasterFileDirectory) {
        this.resultsRasterFileDirectory = resultsRasterFileDirectory;
        this.adminRasterFileDirectory = adminRasterFileDirectory;
    }

    /**
     * Gets the location of the masked mean prediction raster file for the specified model run.
     * @param modelRun The model run.
     * @return A masked mean prediction raster file location.
     */
    public File getMaskedMeanPredictionRasterFile(ModelRun modelRun) {
        return getMaskedRasterFile(modelRun, MEAN_PREDICTION_RASTER_TYPE);
    }

    /**
     * Gets the location of the pre-masking mean prediction raster file for the specified model run.
     * @param modelRun The model run.
     * @return A pre-masking mean prediction raster file location.
     */
    public File getFullMeanPredictionRasterFile(ModelRun modelRun) {
        return getFullRasterFile(modelRun, MEAN_PREDICTION_RASTER_TYPE);
    }

    /**
     * Gets the location of the masked prediction uncertainty raster file for the specified model run.
     * @param modelRun The model run.
     * @return A masked prediction uncertainty raster file location.
     */
    public File getMaskedPredictionUncertaintyRasterFile(ModelRun modelRun) {
        return getMaskedRasterFile(modelRun, PREDICTION_UNCERTAINTY_RASTER_TYPE);
    }

    /**
     * Gets the location of the pre-masking prediction uncertainty raster file for the specified model run.
     * @param modelRun The model run.
     * @return A pre-masking prediction uncertainty raster file location.
     */
    public File getFullPredictionUncertaintyRasterFile(ModelRun modelRun) {
        return getFullRasterFile(modelRun, PREDICTION_UNCERTAINTY_RASTER_TYPE);
    }

    /**
     * Gets the location of the extent input raster file for the specified model run.
     * @param modelRun The model run.
     * @return A prediction uncertainty raster file location.
     */
    public File getExtentInputRasterFile(ModelRun modelRun) {
        return getFile(modelRun, EXTENT_INPUT_RASTER_TYPE, EXTENT_FILENAME_FORMAT);
    }

    private File getFullRasterFile(ModelRun modelRun, String type) {
        return getFile(modelRun, type, FULL_FILENAME_FORMAT);
    }

    private File getMaskedRasterFile(ModelRun modelRun, String type) {
        return getFile(modelRun, type, MASKED_FILENAME_FORMAT);
    }

    private File getFile(ModelRun modelRun, String type, String pattern) {
        String fileName = String.format(pattern, modelRun.getName(), type);
        return Paths.get(resultsRasterFileDirectory.getAbsolutePath(), fileName).toFile();
    }

    /**
     * Gets the location of an admin unit raster file for the admin unit level.
     * @param level The admin unit level.
     * @return An admin unit raster file location.
     */
    public File getAdminRaster(int level) {
        String fileName = String.format(ADMIN_RASTER_FILENAME_FORMAT, level);
        return Paths.get(adminRasterFileDirectory.getAbsolutePath(), fileName).toFile();
    }

    /**
     * Gets the location of an extent gaul code raster file.
     * @param isGlobal If the global or tropical raster should be returned.
     * @return An extent gaul code raster file location.
     */
    public File getExtentGaulRaster(boolean isGlobal) {
        String fileName = String.format(EXTENT_GAUL_RASTER_FILENAME_FORMAT,
                (isGlobal ? GLOBAL_EXTENT_GAUL_TYPE : TROPICAL_EXTENT_GAUL_TYPE));
        return Paths.get(adminRasterFileDirectory.getAbsolutePath(), fileName).toFile();
    }
}
