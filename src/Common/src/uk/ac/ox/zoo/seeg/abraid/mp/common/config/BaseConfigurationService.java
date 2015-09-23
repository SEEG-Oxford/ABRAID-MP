package uk.ac.ox.zoo.seeg.abraid.mp.common.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Service class for mutable configuration data.
 * Copyright (c) 2015 University of Oxford
 */
public class BaseConfigurationService {
    private static final Logger LOGGER = Logger.getLogger(BaseConfigurationService.class);
    private static final String LOG_LOADING_CONFIGURATION_FILE =
            "Loading configuration file %s";

    private final FileConfiguration basicProperties;

    public BaseConfigurationService(File basicProperties)
            throws ConfigurationException {
        LOGGER.info(String.format(LOG_LOADING_CONFIGURATION_FILE, basicProperties.toString()));
        this.basicProperties = new PropertiesConfiguration(basicProperties);
        this.basicProperties.setAutoSave(true);
    }

    protected FileConfiguration getConfigFile() {
        return basicProperties;
    }
}

