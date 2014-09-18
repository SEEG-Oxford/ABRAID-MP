package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.ParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * CSV DTO which represents the influence of a covariate file on a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvCovariateInfluence {
    private String covariateName;
    private String covariateDisplayName;
    private Double meanInfluence;
    private Double upperQuantile;
    private Double lowerQuantile;

    public CsvCovariateInfluence() {
    }

    public String getCovariateName() {
        return covariateName;
    }

    //@JsonProperty("")
    public void setCovariateName(String covariateName) {
        this.covariateName = covariateName;
    }

    public String getCovariateDisplayName() {
        return covariateDisplayName;
    }

    //@JsonProperty("display")
    public void setCovariateDisplayName(String covariateDisplayName) {
        this.covariateDisplayName = covariateDisplayName;
    }

    public Double getMeanInfluence() {
        return meanInfluence;
    }

    //@JsonProperty("mean")
    public void setMeanInfluence(Double meanInfluence) {
        this.meanInfluence = meanInfluence;
    }

    public Double getUpperQuantile() {
        return upperQuantile;
    }

    //@JsonProperty("97.5")
    public void setUpperQuantile(Double upperQuantile) {
        this.upperQuantile = upperQuantile;
    }

    public Double getLowerQuantile() {
        return lowerQuantile;
    }

    //@JsonProperty("2.5%")
    public void setLowerQuantile(Double lowerQuantile) {
        this.lowerQuantile = lowerQuantile;
    }

    /**
     * Parses a collection of CsvCovariateInfluence entries from a csv string (header row expected).
     * @param csv The csv string.
     * @return A collection of CsvCovariateInfluence entries.
     * @throws IOException Thrown if the parsing fails.
     */
    public static List<CsvCovariateInfluence> readFromCSV(String csv) throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .setSkipFirstDataRow(true)
                .addColumn("covariateName")
                .addColumn("meanInfluence", CsvSchema.ColumnType.NUMBER)
                .addColumn("lowerQuantile", CsvSchema.ColumnType.NUMBER)
                .addColumn("upperQuantile", CsvSchema.ColumnType.NUMBER)
                .build();

        return ParseUtils.readFromCsv(csv, CsvCovariateInfluence.class, schema);
    }
}
