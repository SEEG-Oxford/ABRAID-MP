package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateInfluenceTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        String name = "upr_p";
        String displayName = "Periurban";
        double meanInfluence = 45.94;

        CovariateInfluence covariateInfluence = mock(CovariateInfluence.class);
        when(covariateInfluence.getCovariateName()).thenReturn(name);
        when(covariateInfluence.getCovariateDisplayName()).thenReturn(displayName);
        when(covariateInfluence.getMeanInfluence()).thenReturn(meanInfluence);

        // Act
        JsonCovariateInfluence result = new JsonCovariateInfluence(covariateInfluence);

        // Assert
        assertThat(result.getName()).isEqualTo(displayName);
        assertThat(result.getMeanInfluence()).isEqualTo(meanInfluence);
    }
}
