package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public interface RasterTransformation {
    public void transform(WritableRaster raster, Raster[] referenceRasters);
}
