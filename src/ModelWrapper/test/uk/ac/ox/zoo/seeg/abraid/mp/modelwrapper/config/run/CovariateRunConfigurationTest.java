package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the CovariateRunConfiguration class.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateRunConfigurationTest {
    @Test
    public void constructorBindsParametersCorrectly() {
        // Arrange
        String expectedCovariateDir = "foobar";
        List<String> expectedCovariatePaths = Arrays.asList("a", "b", "c");

        // Act
        CovariateRunConfiguration result = new CovariateRunConfiguration(expectedCovariateDir, expectedCovariatePaths);

        // Assert
        assertThat(result.getCovariateDirectory()).isEqualTo(expectedCovariateDir);
        assertThat(result.getCovariateFilePaths()).containsAll(expectedCovariatePaths);
    }
}
