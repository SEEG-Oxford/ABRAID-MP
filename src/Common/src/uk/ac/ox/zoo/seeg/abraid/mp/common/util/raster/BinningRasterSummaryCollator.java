package uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster;

import org.apache.commons.lang.math.DoubleRange;

import java.io.IOException;
import java.util.*;

/**
* Counts the number of raster pixels that fall within a set of predefined bins.
* Copyright (c) 2015 University of Oxford
*/
public class BinningRasterSummaryCollator implements RasterSummaryCollator<Map<DoubleRange, Integer>> {
    private Map<DoubleRange, Integer> bins;
    private List<DoubleRange> keys;

    public BinningRasterSummaryCollator(List<DoubleRange> bins) {
        this.bins = new HashMap<>();
        List<DoubleRange> unsortedKeys = new ArrayList<>();
        for (DoubleRange bin : bins) {
            this.bins.put(bin, 0);
            unsortedKeys.add(bin);
        }

        Collections.sort(unsortedKeys, new Comparator<DoubleRange>() {
            @Override
            public int compare(DoubleRange o1, DoubleRange o2) {
                return Double.compare(o1.getMinimumDouble(), o2.getMinimumDouble());
            }
        });
        this.keys = unsortedKeys;
    }

    @Override
    public void addValue(double value) throws IOException {
        for (int i = 0; i < this.keys.size(); i++) {
            DoubleRange range = this.keys.get(i);
            if (range.containsDouble(value)) {
                this.bins.put(range, this.bins.get(range) + 1);
                return;
            }
        }
        throw new IOException();
    }

    @Override
    public Map<DoubleRange, Integer> getSummary() {
        return this.bins;
    }
}
