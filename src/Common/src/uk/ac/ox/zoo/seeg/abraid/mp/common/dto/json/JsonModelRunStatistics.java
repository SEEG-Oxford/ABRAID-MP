package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.List;

/**
 * The JSON DTO used to represent a set of summarising statistics across all submodels of a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunStatistics {
    private double deviance;
    private double devianceSd;

    private double rmse;
    private double rmseSd;

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

    private double threshold;
    private double thresholdSd;

    public JsonModelRunStatistics(List<SubmodelStatistic> submodelStatistics) {
        DescriptiveStatistics devianceStats = new DescriptiveStatistics();
        DescriptiveStatistics rmseStats = new DescriptiveStatistics();
        DescriptiveStatistics kappaStats = new DescriptiveStatistics();
        DescriptiveStatistics aucStats = new DescriptiveStatistics();
        DescriptiveStatistics sensStats = new DescriptiveStatistics();
        DescriptiveStatistics specStats = new DescriptiveStatistics();
        DescriptiveStatistics pccStats = new DescriptiveStatistics();
        DescriptiveStatistics thresholdStats = new DescriptiveStatistics();

        for (SubmodelStatistic submodelStatistic : submodelStatistics) {
            devianceStats.addValue(submodelStatistic.getDeviance());
            rmseStats.addValue(submodelStatistic.getRootMeanSquareError());
            kappaStats.addValue(submodelStatistic.getKappa());
            aucStats.addValue(submodelStatistic.getAreaUnderCurve());
            sensStats.addValue(submodelStatistic.getSensitivity());
            specStats.addValue(submodelStatistic.getSpecificity());
            pccStats.addValue(submodelStatistic.getProportionCorrectlyClassified());
            thresholdStats.addValue(submodelStatistic.getThreshold());
        }

        this.deviance = devianceStats.getMean();
        this.devianceSd = devianceStats.getStandardDeviation();
        this.rmse = rmseStats.getMean();
        this.rmseSd = rmseStats.getStandardDeviation();
        this.kappa = kappaStats.getMean();
        this.kappaSd = kappaStats.getStandardDeviation();
        this.auc = aucStats.getMean();
        this.aucSd = aucStats.getStandardDeviation();
        this.sens = sensStats.getMean();
        this.sensSd = sensStats.getStandardDeviation();
        this.spec = specStats.getMean();
        this.specSd = specStats.getStandardDeviation();
        this.pcc = pccStats.getMean();
        this.pccSd = pccStats.getStandardDeviation();
        this.threshold = thresholdStats.getMean();
        this.thresholdSd = thresholdStats.getStandardDeviation();
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

    public double getThreshold() {
        return threshold;
    }

    public double getDevianceSd() {
        return devianceSd;
    }

    public double getRmseSd() {
        return rmseSd;
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

    public double getThresholdSd() {
        return thresholdSd;
    }
}
