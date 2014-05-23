package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the CodeRunConfiguration class.
 * Copyright (c) 2014 University of Oxford
 */
public class CodeRunConfigurationTest {
    @Test
    public void constructorBindsParametersCorrectly() {
        // Arrange
        String expectedModelVersion = "foo";
        String expectedRepository = "bar";

        // Act
        CodeRunConfiguration result = new CodeRunConfiguration(expectedModelVersion, expectedRepository);

        // Assert
        assertThat(result.getModelVersion()).isEqualTo(expectedModelVersion);
        assertThat(result.getModelRepository()).isEqualTo(expectedRepository);
    }
}
