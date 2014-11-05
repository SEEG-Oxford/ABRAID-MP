package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvSubmodelStatistic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunStatistics;

import javax.persistence.*;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Represents the validation statistics for a submodel in a model run.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "submodel_statistic")
@NamedQueries({
        @NamedQuery(
                name = "getSubmodelStatisticsForModelRun",
                query = "from SubmodelStatistic where modelRun=:modelRun"
        )
})
public class SubmodelStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "model_run_id", nullable = false)
    private ModelRun modelRun;

    // "deviance"
    private Double deviance;

    // "rmse"
    @Column(name = "root_mean_square_error")
    private Double rootMeanSquareError;

    // "kappa"
    private Double kappa;

    // "auc"
    @Column(name = "area_under_curve")
    private Double areaUnderCurve;

    // "sens"
    private Double sensitivity;

    // "spec"
    private Double specificity;

    // "pcc"
    @Column(name = "proportion_correctly_classified")
    private Double proportionCorrectlyClassified;

    // "kappa_sd"
    @Column(name = "kappa_sd")
    private Double kappaStandardDeviation;

    // "auc_sd"
    @Column(name = "area_under_curve_sd")
    private Double areaUnderCurveStandardDeviation;

    // "sens_sd"
    @Column(name = "sensitivity_sd")
    private Double sensitivityStandardDeviation;

    // "spec_sd"
    @Column(name = "specificity_sd")
    private Double specificityStandardDeviation;

    // "pcc_sd"
    @Column(name = "proportion_correctly_classified_sd")
    private Double proportionCorrectlyClassifiedStandardDeviation;

    //"thresh"
    private Double threshold;

    public SubmodelStatistic() {
    }

    public SubmodelStatistic(Double deviance, Double rootMeanSquareError, Double kappa, Double areaUnderCurve,
                             Double sensitivity, Double specificity, Double proportionCorrectlyClassified,
                             Double threshold) {
        setDeviance(deviance);
        setRootMeanSquareError(rootMeanSquareError);
        setKappa(kappa);
        setAreaUnderCurve(areaUnderCurve);
        setSensitivity(sensitivity);
        setSpecificity(specificity);
        setProportionCorrectlyClassified(proportionCorrectlyClassified);
        setThreshold(threshold);
    }

    public SubmodelStatistic(CsvSubmodelStatistic dto, ModelRun parentRun) {
        setModelRun(parentRun);
        setDeviance(dto.getDeviance());
        setRootMeanSquareError(dto.getRootMeanSquareError());
        setKappa(dto.getKappa());
        setAreaUnderCurve(dto.getAreaUnderCurve());
        setSensitivity(dto.getSensitivity());
        setSpecificity(dto.getSpecificity());
        setProportionCorrectlyClassified(dto.getProportionCorrectlyClassified());
        setKappaStandardDeviation(dto.getKappaStandardDeviation());
        setAreaUnderCurveStandardDeviation(dto.getAreaUnderCurveStandardDeviation());
        setSensitivityStandardDeviation(dto.getSensitivityStandardDeviation());
        setSpecificityStandardDeviation(dto.getSpecificityStandardDeviation());
        setProportionCorrectlyClassifiedStandardDeviation(dto.getProportionCorrectlyClassifiedStandardDeviation());
        setThreshold(dto.getThreshold());
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

    public Double getDeviance() {
        return deviance;
    }

    public void setDeviance(Double deviance) {
        this.deviance = deviance;
    }

    public Double getRootMeanSquareError() {
        return rootMeanSquareError;
    }

    public void setRootMeanSquareError(Double rootMeanSquareError) {
        this.rootMeanSquareError = rootMeanSquareError;
    }

    public Double getKappa() {
        return kappa;
    }

    public void setKappa(Double kappa) {
        this.kappa = kappa;
    }

    public Double getAreaUnderCurve() {
        return areaUnderCurve;
    }

    public void setAreaUnderCurve(Double areaUnderCurve) {
        this.areaUnderCurve = areaUnderCurve;
    }

    public Double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Double getSpecificity() {
        return specificity;
    }

    public void setSpecificity(Double specificity) {
        this.specificity = specificity;
    }

    public Double getProportionCorrectlyClassified() {
        return proportionCorrectlyClassified;
    }

    public void setProportionCorrectlyClassified(Double proportionCorrectlyClassified) {
        this.proportionCorrectlyClassified = proportionCorrectlyClassified;
    }

    public Double getKappaStandardDeviation() {
        return kappaStandardDeviation;
    }

    public void setKappaStandardDeviation(Double kappaStandardDeviation) {
        this.kappaStandardDeviation = kappaStandardDeviation;
    }

    public Double getAreaUnderCurveStandardDeviation() {
        return areaUnderCurveStandardDeviation;
    }

    public void setAreaUnderCurveStandardDeviation(Double areaUnderCurveStandardDeviation) {
        this.areaUnderCurveStandardDeviation = areaUnderCurveStandardDeviation;
    }

    public Double getSensitivityStandardDeviation() {
        return sensitivityStandardDeviation;
    }

    public void setSensitivityStandardDeviation(Double sensitivityStandardDeviation) {
        this.sensitivityStandardDeviation = sensitivityStandardDeviation;
    }

    public Double getSpecificityStandardDeviation() {
        return specificityStandardDeviation;
    }

    public void setSpecificityStandardDeviation(Double specificityStandardDeviation) {
        this.specificityStandardDeviation = specificityStandardDeviation;
    }

    public Double getProportionCorrectlyClassifiedStandardDeviation() {
        return proportionCorrectlyClassifiedStandardDeviation;
    }

    public void setProportionCorrectlyClassifiedStandardDeviation(
            Double proportionCorrectlyClassifiedStandardDeviation) {
        this.proportionCorrectlyClassifiedStandardDeviation = proportionCorrectlyClassifiedStandardDeviation;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubmodelStatistic that = (SubmodelStatistic) o;

        if (areaUnderCurve != null ? !areaUnderCurve.equals(that.areaUnderCurve) : that.areaUnderCurve != null)
            return false;
        if (areaUnderCurveStandardDeviation != null ? !areaUnderCurveStandardDeviation.equals(that.areaUnderCurveStandardDeviation) : that.areaUnderCurveStandardDeviation != null)
            return false;
        if (deviance != null ? !deviance.equals(that.deviance) : that.deviance != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (kappa != null ? !kappa.equals(that.kappa) : that.kappa != null) return false;
        if (kappaStandardDeviation != null ? !kappaStandardDeviation.equals(that.kappaStandardDeviation) : that.kappaStandardDeviation != null)
            return false;
        if (modelRun != null ? !modelRun.equals(that.modelRun) : that.modelRun != null) return false;
        if (proportionCorrectlyClassified != null ? !proportionCorrectlyClassified.equals(that.proportionCorrectlyClassified) : that.proportionCorrectlyClassified != null)
            return false;
        if (proportionCorrectlyClassifiedStandardDeviation != null ? !proportionCorrectlyClassifiedStandardDeviation.equals(that.proportionCorrectlyClassifiedStandardDeviation) : that.proportionCorrectlyClassifiedStandardDeviation != null)
            return false;
        if (rootMeanSquareError != null ? !rootMeanSquareError.equals(that.rootMeanSquareError) : that.rootMeanSquareError != null)
            return false;
        if (sensitivity != null ? !sensitivity.equals(that.sensitivity) : that.sensitivity != null) return false;
        if (sensitivityStandardDeviation != null ? !sensitivityStandardDeviation.equals(that.sensitivityStandardDeviation) : that.sensitivityStandardDeviation != null)
            return false;
        if (specificity != null ? !specificity.equals(that.specificity) : that.specificity != null) return false;
        if (specificityStandardDeviation != null ? !specificityStandardDeviation.equals(that.specificityStandardDeviation) : that.specificityStandardDeviation != null)
            return false;
        if (threshold != null ? !threshold.equals(that.threshold) : that.threshold != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (modelRun != null ? modelRun.hashCode() : 0);
        result = 31 * result + (deviance != null ? deviance.hashCode() : 0);
        result = 31 * result + (rootMeanSquareError != null ? rootMeanSquareError.hashCode() : 0);
        result = 31 * result + (kappa != null ? kappa.hashCode() : 0);
        result = 31 * result + (areaUnderCurve != null ? areaUnderCurve.hashCode() : 0);
        result = 31 * result + (sensitivity != null ? sensitivity.hashCode() : 0);
        result = 31 * result + (specificity != null ? specificity.hashCode() : 0);
        result = 31 * result + (proportionCorrectlyClassified != null ? proportionCorrectlyClassified.hashCode() : 0);
        result = 31 * result + (kappaStandardDeviation != null ? kappaStandardDeviation.hashCode() : 0);
        result = 31 * result + (areaUnderCurveStandardDeviation != null ? areaUnderCurveStandardDeviation.hashCode() : 0);
        result = 31 * result + (sensitivityStandardDeviation != null ? sensitivityStandardDeviation.hashCode() : 0);
        result = 31 * result + (specificityStandardDeviation != null ? specificityStandardDeviation.hashCode() : 0);
        result = 31 * result + (proportionCorrectlyClassifiedStandardDeviation != null ? proportionCorrectlyClassifiedStandardDeviation.hashCode() : 0);
        result = 31 * result + (threshold != null ? threshold.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON

    /**
     * Gets the set of summarising statistics across all submodels of a model run.
     * @param submodelStatistics The list of submodel statistics.
     * @return The DTO representing the set of summarising statistics.
     */
    public static JsonModelRunStatistics summarise(List<SubmodelStatistic> submodelStatistics) {
        JsonModelRunStatistics json = new JsonModelRunStatistics();

        List<SubmodelStatistic> completeSubmodels =  filter(
                having(on(SubmodelStatistic.class).containsAllRequiredPropertiesForSummarising()),
                filter(notNullValue(), submodelStatistics));

        if (!completeSubmodels.isEmpty()) {
            DescriptiveStatistics devianceStats = new DescriptiveStatistics();
            DescriptiveStatistics rmseStats = new DescriptiveStatistics();
            DescriptiveStatistics kappaStats = new DescriptiveStatistics();
            DescriptiveStatistics aucStats = new DescriptiveStatistics();
            DescriptiveStatistics sensStats = new DescriptiveStatistics();
            DescriptiveStatistics specStats = new DescriptiveStatistics();
            DescriptiveStatistics pccStats = new DescriptiveStatistics();
            DescriptiveStatistics thresholdStats = new DescriptiveStatistics();

            for (SubmodelStatistic submodelStatistic : completeSubmodels) {
                devianceStats.addValue(submodelStatistic.getDeviance());
                rmseStats.addValue(submodelStatistic.getRootMeanSquareError());
                kappaStats.addValue(submodelStatistic.getKappa());
                aucStats.addValue(submodelStatistic.getAreaUnderCurve());
                sensStats.addValue(submodelStatistic.getSensitivity());
                specStats.addValue(submodelStatistic.getSpecificity());
                pccStats.addValue(submodelStatistic.getProportionCorrectlyClassified());
                thresholdStats.addValue(submodelStatistic.getThreshold());
            }

            json.setDeviance(devianceStats.getMean());
            json.setDevianceSd(devianceStats.getStandardDeviation());
            json.setRmse(rmseStats.getMean());
            json.setRmseSd(rmseStats.getStandardDeviation());
            json.setKappa(kappaStats.getMean());
            json.setKappaSd(kappaStats.getStandardDeviation());
            json.setAuc(aucStats.getMean());
            json.setAucSd(aucStats.getStandardDeviation());
            json.setSens(sensStats.getMean());
            json.setSensSd(sensStats.getStandardDeviation());
            json.setSpec(specStats.getMean());
            json.setSpecSd(specStats.getStandardDeviation());
            json.setPcc(pccStats.getMean());
            json.setPccSd(pccStats.getStandardDeviation());
            json.setThreshold(thresholdStats.getMean());
            json.setThresholdSd(thresholdStats.getStandardDeviation());
        }

        return json;
    }

    protected boolean containsAllRequiredPropertiesForSummarising() {
        return
                deviance != null &&
                rootMeanSquareError != null &&
                kappa != null &&
                areaUnderCurve != null &&
                sensitivity != null &&
                specificity != null &&
                proportionCorrectlyClassified != null &&
                threshold != null;
    }
}
