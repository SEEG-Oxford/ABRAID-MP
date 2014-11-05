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

        assertSd(result.getDevianceSd(), 2);
        assertSd(result.getRmseSd(), 2);
        assertSd(result.getKappaSd(), 2);
        assertSd(result.getAucSd(), 2);
        assertSd(result.getSensSd(), 2);
        assertSd(result.getSpecSd(), 2);
        assertSd(result.getPccSd(), 2);
        assertSd(result.getThresholdSd(), 2);
    }

    private void assertSd(double sd, int count) {
        assertThat(sd).isEqualTo(Math.sqrt(count));
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

    @Test
    public void summariseReturnsExpectedJsonWhenNullsPresent() {
        // Arrange
        SubmodelStatistic stat1 = new SubmodelStatistic(1.0, 2.0, null, 4.0, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat2 = new SubmodelStatistic(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

        // Act
        JsonModelRunStatistics result = SubmodelStatistic.summarise(Arrays.asList(stat1, stat2));

        // Assert - the whole first submodel should be excluded, so results just equal the 2nd model
        assertThat(result.getDeviance()).isEqualTo(stat2.getDeviance());
        assertThat(result.getRmse()).isEqualTo(stat2.getRootMeanSquareError());
        assertThat(result.getKappa()).isEqualTo(stat2.getKappa());
        assertThat(result.getAuc()).isEqualTo(stat2.getAreaUnderCurve());
        assertThat(result.getSens()).isEqualTo(stat2.getSensitivity());
        assertThat(result.getSpec()).isEqualTo(stat2.getSpecificity());
        assertThat(result.getPcc()).isEqualTo(stat2.getProportionCorrectlyClassified());
        assertThat(result.getThreshold()).isEqualTo(stat2.getThreshold());

        assertThat(result.getDevianceSd()).isEqualTo(0);
        assertThat(result.getRmseSd()).isEqualTo(0);
        assertThat(result.getKappaSd()).isEqualTo(0);
        assertThat(result.getAucSd()).isEqualTo(0);
        assertThat(result.getSensSd()).isEqualTo(0);
        assertThat(result.getSpecSd()).isEqualTo(0);
        assertThat(result.getPccSd()).isEqualTo(0);
        assertThat(result.getThresholdSd()).isEqualTo(0);
    }

    @Test
    public void summariseReturnsExpectedJsonWhenNoCompleteSubmodelsPresent() {
        // Arrange
        SubmodelStatistic stat1 = new SubmodelStatistic(null, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat2 = new SubmodelStatistic(1.0, null, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat3 = new SubmodelStatistic(1.0, 2.0, null, 4.0, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat4 = new SubmodelStatistic(1.0, 2.0, 3.0, null, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat5 = new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, null, 6.0, 7.0, 8.0);
        SubmodelStatistic stat6 = new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, 5.0, null, 7.0, 8.0);
        SubmodelStatistic stat7 = new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, null, 8.0);
        SubmodelStatistic stat8 = new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, null);

        // Act
        JsonModelRunStatistics result = SubmodelStatistic.summarise(Arrays.asList(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8, null));

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
