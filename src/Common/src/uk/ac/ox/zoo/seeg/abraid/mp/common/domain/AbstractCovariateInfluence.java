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

    @ManyToOne
    @JoinColumn(name = "covariate_file_id", nullable = false)
    private CovariateFile covariateFile;

    @Column(name = "mean_influence")
    private Double meanInfluence;

    @Column(name = "upper_quantile")
    private Double upperQuantile;

    @Column(name = "lower_quantile")
    private Double lowerQuantile;

    public AbstractCovariateInfluence() {
    }

    public AbstractCovariateInfluence(CovariateFile covariate, AbstractCsvCovariateInfluence dto, ModelRun parentRun) {
        setModelRun(parentRun);
        setCovariateFile(covariate);
        setMeanInfluence(dto.getMeanInfluence());
        setUpperQuantile(dto.getUpperQuantile());
        setLowerQuantile(dto.getLowerQuantile());
    }

    public AbstractCovariateInfluence(CovariateFile covariate, Double meanInfluence) {
        setCovariateFile(covariate);
        setMeanInfluence(meanInfluence);
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

    public CovariateFile getCovariateFile() {
        return covariateFile;
    }

    public void setCovariateFile(CovariateFile covariateFile) {
        this.covariateFile = covariateFile;
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

        if (covariateFile != null ? !covariateFile.equals(that.covariateFile) : that.covariateFile != null)
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
        result = 31 * result + (covariateFile != null ? covariateFile.hashCode() : 0);
        result = 31 * result + (meanInfluence != null ? meanInfluence.hashCode() : 0);
        result = 31 * result + (upperQuantile != null ? upperQuantile.hashCode() : 0);
        result = 31 * result + (lowerQuantile != null ? lowerQuantile.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
