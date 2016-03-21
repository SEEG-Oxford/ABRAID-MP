package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;

/**
 * Interface to define a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public interface RunConfigurationFactory {
    /**
     * Creates a new RunConfiguration using the current defaults.
     * @param runName The name of the model run.
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable cannot be found.
     * @throws IOException When the covariate configuration cannot be read.
     */
    RunConfiguration createDefaultConfiguration(String runName)
            throws ConfigurationException, IOException;
}
