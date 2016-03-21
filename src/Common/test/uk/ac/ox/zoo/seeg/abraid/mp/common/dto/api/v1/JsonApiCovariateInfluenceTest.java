package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonApiCovariateInfluence.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiCovariateInfluenceTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        double influence = 0.4;
        String name = "abc";
        CovariateInfluence covariateInfluence = mock(CovariateInfluence.class);
        CovariateFile covariateFile = mock(CovariateFile.class);
        when(covariateInfluence.getCovariateFile()).thenReturn(covariateFile);
        when(covariateFile.getName()).thenReturn(name);
        when(covariateInfluence.getMeanInfluence()).thenReturn(influence);

        // Act
        JsonApiCovariateInfluence dto = new JsonApiCovariateInfluence(covariateInfluence);

        // Assert
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getMeanInfluence()).isEqualTo(influence);
    }
}
