package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.joda.time.LocalDateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;

import java.nio.file.Paths;

/**
 * Provides a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfigurationFactoryImpl implements RunConfigurationFactory {
    // This the max file name length (255) minus reserved space for a GUID (36), a datetime (19) and separators (2)
    private static final int MAX_DISEASE_NAME_LENGTH = 195;

    private final ConfigurationService configurationService;

    public RunConfigurationFactoryImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Creates a new RunConfiguration using the current defaults.
     * @return The new RunConfiguration
     */
    @Override
    public RunConfiguration createDefaultConfiguration(String diseaseName) throws ConfigurationException {
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
