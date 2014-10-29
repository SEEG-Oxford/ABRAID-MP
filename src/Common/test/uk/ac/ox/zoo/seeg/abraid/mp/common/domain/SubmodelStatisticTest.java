package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvSubmodelStatistic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunStatistics;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for SubmodelStatistic.
 * Copyright (c) 2014 University of Oxford
 */
public class SubmodelStatisticTest {
    @Test
    public void constructorBindsFieldCorrectly() {
        // Arrange
        ModelRun runExpectation = mock(ModelRun.class);
        CsvSubmodelStatistic dtoExpectation = new CsvSubmodelStatistic();
        dtoExpectation.setDeviance(1.0);
        dtoExpectation.setRootMeanSquareError(2.0);
        dtoExpectation.setKappa(3.0);
        dtoExpectation.setAreaUnderCurve(4.0);
        dtoExpectation.setSensitivity(5.0);
        dtoExpectation.setSpecificity(6.0);
        dtoExpectation.setProportionCorrectlyClassified(7.0);
        dtoExpectation.setKappaStandardDeviation(8.0);
        dtoExpectation.setAreaUnderCurveStandardDeviation(9.0);
        dtoExpectation.setSensitivityStandardDeviation(10.0);
        dtoExpectation.setSpecificityStandardDeviation(11.0);
        dtoExpectation.setProportionCorrectlyClassifiedStandardDeviation(12.0);
        dtoExpectation.setThreshold(13.0);

        // Act
        SubmodelStatistic result = new SubmodelStatistic(dtoExpectation, runExpectation);

        // Assert
        assertThat(result.getModelRun()).isEqualTo(runExpectation);
        assertThat(result.getDeviance()).isEqualTo(dtoExpectation.getDeviance());
        assertThat(result.getRootMeanSquareError()).isEqualTo(dtoExpectation.getRootMeanSquareError());
        assertThat(result.getKappa()).isEqualTo(dtoExpectation.getKappa());
        assertThat(result.getAreaUnderCurve()).isEqualTo(dtoExpectation.getAreaUnderCurve());
        assertThat(result.getSensitivity()).isEqualTo(dtoExpectation.getSensitivity());
        assertThat(result.getSpecificity()).isEqualTo(dtoExpectation.getSpecificity());
        assertThat(result.getProportionCorrectlyClassified()).isEqualTo(dtoExpectation.getProportionCorrectlyClassified());
        assertThat(result.getKappaStandardDeviation()).isEqualTo(dtoExpectation.getKappaStandardDeviation());
        assertThat(result.getAreaUnderCurveStandardDeviation()).isEqualTo(dtoExpectation.getAreaUnderCurveStandardDeviation());
        assertThat(result.getSensitivityStandardDeviation()).isEqualTo(dtoExpectation.getSensitivityStandardDeviation());
        assertThat(result.getSpecificityStandardDeviation()).isEqualTo(dtoExpectation.getSpecificityStandardDeviation());
        assertThat(result.getProportionCorrectlyClassifiedStandardDeviation()).isEqualTo(dtoExpectation.getProportionCorrectlyClassifiedStandardDeviation());
        assertThat(result.getThreshold()).isEqualTo(dtoExpectation.getThreshold());
        assertThat(result.getId()).isNull();
    }

    @Test
    public void summariseReturnsExpectedJson() {
        // Arrange
        SubmodelStatistic stat1 = new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat2 = new SubmodelStatistic(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

        // Act
        JsonModelRunStatistics result = SubmodelStatistic.summarise(Arrays.asList(stat1, stat2));

        // Assert
        assertThat(result.getDeviance()).isEqualTo(2.0);
        assertThat(result.getRmse()).isEqualTo(3.0);
        assertThat(result.getKappa()).isEqualTo(4.0);
        assertThat(result.getAuc()).isEqualTo(5.0);
        assertThat(result.getSens()).isEqualTo(6.0);
        assertThat(result.getSpec()).isEqualTo(7.0);
        assertThat(result.getPcc()).isEqualTo(8.0);
        assertThat(result.getThreshold()).isEqualTo(9.0);

        assertSd(result.getDevianceSd());
        assertSd(result.getRmseSd());
        assertSd(result.getKappaSd());
        assertSd(result.getAucSd());
        assertSd(result.getSensSd());
        assertSd(result.getSpecSd());
        assertSd(result.getPccSd());
        assertSd(result.getThresholdSd());
    }

    private void assertSd(double sd) {
        assertThat(sd).isEqualTo(Math.sqrt(2));
    }

    @Test
    public void summariseReturnsExpectedJsonFromEmptyList() {
        // Act
        JsonModelRunStatistics result = SubmodelStatistic.summarise(new ArrayList<SubmodelStatistic>());

        // Assert
        assertThat(result.getDeviance()).isNull();
        assertThat(result.getRmse()).isNull();
        assertThat(result.getKappa()).isNull();
        assertThat(result.getAuc()).isNull();
        assertThat(result.getSens()).isNull();
        assertThat(result.getSpec()).isNull();
        assertThat(result.getPcc()).isNull();
        assertThat(result.getThreshold()).isNull();

        assertThat(result.getDevianceSd()).isNull();
        assertThat(result.getRmseSd()).isNull();
        assertThat(result.getKappaSd()).isNull();
        assertThat(result.getAucSd()).isNull();
        assertThat(result.getSensSd()).isNull();
        assertThat(result.getSpecSd()).isNull();
        assertThat(result.getPccSd()).isNull();
        assertThat(result.getThresholdSd()).isNull();
    }
}
