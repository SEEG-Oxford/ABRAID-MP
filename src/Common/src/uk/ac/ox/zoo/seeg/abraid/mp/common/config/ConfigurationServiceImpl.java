package uk.ac.ox.zoo.seeg.abraid.mp.common.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Service class for mutable configuration data.
 * Copyright (c) 2015 University of Oxford
 */
public class ConfigurationServiceImpl extends BaseConfigurationService implements ConfigurationService {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationServiceImpl.class);
    private static final String LOG_UPDATING_REPOSITORY_URL_CONFIGURATION =
            "Updating repository url configuration: %s";
    private static final String LOG_UPDATING_VERSION_CONFIGURATION =
            "Updating repository version configuration: %s";

    private static final String MODEL_REPOSITORY_KEY = "model.repo.url";
    private static final String MODEL_VERSION_KEY = "model.repo.version";

    public ConfigurationServiceImpl(File basicProperties)
            throws ConfigurationException {
        super(basicProperties);
    }

    /**
     * Get the current remote repository url to use as a source for the model.
     * @return The repository url.
     */
    @Override
    public String getModelRepositoryUrl() {
        return getConfigFile().getString(MODEL_REPOSITORY_KEY);
    }

    /**
     * Set the current remote repository url to use as a source for the model.
     * @param repositoryUrl The repository url.
     */
    @Override
    public void setModelRepositoryUrl(String repositoryUrl) {
        LOGGER.info(String.format(LOG_UPDATING_REPOSITORY_URL_CONFIGURATION, repositoryUrl));
        getConfigFile().setProperty(MODEL_REPOSITORY_KEY, repositoryUrl);
    }

    /**
     * Get the current model version to use to run the model.
     * @return The model version.
     */
    @Override
    public String getModelRepositoryVersion() {
        return getConfigFile().getString(MODEL_VERSION_KEY);
    }

    /**
     * Set the current model version to use to run the model.
     * @param version The model version.
     */
    @Override
    public void setModelRepositoryVersion(String version) {
        LOGGER.info(String.format(LOG_UPDATING_VERSION_CONFIGURATION, version));
        getConfigFile().setProperty(MODEL_VERSION_KEY, version);
    }
}

