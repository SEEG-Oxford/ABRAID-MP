package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonDisease;

import java.util.ArrayList;
import java.util.Arrays;

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
        when(configurationService.getCovariateDirectory()).thenReturn("expectation3");
        JsonCovariateConfiguration expectedCovariates = new JsonCovariateConfiguration(
                new ArrayList<JsonDisease>(), new ArrayList<JsonCovariateFile>());
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariates);
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);
        DateTimeUtils.setCurrentMillisFixed(0);

        // Act
        RunConfiguration result = target.createDefaultConfiguration(1, true, "foo", "foo1");

        // Assert
        assertThat(result.getRunName()).isEqualTo("foo1_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss"));
        assertThat(result.getBaseDir().getName()).isEqualTo("expectation1");
        assertThat(result.getMaxRuntime()).isEqualTo(12345);
        assertThat(result.getModelVersion()).isEqualTo("expectation2");
        assertThat(result.getCovariateDirectory()).isEqualTo("expectation3");
        assertThat(result.getCovariateFilePaths()).isEqualTo(new ArrayList<String>());
    }

    @Test
    public void createDefaultConfigurationHandlesLongNames() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCacheDirectory()).thenReturn("");
        when(configurationService.getModelRepositoryVersion()).thenReturn("");
        when(configurationService.getMaxModelRunDuration()).thenReturn(0);
        when(configurationService.getRExecutablePath()).thenReturn("");
        when(configurationService.getCovariateDirectory()).thenReturn("");
        when(configurationService.getCovariateConfiguration()).thenReturn(
                new JsonCovariateConfiguration(new ArrayList<JsonDisease>(), new ArrayList<JsonCovariateFile>()));
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);
        DateTimeUtils.setCurrentMillisFixed(0);

        // Act
        String longName = RandomStringUtils.randomAlphanumeric(300);
        RunConfiguration result = target.createDefaultConfiguration(1, true, longName, longName);

        // Assert
        assertThat(result.getRunName()).isEqualTo(
                longName.substring(0, 195) + "_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss"));
    }

    @Test
    public void createDefaultConfigurationExtractsCorrectCovariateFiles() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCacheDirectory()).thenReturn("");
        when(configurationService.getModelRepositoryVersion()).thenReturn("");
        when(configurationService.getMaxModelRunDuration()).thenReturn(1);
        when(configurationService.getRExecutablePath()).thenReturn("");
        when(configurationService.getCovariateDirectory()).thenReturn("");
        JsonCovariateConfiguration expectedCovariates =
                new JsonCovariateConfiguration(new ArrayList<JsonDisease>(), Arrays.asList(
                        new JsonCovariateFile("path1", "", "", false, Arrays.asList(1, 2, 3)),
                        new JsonCovariateFile("path2", "", "", true, Arrays.asList(1, 2, 3)),
                        new JsonCovariateFile("path3", "", "", false, Arrays.asList(2, 3)),
                        new JsonCovariateFile("path4", "", "", false, Arrays.asList(1))
                ));
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariates);
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);

        // Act
        RunConfiguration result = target.createDefaultConfiguration(1, true, "foo", "foo1");

        // Assert
        assertThat(result.getCovariateFilePaths()).hasSize(2);
        assertThat(result.getCovariateFilePaths()).contains("path1");
        assertThat(result.getCovariateFilePaths()).contains("path4");
    }

}
