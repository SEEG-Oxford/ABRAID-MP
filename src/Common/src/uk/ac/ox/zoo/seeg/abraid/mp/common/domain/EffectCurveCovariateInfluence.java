package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.*;

import javax.persistence.*;

/**
 * Represents the influence of a covariate file on a model run through its full range of values,
 * for plotting in an effect curve.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "effect_curve_covariate_influence")
@NamedQueries({
        @NamedQuery(
                name = "getEffectCurveCovariateInfluencesForModelRun",
                query = "from EffectCurveCovariateInfluence where modelRun=:modelRun"
        )
})
public class EffectCurveCovariateInfluence extends AbstractCovariateInfluence {
    @Column(name = "covariate_value")
    private Double covariateValue;

    public EffectCurveCovariateInfluence() {
        super();
    }

    public EffectCurveCovariateInfluence(CsvEffectCurveCovariateInfluence csv, ModelRun modelRun) {
        super(csv, modelRun);
    }

    public Double getCovariateValue() {
        return covariateValue;
    }

    public void setCovariateValue(Double covariateValue) {
        this.covariateValue = covariateValue;
    }


    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EffectCurveCovariateInfluence that = (EffectCurveCovariateInfluence) o;

        if (covariateValue != null ? !covariateValue.equals(that.covariateValue) : that.covariateValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (covariateValue != null ? covariateValue.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
