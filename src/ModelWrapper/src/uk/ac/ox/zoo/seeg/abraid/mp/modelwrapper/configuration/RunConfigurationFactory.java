package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

/**
 * Interface to define a factory method for RunConfigurations.
 * Copyright (c) 2014 University of Oxford
 */
public interface RunConfigurationFactory {
    /**
     * Creates a new RunConfiguration using the current defaults.
     * @return The new RunConfiguration
     */
    RunConfiguration createDefaultConfiguration();
}
