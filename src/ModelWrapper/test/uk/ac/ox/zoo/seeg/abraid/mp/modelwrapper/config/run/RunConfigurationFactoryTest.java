package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonDisease;

import java.io.IOException;
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
    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Test
    public void createDefaultConfigurationUsesCorrectDefaults() throws Exception {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(0);
        ConfigurationService configurationService = mock(ConfigurationService.class);
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);

        when(configurationService.getCacheDirectory()).thenReturn("expectation1");
        when(configurationService.getModelRepositoryVersion()).thenReturn("expectation2");
        setupExpectedCodeConfiguration(configurationService);
        setupExpectedExecutionConfiguration(configurationService);
        setupExpectedCovariateConfiguration(configurationService);
        setupExpectedAdminUnitConfiguration(configurationService);

        String expectedRunNameStart = "foo1_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + "_";

        // Act
        RunConfiguration result = target.createDefaultConfiguration(1, true, "foo", "foo1");

        // Assert
        assertThat(result.getRunName()).startsWith(expectedRunNameStart);
        assertThat(result.getRunName()).matches(expectedRunNameStart + UUID_REGEX);
        assertThat(result.getBaseDir().getName()).isEqualTo("expectation1");
        assertCorrectCodeConfiguration(result.getCodeConfig());
        assertCorrectExecutionConfiguration(result.getExecutionConfig());
        assertCorrectCovariateConfiguration(result.getCovariateConfig());
        assertCorrectAdminUnitConfiguration(result.getAdminUnitConfig());

    }

    private void setupExpectedCodeConfiguration(ConfigurationService configurationService) {
        when(configurationService.getModelRepositoryVersion()).thenReturn("modelVersion");
        when(configurationService.getModelRepositoryUrl()).thenReturn("modelRepository");
    }

    private void assertCorrectCodeConfiguration(CodeRunConfiguration result) {
        assertThat(result.getModelVersion()).isEqualTo("modelVersion");
        assertThat(result.getModelRepository()).isEqualTo("modelRepository");
    }

    private void setupExpectedExecutionConfiguration(ConfigurationService configurationService) throws Exception {
        when(configurationService.getRExecutablePath()).thenReturn("rPath");
        when(configurationService.getMaxModelRunDuration()).thenReturn(1234);
        when(configurationService.getMaxCPUs()).thenReturn(4321);
        when(configurationService.getDryRunFlag()).thenReturn(true);
    }

    private void assertCorrectExecutionConfiguration(ExecutionRunConfiguration result) {
        assertThat(result.getRPath().toString()).isEqualTo("rPath");
        assertThat(result.getMaxRuntime()).isEqualTo(1234);
        assertThat(result.getMaxCPUs()).isEqualTo(4321);
        assertThat(result.getDryRunFlag()).isEqualTo(true);
    }

    private void setupExpectedCovariateConfiguration(ConfigurationService configurationService) throws Exception {
        JsonCovariateConfiguration expectedCovariates = new JsonCovariateConfiguration(
                new ArrayList<JsonDisease>(), Arrays.asList(
                    new JsonCovariateFile("covariateFile", "", null, false, Arrays.asList(1))
        ));
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariates);
        when(configurationService.getCovariateDirectory()).thenReturn("covariateDir");
    }

    private void assertCorrectCovariateConfiguration(CovariateRunConfiguration result) {
        assertThat(result.getCovariateDirectory()).isEqualTo("covariateDir");
        assertThat(result.getCovariateFilePaths()).containsOnly("covariateFile");
    }

    private void setupExpectedAdminUnitConfiguration(ConfigurationService configurationService) {
        when(configurationService.getGlobalRasterFile()).thenReturn("globalRaster");
        when(configurationService.getTropicalRasterFile()).thenReturn("tropicalRaster");
        when(configurationService.getAdmin1RasterFile()).thenReturn("admin1Raster");
        when(configurationService.getAdmin2RasterFile()).thenReturn("admin2Raster");
    }

    private void assertCorrectAdminUnitConfiguration(AdminUnitRunConfiguration result) {
        assertThat(result.getUseGlobalRasterFile()).isEqualTo(true);
        assertThat(result.getGlobalRasterFile()).isEqualTo("globalRaster");
        assertThat(result.getTropicalRasterFile()).isEqualTo("tropicalRaster");
        assertThat(result.getAdmin1RasterFile()).isEqualTo("admin1Raster");
        assertThat(result.getAdmin2RasterFile()).isEqualTo("admin2Raster");
    }

    @Test
    public void createDefaultConfigurationHandlesLongNames() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupMinimumConfig(configurationService);
        RunConfigurationFactory target = new RunConfigurationFactoryImpl(configurationService);
        DateTimeUtils.setCurrentMillisFixed(0);

        String longName = RandomStringUtils.randomAlphanumeric(300);
        String expectedRunNameStart =
                longName.substring(0, 195) + "_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + "_";

        // Act
        RunConfiguration result = target.createDefaultConfiguration(1, true, longName, longName);

        // Assert
        assertThat(result.getRunName()).startsWith(expectedRunNameStart);
        assertThat(result.getRunName()).matches(expectedRunNameStart + UUID_REGEX);
    }

    @Test
    public void createDefaultConfigurationExtractsCorrectCovariateFiles() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        setupMinimumConfig(configurationService);
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
        assertThat(result.getCovariateConfig().getCovariateFilePaths()).contains("path1");
        assertThat(result.getCovariateConfig().getCovariateFilePaths()).contains("path4");
    }

    private void setupMinimumConfig(ConfigurationService configurationService) throws ConfigurationException, IOException {
        when(configurationService.getCacheDirectory()).thenReturn("");
        when(configurationService.getRExecutablePath()).thenReturn("");
        when(configurationService.getCovariateConfiguration()).thenReturn(
                new JsonCovariateConfiguration(new ArrayList<JsonDisease>(), new ArrayList<JsonCovariateFile>()));
    }
}

