package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV DTO which represents the validation statistics for a submodel in a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvSubmodelStatistic {
    private Double deviance; // "deviance"
    private Double rootMeanSquareError; // "rmse"
    private Double kappa; // "kappa"
    private Double areaUnderCurve; // "auc"
    private Double sensitivity; // "sens"
    private Double specificity; // "spec"
    private Double proportionCorrectlyClassified; // "pcc"

    private Double kappaStandardDeviation; // "kappa_sd"
    private Double areaUnderCurveStandardDeviation; // "auc_sd"
    private Double sensitivityStandardDeviation; // "sens_sd"
    private Double specificityStandardDeviation; // "spec_sd"
    private Double proportionCorrectlyClassifiedStandardDeviation; // "pcc_sd"

    private Double threshold; //"thresh"

    public CsvSubmodelStatistic() {
    }

    public Double getDeviance() {
        return deviance;
    }

    @JsonProperty("deviance")
    public void setDeviance(Double deviance) {
        this.deviance = deviance;
    }

    public Double getRootMeanSquareError() {
        return rootMeanSquareError;
    }

    @JsonProperty("rmse")
    public void setRootMeanSquareError(Double rootMeanSquareError) {
        this.rootMeanSquareError = rootMeanSquareError;
    }

    public Double getKappa() {
        return kappa;
    }

    @JsonProperty("kappa")
    public void setKappa(Double kappa) {
        this.kappa = kappa;
    }

    public Double getAreaUnderCurve() {
        return areaUnderCurve;
    }

    @JsonProperty("auc")
    public void setAreaUnderCurve(Double areaUnderCurve) {
        this.areaUnderCurve = areaUnderCurve;
    }

    public Double getSensitivity() {
        return sensitivity;
    }

    @JsonProperty("sens")
    public void setSensitivity(Double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Double getSpecificity() {
        return specificity;
    }

    @JsonProperty("spec")
    public void setSpecificity(Double specificity) {
        this.specificity = specificity;
    }

    public Double getProportionCorrectlyClassified() {
        return proportionCorrectlyClassified;
    }

    @JsonProperty("pcc")
    public void setProportionCorrectlyClassified(Double proportionCorrectlyClassified) {
        this.proportionCorrectlyClassified = proportionCorrectlyClassified;
    }

    public Double getKappaStandardDeviation() {
        return kappaStandardDeviation;
    }

    @JsonProperty("kappa_sd")
    public void setKappaStandardDeviation(Double kappaStandardDeviation) {
        this.kappaStandardDeviation = kappaStandardDeviation;
    }

    public Double getAreaUnderCurveStandardDeviation() {
        return areaUnderCurveStandardDeviation;
    }

    @JsonProperty("auc_sd")
    public void setAreaUnderCurveStandardDeviation(Double areaUnderCurveStandardDeviation) {
        this.areaUnderCurveStandardDeviation = areaUnderCurveStandardDeviation;
    }

    public Double getSensitivityStandardDeviation() {
        return sensitivityStandardDeviation;
    }

    @JsonProperty("sens_sd")
    public void setSensitivityStandardDeviation(Double sensitivityStandardDeviation) {
        this.sensitivityStandardDeviation = sensitivityStandardDeviation;
    }

    public Double getSpecificityStandardDeviation() {
        return specificityStandardDeviation;
    }

    @JsonProperty("spec_sd")
    public void setSpecificityStandardDeviation(Double specificityStandardDeviation) {
        this.specificityStandardDeviation = specificityStandardDeviation;
    }

    public Double getProportionCorrectlyClassifiedStandardDeviation() {
        return proportionCorrectlyClassifiedStandardDeviation;
    }

    @JsonProperty("pcc_sd")
    public void setProportionCorrectlyClassifiedStandardDeviation(Double proportionCorrectlyClassifiedStandardDeviation) {
        this.proportionCorrectlyClassifiedStandardDeviation = proportionCorrectlyClassifiedStandardDeviation;
    }

    public Double getThreshold() {
        return threshold;
    }

    @JsonProperty("thresh")
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    /**
     * Parses a collection of CsvSubmodelStatistic entries from a csv string (header row expected).
     * @param csv The csv string.
     * @return A collection of CsvSubmodelStatistic entries.
     * @throws IOException Thrown if the parsing fails.
     */
    public static List<CsvSubmodelStatistic> readFromCSV(String csv) throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader reader = new CsvMapper().reader(CsvSubmodelStatistic.class).with(schema);
        MappingIterator<CsvSubmodelStatistic> iterator = reader.readValues(csv);
        ArrayList<CsvSubmodelStatistic> results = new ArrayList<>();
        while (iterator.hasNext()) {
            results.add(iterator.next());
        }

        return results;
    }
}
