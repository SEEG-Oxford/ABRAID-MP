package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import org.apache.commons.configuration.ConfigurationException;

import java.io.File;
import java.io.IOException;

/**
 * Interface to define a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public interface RunConfigurationFactory {
    /**
     * Creates a new RunConfiguration using the current defaults.
     * @param tempDataDir The directory where extra model run data (e.g. covariates are stored), before provisioning.
     * @param diseaseGlobal If the disease is global
     * @param diseaseAbbreviation The disease abbreviation
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable cannot be found.
     * @throws IOException When the covariate configuration cannot be read.
     */
    RunConfiguration createDefaultConfiguration(File tempDataDir, boolean diseaseGlobal,
                                                String diseaseAbbreviation)
            throws ConfigurationException, IOException;
}
