package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run;

import org.junit.Test;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the RunConfiguration class.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationTest {
    @Test
    public void constructorBindsParametersCorrectly() {
        // Arrange
        String expectedRunName = "foobar";
        File expectedBaseDir = new File("xyz");
        CodeRunConfiguration expectedCodeConfig = mock(CodeRunConfiguration.class);
        ExecutionRunConfiguration expectedExecutionConfig = mock(ExecutionRunConfiguration.class);
        CovariateRunConfiguration expectedCovariateConfig = mock(CovariateRunConfiguration.class);
        AdminUnitRunConfiguration expectedAdminUnitConfig = mock(AdminUnitRunConfiguration.class);

        // Act
        RunConfiguration result = new RunConfiguration(expectedRunName, expectedBaseDir,
                expectedCodeConfig, expectedExecutionConfig, expectedCovariateConfig, expectedAdminUnitConfig);

        // Assert
        assertThat(result.getRunName()).isEqualTo(expectedRunName);
        assertThat(result.getBaseDir()).isEqualTo(expectedBaseDir);
        assertThat(result.getCodeConfig()).isEqualTo(expectedCodeConfig);
        assertThat(result.getExecutionConfig()).isEqualTo(expectedExecutionConfig);
        assertThat(result.getCovariateConfig()).isEqualTo(expectedCovariateConfig);
        assertThat(result.getAdminUnitConfig()).isEqualTo(expectedAdminUnitConfig);
    }
}
