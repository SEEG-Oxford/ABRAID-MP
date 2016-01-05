package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.joda.time.DateTimeUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ModelWrapperConfigurationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Tests for RunConfigurationFactoryImpl.
* Copyright (c) 2014 University of Oxford
*/
public class RunConfigurationFactoryTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void createDefaultConfigurationUsesCorrectDefaults() throws Exception {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(0);
        ModelWrapperConfigurationService configurationService = mock(ModelWrapperConfigurationService.class);
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);

        when(configurationService.getCacheDirectory()).thenReturn("expectation1");
        setupExpectedExecutionConfiguration(configurationService);
        when(configurationService.getDeleteWorkspaces()).thenReturn(true);

        String expectedRunName = "name123";

        // Act
        RunConfiguration result = target.createDefaultConfiguration(expectedRunName);

        // Assert
        assertThat(result.getRunName()).isEqualTo(expectedRunName);
        assertThat(result.getBaseDir().getParentFile().getName()).isEqualTo("expectation1");
        assertThat(result.getBaseDir().getName()).isEqualTo("runs");
        assertThat(result.getDeleteWorkspace()).isEqualTo(true);
        assertCorrectExecutionConfiguration(result.getExecutionConfig());
    }

    private void setupExpectedExecutionConfiguration(ModelWrapperConfigurationService configurationService) throws Exception {
        when(configurationService.getRExecutablePath()).thenReturn("rPath");
        when(configurationService.getMaxModelRunDuration()).thenReturn(1234);
    }

    private void assertCorrectExecutionConfiguration(ExecutionRunConfiguration result) {
        assertThat(result.getRPath().toString()).isEqualTo("rPath");
        assertThat(result.getMaxRuntime()).isEqualTo(1234);
    }
}

