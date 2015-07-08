package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

import java.util.Collection;
import java.util.List;

/**
 * A DTO to represent the covariate associated with a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateInfluence extends AbstractJsonCovariateInfluence {
    private String info;
    private boolean discrete;
    private List<JsonEffectCurveCovariateInfluence> effectCurve;

    public JsonCovariateInfluence(CovariateInfluence covariateInfluence,
                                  List<JsonEffectCurveCovariateInfluence> effectCurve) {
        super(covariateInfluence);
        this.info = covariateInfluence.getCovariateFile().getInfo();
        this.discrete = covariateInfluence.getCovariateFile().getDiscrete();
        this.effectCurve = effectCurve;
    }

    public String getInfo() {
        return info;
    }

    public Collection<JsonEffectCurveCovariateInfluence> getEffectCurve() {
        return effectCurve;
    }

    public boolean getDiscrete() {
        return discrete;
    }
}
