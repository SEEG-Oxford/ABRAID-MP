package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Interface to provide a mechanism for generating model run scripts.
 * Copyright (c) 2014 University of Oxford
 */
public interface ScriptGenerator {
    /**
     * Creates a model run script file in the working directory for the given configuration.
     * @param runConfiguration The model run configuration.
     * @param workingDirectory The directory in which the script should be created.
     * @param dryRun Indicates whether the full model should run.
     * @return The script file.
     * @throws IOException Thrown in response to issues creating the script file.
     */
    File generateScript(RunConfiguration runConfiguration, File workingDirectory, boolean dryRun) throws IOException;
}
