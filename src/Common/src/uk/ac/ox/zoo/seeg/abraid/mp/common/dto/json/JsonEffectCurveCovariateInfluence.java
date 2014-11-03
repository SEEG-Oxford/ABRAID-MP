package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;

/**
 * A DTO to represent the covariate associated with a model run.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({"name", "covariateValue", "meanInfluence", "lowerQuantile", "upperQuantile"})
public class JsonEffectCurveCovariateInfluence extends AbstractJsonCovariateInfluence {
    private Double covariateValue;
    private Double lowerQuantile;
    private Double upperQuantile;

    public JsonEffectCurveCovariateInfluence(EffectCurveCovariateInfluence covariateInfluence) {
        super(covariateInfluence);
        setCovariateValue(covariateInfluence.getCovariateValue());
        setLowerQuantile(covariateInfluence.getLowerQuantile());
        setUpperQuantile(covariateInfluence.getUpperQuantile());
    }

    public Double getCovariateValue() {
        return covariateValue;
    }

    public void setCovariateValue(Double covariateValue) {
        this.covariateValue = covariateValue;
    }

    public Double getLowerQuantile() {
        return lowerQuantile;
    }

    public void setLowerQuantile(Double lowerQuantile) {
        this.lowerQuantile = lowerQuantile;
    }

    public Double getUpperQuantile() {
        return upperQuantile;
    }

    public void setUpperQuantile(Double upperQuantile) {
        this.upperQuantile = upperQuantile;
    }
}
