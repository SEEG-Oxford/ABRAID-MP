package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AbstractCovariateInfluence;

/**
 * A DTO to represent common fields associated with the influence of a covariate file on a model run.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractJsonCovariateInfluence {
    private String name;
    private Double meanInfluence;

    public AbstractJsonCovariateInfluence(AbstractCovariateInfluence covariateInfluence) {
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
