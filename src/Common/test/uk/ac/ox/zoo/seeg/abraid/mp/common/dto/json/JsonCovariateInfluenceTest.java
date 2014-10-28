package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateInfluenceTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        String name = "upr_p";
        double meanInfluence = 45.94;

        CovariateInfluence covariateInfluence = new CovariateInfluence(name, meanInfluence);

        // Act
        JsonCovariateInfluence result = new JsonCovariateInfluence(covariateInfluence);

        // Assert
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getMeanInfluence()).isEqualTo(meanInfluence);
    }
}
