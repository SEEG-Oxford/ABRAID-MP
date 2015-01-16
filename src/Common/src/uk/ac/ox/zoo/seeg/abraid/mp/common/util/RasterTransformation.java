package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

/**
 * An interface to define a raster transformation operation. This will likely involve iterating through the pixels in
 * the WritableRaster and setting new values where required for the operation being implemented.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface RasterTransformation {
    /**
     * Perform a raster transformation operation.
     * @param raster The raster which should be updated.
     * @param referenceRasters One or more rasters which may be compared against when updating the main raster.
     * @throws IOException thrown if the transformation can not be completed.
     */
    void transform(WritableRaster raster, Raster[] referenceRasters) throws IOException;
}
