package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CsvEffectCurveCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvEffectCurveCovariateInfluenceTest {
    @Test
    public void readFromCSV() throws Exception {
        String csv =
            "\"\",\"\",\"covariate\",\"mean\",\"2.5%\",\"97.5%\"\n" +
            "\"1\",\"covariates/upr_u.tif\",\"0\",\"-3.12483381292309\",\"-5.33090496651437\",\"0.290991614282302\"\n" +
            "\"2\",\"covariates/upr_u.tif\",\"0.0101010101010101\",\"-2.00497989775219\",\"-4.16498912476439\",\"0.970722938007879\"\n" +
            "\"3\",\"covariates/upr_u.tif\",\"0.0202020202020202\",\"-2.00490998103643\",\"-4.16498912476439\",\"0.970722938007879\"\n" +
            "\"4\",\"covariates/upr_u.tif\",\"0.0303030303030303\",\"-2.0016904203227\",\"-4.16498912476439\",\"0.970722938007879\"";

        // Act
        List<CsvEffectCurveCovariateInfluence> result = CsvEffectCurveCovariateInfluence.readFromCSV(csv);

        // Assert
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getCovariateFilePath()).isEqualTo("covariates/upr_u.tif");
        assertThat(result.get(0).getIndex()).isEqualTo("1");
        assertThat(result.get(0).getCovariateValue()).isEqualTo(0);
        assertThat(result.get(0).getMeanInfluence()).isEqualTo(-3.12483381292309);
        assertThat(result.get(0).getLowerQuantile()).isEqualTo(-5.33090496651437);
        assertThat(result.get(0).getUpperQuantile()).isEqualTo(0.290991614282302);
        assertThat(result.get(1).getCovariateFilePath()).isEqualTo("covariates/upr_u.tif");
        assertThat(result.get(1).getIndex()).isEqualTo("2");
        assertThat(result.get(1).getCovariateValue()).isEqualTo(0.0101010101010101);
        assertThat(result.get(1).getMeanInfluence()).isEqualTo(-2.00497989775219);
    }
}
