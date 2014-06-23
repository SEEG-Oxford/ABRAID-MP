package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvSubmodelStatistic;

import static org.fest.assertions.api.Assertions.assertThat;
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
}
