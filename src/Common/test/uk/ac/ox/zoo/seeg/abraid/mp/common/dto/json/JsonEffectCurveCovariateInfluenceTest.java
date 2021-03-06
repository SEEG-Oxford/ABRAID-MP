package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;

import java.util.Arrays;

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
        String displayName = "GRUMP peri-urban surface";
        double meanInfluence = 45.94;
        double lowerQuantile = 1231.43;
        double upperQuantile = 51342.1;
        double covariateValue = 2345.0;

        EffectCurveCovariateInfluence covariateInfluence = mock(EffectCurveCovariateInfluence.class);
        CovariateFile covariateFile = mock(CovariateFile.class);
        CovariateSubFile subObj = mock(CovariateSubFile.class);
        when(subObj.getFile()).thenReturn(name);
        when(covariateFile.getFiles()).thenReturn(Arrays.asList(subObj));
        when(covariateFile.getName()).thenReturn(displayName);
        when(covariateInfluence.getCovariateFile()).thenReturn(covariateFile);
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
