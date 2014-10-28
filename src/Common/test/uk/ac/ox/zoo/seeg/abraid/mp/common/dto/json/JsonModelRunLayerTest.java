package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonModelRunLayer.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunLayerTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        ModelRun modelRun = mock(ModelRun.class);

        String covariateName = "Name";
        double meanInfluence = 20.2;
        CovariateInfluence covariateInfluence = new CovariateInfluence(covariateName, meanInfluence);
        SubmodelStatistic submodelStatistic = new SubmodelStatistic();

        when(modelRun.getName()).thenReturn("expectedName");
        when(modelRun.getRequestDate()).thenReturn(new DateTime(1413210069L * 1000));
        when(modelRun.getCovariateInfluences()).thenReturn(Arrays.asList(covariateInfluence));
        when(modelRun.getSubmodelStatistics()).thenReturn(Arrays.asList(submodelStatistic));

        // Act
        JsonModelRunLayer result = new JsonModelRunLayer(modelRun);

        // Assert
        assertThat(result.getId()).isEqualTo("expectedName");
        assertThat(result.getDate()).isEqualTo("2014-10-13");
        assertThat(result.getCovariates()).hasSize(1);
        assertThat(result.getStatistics()).isNotNull();

        JsonCovariateInfluence jsonCovariateInfluence = result.getCovariates().get(0);
        assertThat(jsonCovariateInfluence.getName()).isEqualTo(covariateName);
        assertThat(jsonCovariateInfluence.getMeanInfluence()).isEqualTo(meanInfluence);

    }
}
