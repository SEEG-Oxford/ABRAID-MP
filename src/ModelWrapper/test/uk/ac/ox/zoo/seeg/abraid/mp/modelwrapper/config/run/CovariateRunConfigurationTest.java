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
        Map<String, String> covariateNames = new HashMap<>();
        covariateNames.put("a", "a");
        covariateNames.put("b", "b");
        covariateNames.put("c", "c");

        Map<String, String> expectedCovariateFiles = new HashMap<>();
        expectedCovariateFiles.put("foobar/a", "a");
        expectedCovariateFiles.put("foobar/b", "b");
        expectedCovariateFiles.put("foobar/c", "c");

        // Act
        CovariateRunConfiguration result = new CovariateRunConfiguration(expectedCovariateDir, covariateNames);

        // Assert
        assertThat(result.getCovariateFiles()).isEqualTo(expectedCovariateFiles);
    }
}
