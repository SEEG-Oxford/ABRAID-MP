package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import java.io.IOException;

/**
 * Defines a raster summary operation. Used by RasterUtils.summarizeRaster.
 * addValue will be called for each pixel in the raster, followed by getSummary to get the result.
 * @param <TResult> The type of summary data returned by the operation.
 * Copyright (c) 2015 University of Oxford
 */
public interface RasterSummaryCollator<TResult> {
    /**
     * Add a single pixel value to the raster summary. Will be called once for each non-NODATA pixel in the raster.
     * @param value The pixel value.
     * @throws IOException Thrown if the operation fails.
     */
    void addValue(double value) throws IOException;

    /**
     * Returns the result of the summary operation. Will be called after all calls to addValue.
     * @return The summary result.
     * @throws IOException Thrown if the operation fails.
     */
    TResult getSummary() throws IOException;
}
