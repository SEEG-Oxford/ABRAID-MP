package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Interface to provide a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public interface WorkspaceProvisioner {
    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @throws IOException Thrown if the directory can not be correctly provisioned.
     * @return The model wrapper script file to run.
     */
    File provisionWorkspace(RunConfiguration configuration) throws IOException;
}
