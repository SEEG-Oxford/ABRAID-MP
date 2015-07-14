package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateValueBin;

/**
 * A DTO to represent the count of covariate values falling within a defined range (a histogram entry).
 * Copyright (c) 2015 University of Oxford
 */
public class JsonCovariateValueBin {
    private double min;
    private double max;
    private int count;

    public JsonCovariateValueBin(CovariateValueBin domainObject) {
        this.min = domainObject.getMin();
        this.max = domainObject.getMax();
        this.count = domainObject.getCount();
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getCount() {
        return count;
    }
}
