package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.AbstractCsvCovariateInfluence;

import javax.persistence.*;

/**
 * Represents the common fields associated with the influence of a covariate file on a model run.
 * Copyright (c) 2014 University of Oxford
 */
@MappedSuperclass
public class AbstractCovariateInfluence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "model_run_id", nullable = false)
    private ModelRun modelRun;

    @Column(name = "covariate_name")
    private String covariateName;

    @Column(name = "covariate_display_name")
    private String covariateDisplayName;

    @Column(name = "mean_influence")
    private Double meanInfluence;

    @Column(name = "upper_quantile")
    private Double upperQuantile;

    @Column(name = "lower_quantile")
    private Double lowerQuantile;

    public AbstractCovariateInfluence() {
    }

    public AbstractCovariateInfluence(AbstractCsvCovariateInfluence dto, ModelRun parentRun) {
        setModelRun(parentRun);
        setCovariateName(dto.getCovariateName());
        setCovariateDisplayName(dto.getCovariateDisplayName());
        setMeanInfluence(dto.getMeanInfluence());
        setUpperQuantile(dto.getUpperQuantile());
        setLowerQuantile(dto.getLowerQuantile());
    }

    public AbstractCovariateInfluence(String name, Double meanInfluence) {
        // NB. Should be changed to covariateDisplayName
        this.covariateName = name;
        this.meanInfluence = meanInfluence;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModelRun getModelRun() {
        return modelRun;
    }

    public void setModelRun(ModelRun modelRun) {
        this.modelRun = modelRun;
    }

    public String getCovariateName() {
        return covariateName;
    }

    public void setCovariateName(String covariateName) {
        this.covariateName = covariateName;
    }

    public String getCovariateDisplayName() {
        return covariateDisplayName;
    }

    public void setCovariateDisplayName(String covariateDisplayName) {
        this.covariateDisplayName = covariateDisplayName;
    }

    public Double getMeanInfluence() {
        return meanInfluence;
    }

    public void setMeanInfluence(Double meanInfluence) {
        this.meanInfluence = meanInfluence;
    }

    public Double getUpperQuantile() {
        return upperQuantile;
    }

    public void setUpperQuantile(Double upperQuantile) {
        this.upperQuantile = upperQuantile;
    }

    public Double getLowerQuantile() {
        return lowerQuantile;
    }

    public void setLowerQuantile(Double lowerQuantile) {
        this.lowerQuantile = lowerQuantile;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCovariateInfluence that = (AbstractCovariateInfluence) o;

        if (covariateDisplayName != null ? !covariateDisplayName.equals(that.covariateDisplayName) : that.covariateDisplayName != null)
            return false;
        if (covariateName != null ? !covariateName.equals(that.covariateName) : that.covariateName != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lowerQuantile != null ? !lowerQuantile.equals(that.lowerQuantile) : that.lowerQuantile != null)
            return false;
        if (meanInfluence != null ? !meanInfluence.equals(that.meanInfluence) : that.meanInfluence != null)
            return false;
        if (modelRun != null ? !modelRun.equals(that.modelRun) : that.modelRun != null) return false;
        if (upperQuantile != null ? !upperQuantile.equals(that.upperQuantile) : that.upperQuantile != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (modelRun != null ? modelRun.hashCode() : 0);
        result = 31 * result + (covariateName != null ? covariateName.hashCode() : 0);
        result = 31 * result + (covariateDisplayName != null ? covariateDisplayName.hashCode() : 0);
        result = 31 * result + (meanInfluence != null ? meanInfluence.hashCode() : 0);
        result = 31 * result + (upperQuantile != null ? upperQuantile.hashCode() : 0);
        result = 31 * result + (lowerQuantile != null ? lowerQuantile.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
