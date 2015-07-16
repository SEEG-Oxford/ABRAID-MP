package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
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
        String name = "dir/upr_p.tif";
        String displayName = "GRUMP peri-urban surface";
        double meanInfluence = 45.94;

        CovariateInfluence covariateInfluence = mock(CovariateInfluence.class);
        CovariateFile covariateFile = mock(CovariateFile.class);
        when(covariateFile.getFile()).thenReturn(name);
        when(covariateFile.getName()).thenReturn(displayName);
        when(covariateInfluence.getMeanInfluence()).thenReturn(meanInfluence);
        when(covariateInfluence.getCovariateFile()).thenReturn(covariateFile);

        // Act
        JsonCovariateInfluence result = new JsonCovariateInfluence(covariateInfluence);

        // Assert
        assertThat(result.getName()).isEqualTo(displayName);
        assertThat(result.getMeanInfluence()).isEqualTo(meanInfluence);
    }
}
