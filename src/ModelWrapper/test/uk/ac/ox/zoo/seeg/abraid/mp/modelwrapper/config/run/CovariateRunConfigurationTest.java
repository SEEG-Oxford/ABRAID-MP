package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CovariateRunConfiguration class.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateRunConfigurationTest {
    @Test
    public void constructorBindsParametersCorrectly() {
        // Arrange
        String expectedCovariateDir = "foobar";
        Map<String, String> expectedCovariateFiles = new HashMap<>();
        expectedCovariateFiles.put("a", "a");
        expectedCovariateFiles.put("b", "b");
        expectedCovariateFiles.put("c", "c");

        // Act
        CovariateRunConfiguration result = new CovariateRunConfiguration(expectedCovariateDir, expectedCovariateFiles);

        // Assert
        assertThat(result.getCovariateDirectory()).isEqualTo(expectedCovariateDir);
        assertThat(result.getCovariateFiles()).isEqualTo(expectedCovariateFiles);
    }
}
