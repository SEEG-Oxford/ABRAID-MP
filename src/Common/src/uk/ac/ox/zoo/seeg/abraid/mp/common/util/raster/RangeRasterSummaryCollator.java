package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import org.apache.commons.lang.math.DoubleRange;

import java.io.IOException;

/**
* Finds the min and max values in a raster file.
* Copyright (c) 2015 University of Oxford
*/
public class RangeRasterSummaryCollator implements RasterSummaryCollator<DoubleRange> {
    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    @Override
    public void addValue(double value) throws IOException {
        if (value < min) {
            min = value;
        }
        if (value > max) {
            max = value;
        }
    }

    @Override
    public DoubleRange getSummary() {
        return new DoubleRange(min, max);
    }
}
