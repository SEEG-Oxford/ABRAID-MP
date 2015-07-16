package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
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
        File expectedTempDir = new File("abc");
        String expectedWorkingDirectory = expectedBaseDir.getAbsolutePath() + File.separator + expectedRunName;
        CodeRunConfiguration expectedCodeConfig = mock(CodeRunConfiguration.class);
        ExecutionRunConfiguration expectedExecutionConfig = mock(ExecutionRunConfiguration.class);
        AdminUnitRunConfiguration expectedAdminUnitConfig = mock(AdminUnitRunConfiguration.class);

        // Act
        RunConfiguration result = new RunConfiguration(expectedRunName, expectedBaseDir, expectedTempDir,
                expectedCodeConfig, expectedExecutionConfig, expectedAdminUnitConfig);

        // Assert
        assertThat(result.getRunName()).isEqualTo(expectedRunName);
        assertThat(result.getBaseDir()).isEqualTo(expectedBaseDir);
        assertThat(result.getTempDataDir()).isEqualTo(expectedTempDir);
        assertThat(result.getCodeConfig()).isEqualTo(expectedCodeConfig);
        assertThat(result.getExecutionConfig()).isEqualTo(expectedExecutionConfig);
        assertThat(result.getAdminUnitConfig()).isEqualTo(expectedAdminUnitConfig);
        assertThat(result.getWorkingDirectoryPath().toString()).isEqualTo(expectedWorkingDirectory);
    }
}
