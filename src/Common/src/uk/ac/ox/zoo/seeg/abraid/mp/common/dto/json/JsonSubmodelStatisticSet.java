package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

/**
 * The JSON DTO used to represent a set of statistics for one submodel of a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonSubmodelStatisticSet {
    private double deviance;

    private double rmse;

    private double kappa;
    private double kappaSd;

    private double auc;
    private double aucSd;

    private double sens;
    private double sensSd;

    private double spec;
    private double specSd;

    private double pcc;
    private double pccSd;

    private double thresh;

    public JsonSubmodelStatisticSet(SubmodelStatistic submodelStatistic) {
        this.deviance = submodelStatistic.getDeviance();
        this.rmse = submodelStatistic.getRootMeanSquareError();
        this.kappa = submodelStatistic.getKappa();
        this.kappaSd = submodelStatistic.getKappaStandardDeviation();
        this.auc = submodelStatistic.getAreaUnderCurve();
        this.aucSd = submodelStatistic.getAreaUnderCurveStandardDeviation();
        this.sens = submodelStatistic.getSensitivity();
        this.sensSd = submodelStatistic.getSensitivityStandardDeviation();
        this.spec = submodelStatistic.getSpecificity();
        this.specSd = submodelStatistic.getSpecificityStandardDeviation();
        this.pcc = submodelStatistic.getProportionCorrectlyClassified();
        this.pccSd = submodelStatistic.getProportionCorrectlyClassifiedStandardDeviation();
        this.thresh = submodelStatistic.getThreshold();
    }

    public double getDeviance() {
        return deviance;
    }

    public double getRmse() {
        return rmse;
    }

    public double getKappa() {
        return kappa;
    }

    public double getAuc() {
        return auc;
    }

    public double getSens() {
        return sens;
    }

    public double getSpec() {
        return spec;
    }

    public double getPcc() {
        return pcc;
    }

    public double getKappaSd() {
        return kappaSd;
    }

    public double getAucSd() {
        return aucSd;
    }

    public double getSensSd() {
        return sensSd;
    }

    public double getSpecSd() {
        return specSd;
    }

    public double getPccSd() {
        return pccSd;
    }

    public double getThresh() {
        return thresh;
    }
}
