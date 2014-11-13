package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonEffectCurveCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonEffectCurveCovariateInfluenceTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        String name = "upr_p";
        String displayName = "Periurban";
        double meanInfluence = 45.94;
        double lowerQuantile = 1231.43;
        double upperQuantile = 51342.1;
        double covariateValue = 2345.0;

        EffectCurveCovariateInfluence covariateInfluence = mock(EffectCurveCovariateInfluence.class);
        when(covariateInfluence.getCovariateFilePath()).thenReturn(name);
        when(covariateInfluence.getCovariateDisplayName()).thenReturn(displayName);
        when(covariateInfluence.getMeanInfluence()).thenReturn(meanInfluence);
        when(covariateInfluence.getCovariateValue()).thenReturn(covariateValue);
        when(covariateInfluence.getUpperQuantile()).thenReturn(upperQuantile);
        when(covariateInfluence.getLowerQuantile()).thenReturn(lowerQuantile);

        // Act
        JsonEffectCurveCovariateInfluence result = new JsonEffectCurveCovariateInfluence(covariateInfluence);

        // Assert
        assertThat(result.getName()).isEqualTo(displayName);
        assertThat(result.getMeanInfluence()).isEqualTo(meanInfluence);
        assertThat(result.getCovariateValue()).isEqualTo(covariateValue);
        assertThat(result.getUpperQuantile()).isEqualTo(upperQuantile);
        assertThat(result.getLowerQuantile()).isEqualTo(lowerQuantile);
    }
}
