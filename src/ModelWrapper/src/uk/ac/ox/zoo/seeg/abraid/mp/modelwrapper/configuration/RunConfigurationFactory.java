package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;

/**
 * Interface to define a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public interface RunConfigurationFactory {
    /**
     * Creates a new RunConfiguration using the current defaults.
     * @param diseaseId The disease id
     * @param diseaseGlobal If the disease is global
     * @param diseaseAbbreviation The disease abbreviation
     * @param diseaseName The disease name
     * @return The new RunConfiguration
     * @throws ConfigurationException When the R executable can not be found.
     * @throws IOException When the covariate configuration can not be read.
     */
    RunConfiguration createDefaultConfiguration(int diseaseId, boolean diseaseGlobal,
                                                String diseaseName, String diseaseAbbreviation)
            throws ConfigurationException, IOException;
}
