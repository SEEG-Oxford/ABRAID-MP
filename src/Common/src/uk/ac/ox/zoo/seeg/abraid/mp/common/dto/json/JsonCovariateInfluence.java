package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

/**
 * A DTO to represent the covariate associated with a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateInfluence {
    private String name;
    private double meanInfluence;

    public JsonCovariateInfluence(CovariateInfluence covariateInfluence) {
        // NB. Should use CovariateDisplayName when it is returned from the R model.
        this.name = covariateInfluence.getCovariateName();
        this.meanInfluence = covariateInfluence.getMeanInfluence();
    }

    public String getName() {
        return name;
    }

    public double getMeanInfluence() {
        return meanInfluence;
    }
}
