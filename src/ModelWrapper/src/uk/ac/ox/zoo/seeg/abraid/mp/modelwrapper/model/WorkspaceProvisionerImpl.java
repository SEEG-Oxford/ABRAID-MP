package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Provides a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerImpl implements WorkspaceProvisioner {
    private final ScriptGenerator scriptGenerator;

    public WorkspaceProvisionerImpl(ScriptGenerator scriptGenerator) {
        this.scriptGenerator = scriptGenerator;
    }

    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @throws IOException Thrown if the directory can not be correctly provisioned.
     * @return The model wrapper script file to run.
     */
    @Override
    public File provisionWorkspace(RunConfiguration configuration) throws IOException {
        // Create directory
        Path workingDirectoryPath = Paths.get(
                configuration.getBaseDir().getAbsolutePath(),
                configuration.getRunName() + "-" + UUID.randomUUID().toString());

        File workingDirectory = workingDirectoryPath.toFile();
        workingDirectory.mkdirs();

        // Copy input data
        // Copy model

        // Template script
        File runScript = scriptGenerator.generateScript(configuration, workingDirectory, false);
        return runScript;
    }
}
