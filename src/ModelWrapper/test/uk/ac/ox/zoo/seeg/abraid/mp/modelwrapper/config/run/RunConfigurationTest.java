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
        boolean expectedDeleteWorkspace = true;
        String expectedWorkingDirectory = expectedBaseDir.getAbsolutePath() + File.separator + expectedRunName;
        ExecutionRunConfiguration expectedExecutionConfig = mock(ExecutionRunConfiguration.class);

        // Act
        RunConfiguration result = new RunConfiguration(expectedRunName, expectedBaseDir, expectedDeleteWorkspace, expectedExecutionConfig);

        // Assert
        assertThat(result.getRunName()).isEqualTo(expectedRunName);
        assertThat(result.getBaseDir()).isEqualTo(expectedBaseDir);
        assertThat(result.getDeleteWorkspace()).isEqualTo(expectedDeleteWorkspace);
        assertThat(result.getExecutionConfig()).isEqualTo(expectedExecutionConfig);
        assertThat(result.getWorkingDirectoryPath().toString()).isEqualTo(expectedWorkingDirectory);
    }
}
