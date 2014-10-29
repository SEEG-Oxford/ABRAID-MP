package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.List;

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
