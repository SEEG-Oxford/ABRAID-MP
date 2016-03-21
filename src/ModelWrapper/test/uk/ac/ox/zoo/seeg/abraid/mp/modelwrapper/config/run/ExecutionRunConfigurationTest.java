package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Tests the ExecutionRunConfiguration class.
* Copyright (c) 2014 University of Oxford
*/
public class ExecutionRunConfigurationTest {
    @Test
    public void constructorBindsParametersCorrectly() {
        // Arrange
        File expectedRPath = new File("xyz");
        int expectedMaxRunTime = 4;

        // Act
        ExecutionRunConfiguration result = new ExecutionRunConfiguration(expectedRPath, expectedMaxRunTime);

        // Assert
        assertThat(result.getRPath()).isEqualTo(expectedRPath);
        assertThat(result.getMaxRuntime()).isEqualTo(expectedMaxRunTime);
    }
}
