package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
* Finds the unique values in a raster file.
* Copyright (c) 2015 University of Oxford
*/
public class ValuesRasterSummaryCollator implements RasterSummaryCollator<Collection<Double>> {
    private Collection<Double> values = new ArrayList<>();

    @Override
    public void addValue(double value) throws IOException {
        if (!values.contains(value)) {
            values.add(value);
        }
    }

    @Override
    public Collection<Double> getSummary() {
        return values;
    }
}
