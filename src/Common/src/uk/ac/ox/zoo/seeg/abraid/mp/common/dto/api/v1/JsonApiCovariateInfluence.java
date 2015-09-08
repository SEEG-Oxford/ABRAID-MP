package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

/**
 * Represents a covariate in the v1 JSON API.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiCovariateInfluence {
    private String name;
    private Double meanInfluence;

    public JsonApiCovariateInfluence(CovariateInfluence covariateInfluence) {
        this.name = covariateInfluence.getCovariateFile().getName();
        this.meanInfluence = covariateInfluence.getMeanInfluence();
    }

    public String getName() {
        return name;
    }

    public Double getMeanInfluence() {
        return meanInfluence;
    }
}
