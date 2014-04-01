package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for RunConfigurationFactoryImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationFactoryTest {
    @Test
    public void createDefaultConfigurationUsesCorrectDefaults() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCacheDirectory()).thenReturn("expectation1");
        when(configurationService.getModelRepositoryVersion()).thenReturn("expectation2");
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);

        // Act
        RunConfiguration result = target.createDefaultConfiguration();

        // Assert
        assertThat(result.getRunName()).isEqualTo("run");
        assertThat(result.getBaseDir().getName()).isEqualTo("expectation1");
        assertThat(result.getMaxRuntime()).isEqualTo(Integer.MAX_VALUE);
        assertThat(result.getModelVersion()).isEqualTo("expectation2");
    }
}
