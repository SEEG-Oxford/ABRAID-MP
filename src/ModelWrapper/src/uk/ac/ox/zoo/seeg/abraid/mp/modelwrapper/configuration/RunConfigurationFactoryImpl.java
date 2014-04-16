package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

<<<<<<< HEAD
import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.LocalDateTime;
=======
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;
>>>>>>> master

import java.nio.file.Paths;

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
     * @param diseaseName The disease name
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable can not be found.
     */
    @Override
    public RunConfiguration createDefaultConfiguration(String diseaseName) throws ConfigurationException {
        LOGGER.info(LOG_CREATING_THE_DEFAULT_RUN_CONFIGURATION);
        return new RunConfiguration(
                Paths.get(configurationService.getRExecutablePath()).toFile(),
                Paths.get(configurationService.getCacheDirectory()).toFile(),
                buildRunName(diseaseName),
                configurationService.getMaxModelRunDuration(),
                configurationService.getModelRepositoryVersion());
    }

    private String buildRunName(String diseaseName) {
        String safeDiseaseName = diseaseName.replaceAll("[^A-Za-z0-9]", "-");
        if (safeDiseaseName.length() > MAX_DISEASE_NAME_LENGTH) {
            safeDiseaseName = safeDiseaseName.substring(0, MAX_DISEASE_NAME_LENGTH);
        }
        return safeDiseaseName + "_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss");
    }
}
