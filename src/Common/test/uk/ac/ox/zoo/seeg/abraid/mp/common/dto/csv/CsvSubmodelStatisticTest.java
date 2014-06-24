package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for CSVSubmodelStatistic.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvSubmodelStatisticTest {
    @Test
    public void readFromCSVReturnsCorrectResult() throws IOException {
        // Arrange
        String csv =
                "\"deviance\",\"rmse\",\"kappa\",\"auc\",\"sens\",\"spec\",\"pcc\",\"kappa_sd\",\"auc_sd\",\"sens_sd\",\"spec_sd\",\"pcc_sd\",\"thresh\"\n" +
                "157.686918531063,0.481008546944311,0.333992726792911,0.666749557052397,0.402030097697386,0.931962629095525,0.666996363396456,0.0714218788340403,0.0503337094885771,0.0627087334416405,0.0278452870909863,0.0432198309616061,0.46605\n" +
                "323.763698135803,0.560711287878735,0.32148423332026,0.671910419911593,0.471226939266407,0.850257294053853,0.66074211666013,0.0657379847027787,0.0419017277242429,0.0536652567379385,0.0353024197450659,0.0366859590096509,0.1435\n";

        // Act
        List<CsvSubmodelStatistic> result = CsvSubmodelStatistic.readFromCSV(csv);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDeviance()).isEqualTo(157.686918531063);
        assertThat(result.get(0).getRootMeanSquareError()).isEqualTo(0.481008546944311);
        assertThat(result.get(0).getKappa()).isEqualTo(0.333992726792911);
        assertThat(result.get(0).getAreaUnderCurve()).isEqualTo(0.666749557052397);
        assertThat(result.get(0).getSensitivity()).isEqualTo(0.402030097697386);
        assertThat(result.get(0).getSpecificity()).isEqualTo(0.931962629095525);
        assertThat(result.get(0).getProportionCorrectlyClassified()).isEqualTo(0.666996363396456);
        assertThat(result.get(0).getKappaStandardDeviation()).isEqualTo(0.0714218788340403);
        assertThat(result.get(0).getAreaUnderCurveStandardDeviation()).isEqualTo(0.0503337094885771);
        assertThat(result.get(0).getSensitivityStandardDeviation()).isEqualTo(0.0627087334416405);
        assertThat(result.get(0).getSpecificityStandardDeviation()).isEqualTo(0.0278452870909863);
        assertThat(result.get(0).getProportionCorrectlyClassifiedStandardDeviation()).isEqualTo(0.0432198309616061);
        assertThat(result.get(0).getThreshold()).isEqualTo(0.46605);
        assertThat(result.get(1).getDeviance()).isEqualTo(323.763698135803);
    }
}
