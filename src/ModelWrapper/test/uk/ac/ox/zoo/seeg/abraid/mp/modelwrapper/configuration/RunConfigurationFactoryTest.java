package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
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
        when(configurationService.getMaxModelRunDuration()).thenReturn(12345);
        when(configurationService.getRExecutablePath()).thenReturn("");
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);
        DateTimeUtils.setCurrentMillisFixed(0);

        // Act
        RunConfiguration result = target.createDefaultConfiguration("foo");

        // Assert
        assertThat(result.getRunName()).isEqualTo("foo_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss"));
        assertThat(result.getBaseDir().getName()).isEqualTo("expectation1");
        assertThat(result.getMaxRuntime()).isEqualTo(12345);
        assertThat(result.getModelVersion()).isEqualTo("expectation2");
    }

    @Test
    public void createDefaultConfigurationHandlesLongNames() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCacheDirectory()).thenReturn("");
        when(configurationService.getModelRepositoryVersion()).thenReturn("");
        when(configurationService.getMaxModelRunDuration()).thenReturn(0);
        when(configurationService.getRExecutablePath()).thenReturn("");
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);
        DateTimeUtils.setCurrentMillisFixed(0);

        // Act
        String longName = RandomStringUtils.randomAlphanumeric(300);
        RunConfiguration result = target.createDefaultConfiguration(longName);

        // Assert
        assertThat(result.getRunName()).isEqualTo(
                longName.substring(0, 195) + "_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss"));
    }
}
