package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the JsonModelRunStatistics.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunStatisticsTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        SubmodelStatistic stat1 = new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
        SubmodelStatistic stat2 = new SubmodelStatistic(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

        // Act
        JsonModelRunStatistics result = new JsonModelRunStatistics(Arrays.asList(stat1, stat2));

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

}
