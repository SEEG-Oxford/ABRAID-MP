package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.junit.Test;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the RunConfiguration class.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationTest {
    @Test
    public void runConfigurationBindsParametersCorrectly() {
        // Arrange
        String expectedRunName = "foobar";
        File expectedBaseDir = new File("xyz");
        File expectedRPath = new File("abc");
        int expectedMaxRuntime = 123;

        // Act
        RunConfiguration result = new RunConfiguration(expectedRPath, expectedBaseDir, expectedRunName, expectedMaxRuntime);

        // Assert
        assertThat(result.getRunName()).isEqualTo(expectedRunName);
        assertThat(result.getBaseDir()).isEqualTo(expectedBaseDir);
        assertThat(result.getRPath()).isEqualTo(expectedRPath);
        assertThat(result.getMaxRuntime()).isEqualTo(expectedMaxRuntime);
    }
}
