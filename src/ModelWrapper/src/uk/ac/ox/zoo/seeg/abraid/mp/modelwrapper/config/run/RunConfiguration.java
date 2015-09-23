package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An immutable data structure to hold the configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfiguration {
    // The name of the run
    private final String runName;

    // The base directory into which the run directory should be created
    private final File baseDir;

    // Sub configuration objects
    private final ExecutionRunConfiguration executionConfig;

    public RunConfiguration(String runName, File baseDir, ExecutionRunConfiguration executionConfig) {
        this.runName = runName;
        this.baseDir = baseDir;
        this.executionConfig = executionConfig;
    }

    public String getRunName() {
        return runName;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public ExecutionRunConfiguration getExecutionConfig() {
        return executionConfig;
    }

    public Path getWorkingDirectoryPath() {
        return Paths.get(getBaseDir().getAbsolutePath(), getRunName());
    }
}
