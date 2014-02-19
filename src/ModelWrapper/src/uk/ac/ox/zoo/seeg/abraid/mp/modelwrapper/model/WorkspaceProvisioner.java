package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;

/**
 * Interface to provide a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public interface WorkspaceProvisioner {
    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @return The model wrapper script file to run.
     */
    File provisionWorkspace(RunConfiguration configuration);
}
