package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

/**
 * CSV DTO which represents the common fields associated with the influence of a covariate file on a model run.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractCsvCovariateInfluence {
    private String covariateFilePath;
    private String covariateDisplayName;
    private Double meanInfluence;
    private Double upperQuantile;
    private Double lowerQuantile;

    public AbstractCsvCovariateInfluence() {
    }

    public String getCovariateFilePath() {
        return covariateFilePath;
    }

    public void setCovariateFilePath(String covariateFilePath) {
        this.covariateFilePath = covariateFilePath;
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
}
