package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the JsonSubmodelStatisticSet.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonSubmodelStatisticSetTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        SubmodelStatistic submodelStatistic = createSubmodelStatistic();

        // Act
        JsonSubmodelStatisticSet result = new JsonSubmodelStatisticSet(submodelStatistic);

        // Assert
        assertJsonSubmodelStatistic(result);
    }

    private SubmodelStatistic createSubmodelStatistic() {
        SubmodelStatistic submodelStatistic = new SubmodelStatistic();
        submodelStatistic.setModelRun(new ModelRun());
        submodelStatistic.setDeviance(1.0);
        submodelStatistic.setRootMeanSquareError(1.1);
        submodelStatistic.setKappa(1.2);
        submodelStatistic.setAreaUnderCurve(1.3);
        submodelStatistic.setSensitivity(1.4);
        submodelStatistic.setSpecificity(1.5);
        submodelStatistic.setProportionCorrectlyClassified(1.6);
        submodelStatistic.setKappaStandardDeviation(1.7);
        submodelStatistic.setAreaUnderCurveStandardDeviation(1.8);
        submodelStatistic.setSensitivityStandardDeviation(1.9);
        submodelStatistic.setSpecificityStandardDeviation(2.0);
        submodelStatistic.setProportionCorrectlyClassifiedStandardDeviation(2.1);
        submodelStatistic.setThreshold(2.2);
        return submodelStatistic;
    }

    private void assertJsonSubmodelStatistic(JsonSubmodelStatisticSet jsonSubmodelStatisticSet) {
        assertThat(jsonSubmodelStatisticSet.getDeviance()).isEqualTo(1.0);
        assertThat(jsonSubmodelStatisticSet.getRmse()).isEqualTo(1.1);
        assertThat(jsonSubmodelStatisticSet.getKappa()).isEqualTo(1.2);
        assertThat(jsonSubmodelStatisticSet.getAuc()).isEqualTo(1.3);
        assertThat(jsonSubmodelStatisticSet.getSens()).isEqualTo(1.4);
        assertThat(jsonSubmodelStatisticSet.getSpec()).isEqualTo(1.5);
        assertThat(jsonSubmodelStatisticSet.getPcc()).isEqualTo(1.6);
        assertThat(jsonSubmodelStatisticSet.getKappaSd()).isEqualTo(1.7);
        assertThat(jsonSubmodelStatisticSet.getAucSd()).isEqualTo(1.8);
        assertThat(jsonSubmodelStatisticSet.getSensSd()).isEqualTo(1.9);
        assertThat(jsonSubmodelStatisticSet.getSpecSd()).isEqualTo(2.0);
        assertThat(jsonSubmodelStatisticSet.getPccSd()).isEqualTo(2.1);
        assertThat(jsonSubmodelStatisticSet.getThreshold()).isEqualTo(2.2);
    }

}
