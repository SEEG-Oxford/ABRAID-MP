package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

/**
 * The JSON DTO used to represent a set of summarising statistics across all submodels of a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunStatistics {
    private Double deviance;
    private Double devianceSd;

    private Double rmse;
    private Double rmseSd;

    private Double kappa;
    private Double kappaSd;

    private Double auc;
    private Double aucSd;

    private Double sens;
    private Double sensSd;

    private Double spec;
    private Double specSd;

    private Double pcc;
    private Double pccSd;

    private Double threshold;
    private Double thresholdSd;

    public JsonModelRunStatistics() {
    }

    public void setDeviance(Double deviance) {
        this.deviance = deviance;
    }

    public void setDevianceSd(Double devianceSd) {
        this.devianceSd = devianceSd;
    }

    public void setRmse(Double rmse) {
        this.rmse = rmse;
    }

    public void setRmseSd(Double rmseSd) {
        this.rmseSd = rmseSd;
    }

    public void setKappa(Double kappa) {
        this.kappa = kappa;
    }

    public void setKappaSd(Double kappaSd) {
        this.kappaSd = kappaSd;
    }

    public void setAuc(Double auc) {
        this.auc = auc;
    }

    public void setAucSd(Double aucSd) {
        this.aucSd = aucSd;
    }

    public void setSens(Double sens) {
        this.sens = sens;
    }

    public void setSensSd(Double sensSd) {
        this.sensSd = sensSd;
    }

    public void setSpec(Double spec) {
        this.spec = spec;
    }

    public void setSpecSd(Double specSd) {
        this.specSd = specSd;
    }

    public void setPcc(Double pcc) {
        this.pcc = pcc;
    }

    public void setPccSd(Double pccSd) {
        this.pccSd = pccSd;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public void setThresholdSd(Double thresholdSd) {
        this.thresholdSd = thresholdSd;
    }

    public Double getDeviance() {
        return deviance;
    }

    public Double getRmse() {
        return rmse;
    }

    public Double getKappa() {
        return kappa;
    }

    public Double getAuc() {
        return auc;
    }

    public Double getSens() {
        return sens;
    }

    public Double getSpec() {
        return spec;
    }

    public Double getPcc() {
        return pcc;
    }

    public Double getThreshold() {
        return threshold;
    }

    public Double getDevianceSd() {
        return devianceSd;
    }

    public Double getRmseSd() {
        return rmseSd;
    }

    public Double getKappaSd() {
        return kappaSd;
    }

    public Double getAucSd() {
        return aucSd;
    }

    public Double getSensSd() {
        return sensSd;
    }

    public Double getSpecSd() {
        return specSd;
    }

    public Double getPccSd() {
        return pccSd;
    }

    public Double getThresholdSd() {
        return thresholdSd;
    }
}
