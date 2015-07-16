package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.ParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * CSV DTO which represents the influence of a covariate file on a model run through its full range of values,
 * for plotting in an effect curve.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvEffectCurveCovariateInfluence extends AbstractCsvCovariateInfluence {
    private Double covariateValue;

    /**
     * Parses a collection of CsvCovariateInfluence entries from a csv string (header row expected).
     * @param csv The csv string.
     * @return A collection of CsvCovariateInfluence entries.
     * @throws java.io.IOException Thrown if the parsing fails.
     */
    public static List<CsvEffectCurveCovariateInfluence> readFromCSV(String csv) throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .setSkipFirstDataRow(true)
                .addColumn("index")
                .addColumn("covariateFilePath")
                .addColumn("covariateValue")
                .addColumn("meanInfluence", CsvSchema.ColumnType.NUMBER)
                .addColumn("lowerQuantile", CsvSchema.ColumnType.NUMBER)
                .addColumn("upperQuantile", CsvSchema.ColumnType.NUMBER)
                .build();

        return ParseUtils.readFromCsv(csv, CsvEffectCurveCovariateInfluence.class, schema);
    }

    public Double getCovariateValue() {
        return covariateValue;
    }

    public void setCovariateValue(Double covariateValue) {
        this.covariateValue = covariateValue;
    }
}
