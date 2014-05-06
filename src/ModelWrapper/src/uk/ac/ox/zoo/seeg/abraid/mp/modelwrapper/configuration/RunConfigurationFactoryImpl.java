package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.joda.time.LocalDateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

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
     * @param diseaseName The disease name
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable can not be found.
     * @throws IOException When the covariate configuration can not be read.
     */
    @Override
    public RunConfiguration createDefaultConfiguration(int diseaseId, String diseaseName)
            throws ConfigurationException, IOException {
        LOGGER.info(LOG_CREATING_THE_DEFAULT_RUN_CONFIGURATION);
        return new RunConfiguration(
                Paths.get(configurationService.getRExecutablePath()).toFile(),
                Paths.get(configurationService.getCacheDirectory()).toFile(),
                buildRunName(diseaseName),
                configurationService.getMaxModelRunDuration(),
                configurationService.getModelRepositoryVersion(),
                configurationService.getCovariateDirectory(),
                buildCovariateFileList(diseaseId));
    }

    private Collection<String> buildCovariateFileList(int diseaseId)
            throws ConfigurationException, IOException {
        JsonCovariateConfiguration covariateConfig = configurationService.getCovariateConfiguration();

        Collection<JsonCovariateFile> files = covariateConfig.getFiles();
        files = filter(having(on(JsonCovariateFile.class).getHide(), equalTo(false)), files);
        files = filter(having(on(JsonCovariateFile.class).getEnabled(), (Matcher) hasItem(diseaseId)), files);


        return extract(files, on(JsonCovariateFile.class).getPath());
    }

    private String buildRunName(String diseaseName) {
        String safeDiseaseName = diseaseName.replaceAll("[^A-Za-z0-9]", "-");
        if (safeDiseaseName.length() > MAX_DISEASE_NAME_LENGTH) {
            safeDiseaseName = safeDiseaseName.substring(0, MAX_DISEASE_NAME_LENGTH);
        }
        return safeDiseaseName + "_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss");
    }
}
