package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Provides a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerImpl implements WorkspaceProvisioner {
    private static final String SCRIPT_FILE_NAME = "runModel.R";

    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @return The model wrapper script file to run.
     */
    @Override
    public File provisionWorkspace(RunConfiguration configuration) {
        // Create directory
        Path workingDirectoryPath = Paths.get(
                configuration.getBaseDir().getAbsolutePath(),
                configuration.getRunName() + "-" + UUID.randomUUID().toString());

        File workingDirectory = workingDirectoryPath.toFile();
        workingDirectory.mkdirs();

        // Copy input data
        // Copy model

        // Template script

        return Paths.get(workingDirectoryPath.toString(), SCRIPT_FILE_NAME).toFile();
    }
}
