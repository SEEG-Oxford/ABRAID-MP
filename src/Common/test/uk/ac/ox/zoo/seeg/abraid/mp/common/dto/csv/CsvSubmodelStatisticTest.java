package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CSVSubmodelStatistic.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvSubmodelStatisticTest {
    @Test
    public void readFromCSVReturnsCorrectResult() throws IOException {
        // Arrange
        String csv =
                "\"auc\",\"sens\",\"spec\",\"pcc\",\"kappa\",\"auc_sd\",\"sens_sd\",\"spec_sd\",\"pcc_sd\",\"kappa_sd\"\n" +
                "0.848373229949656,0.696775452743195,0.881028028124802,0.788901740433999,0.577803480867997,0.0512267573798518,0.0861498610345838,0.0550388589050863,0.0563446472001997,0.106883260098322\n" +
                "0.849398396409793,0.686029409133839,0.904489122498894,0.795259265816366,0.590518531632732,0.0422574560639849,0.070683263020509,0.0374679071356592,0.0445211037495852,0.0845893167437474";

        // Act
        List<CsvSubmodelStatistic> result = CsvSubmodelStatistic.readFromCSV(csv);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAreaUnderCurve()).isEqualTo(0.848373229949656);
        assertThat(result.get(0).getSensitivity()).isEqualTo(0.696775452743195);
        assertThat(result.get(0).getSpecificity()).isEqualTo(0.881028028124802);
        assertThat(result.get(0).getProportionCorrectlyClassified()).isEqualTo(0.788901740433999);
        assertThat(result.get(0).getKappa()).isEqualTo(0.577803480867997);
        assertThat(result.get(0).getAreaUnderCurveStandardDeviation()).isEqualTo(0.0512267573798518);
        assertThat(result.get(0).getSensitivityStandardDeviation()).isEqualTo(0.0861498610345838);
        assertThat(result.get(0).getSpecificityStandardDeviation()).isEqualTo(0.0550388589050863);
        assertThat(result.get(0).getProportionCorrectlyClassifiedStandardDeviation()).isEqualTo(0.0563446472001997);
        assertThat(result.get(0).getKappaStandardDeviation()).isEqualTo(0.106883260098322);
    }

    @Test
    public void readFromCSVReturnsCorrectResultWhenTextContainsEmptyEntries() throws IOException {
        // Arrange
        String csv =
                "\"auc\",\"sens\",\"spec\",\"pcc\",\"kappa\",\"auc_sd\",\"sens_sd\",\"spec_sd\",\"pcc_sd\",\"kappa_sd\"\n" +
                ",,,,,,,,,\n" +
                "0.849398396409793,0.686029409133839,0.904489122498894,0.795259265816366,0.590518531632732,0.0422574560639849,0.070683263020509,0.0374679071356592,0.0445211037495852,0.0845893167437474";

        // Act
        List<CsvSubmodelStatistic> result = CsvSubmodelStatistic.readFromCSV(csv);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getKappa()).isNull();
        assertThat(result.get(0).getAreaUnderCurve()).isNull();
        assertThat(result.get(0).getSensitivity()).isNull();
        assertThat(result.get(0).getSpecificity()).isNull();
        assertThat(result.get(0).getProportionCorrectlyClassified()).isNull();
        assertThat(result.get(0).getKappaStandardDeviation()).isNull();
        assertThat(result.get(0).getAreaUnderCurveStandardDeviation()).isNull();
        assertThat(result.get(0).getSensitivityStandardDeviation()).isNull();
        assertThat(result.get(0).getSpecificityStandardDeviation()).isNull();
        assertThat(result.get(0).getProportionCorrectlyClassifiedStandardDeviation()).isNull();
    }
}
