package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.joda.time.LocalDateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;

/**
 * Provides a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationFactoryImpl implements RunConfigurationFactory {
    private static final Logger LOGGER = Logger.getLogger(RunConfigurationFactoryImpl.class);
    private static final String LOG_CREATING_THE_DEFAULT_RUN_CONFIGURATION = "Creating the default run configuration.";

    // This the max file name length (255) minus reserved space for a GUID (36), a datetime (19) and separators (2)
    private static final int MAX_DISEASE_NAME_LENGTH = 195;

    private final ConfigurationService configurationService;

    public RunConfigurationFactoryImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Creates a new RunConfiguration using the current defaults.
     * @param diseaseId The disease id
     * @param diseaseGlobal If the disease is global
     * @param diseaseName The disease name
     * @param diseaseAbbreviation The disease abbreviation
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable cannot be found.
     * @throws IOException When the covariate configuration cannot be read.
     */
    @Override
    public RunConfiguration createDefaultConfiguration(int diseaseId, boolean diseaseGlobal,
                                                       String diseaseName, String diseaseAbbreviation)
            throws ConfigurationException, IOException {
        LOGGER.info(LOG_CREATING_THE_DEFAULT_RUN_CONFIGURATION);
        return new RunConfiguration(
                buildRunName(diseaseAbbreviation),
                buildBaseDir(),
                buildCodeConfig(),
                buildExecutionConfig(),
                buildCovariateConfig(diseaseId),
                buildAdminUnitConfig(diseaseGlobal));
    }

    private String buildRunName(String diseaseAbbreviation) {
        String safeDiseaseName = diseaseAbbreviation.replaceAll("[^A-Za-z0-9]", "-");
        if (safeDiseaseName.length() > MAX_DISEASE_NAME_LENGTH) {
            safeDiseaseName = safeDiseaseName.substring(0, MAX_DISEASE_NAME_LENGTH);
        }

        return safeDiseaseName + "_" +
               LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + "_" +
               UUID.randomUUID();
    }

    private File buildBaseDir() {
        return Paths.get(configurationService.getCacheDirectory()).toFile();
    }

    private CodeRunConfiguration buildCodeConfig() {
        return new CodeRunConfiguration(
                configurationService.getModelRepositoryVersion(),
                configurationService.getModelRepositoryUrl()
        );
    }

    private ExecutionRunConfiguration buildExecutionConfig() throws ConfigurationException {
        return new ExecutionRunConfiguration(
                Paths.get(configurationService.getRExecutablePath()).toFile(),
                configurationService.getMaxModelRunDuration(),
                configurationService.getMaxCPUs(),
                configurationService.getModelVerboseFlag(),
                configurationService.getDryRunFlag()
        );
    }

    private CovariateRunConfiguration buildCovariateConfig(int diseaseId) throws ConfigurationException, IOException {
        return new CovariateRunConfiguration(
                configurationService.getCovariateDirectory(),
                buildCovariateFileList(diseaseId));
    }

    private AdminUnitRunConfiguration buildAdminUnitConfig(boolean diseaseGlobal) {
        return new AdminUnitRunConfiguration(
                diseaseGlobal,
                configurationService.getAdmin1RasterFile(),
                configurationService.getAdmin2RasterFile(),
                configurationService.getTropicalShapeFile(),
                configurationService.getGlobalShapeFile());
    }

    private Collection<String> buildCovariateFileList(int diseaseId)
            throws ConfigurationException, IOException {
        JsonCovariateConfiguration covariateConfig = configurationService.getCovariateConfiguration();

        Collection<JsonCovariateFile> files = covariateConfig.getFiles();
        files = filter(having(on(JsonCovariateFile.class).getHide(), equalTo(false)), files);
        files = filter(having(on(JsonCovariateFile.class).getEnabled(), (Matcher) hasItem(diseaseId)), files);

        return extract(files, on(JsonCovariateFile.class).getPath());
    }
}
