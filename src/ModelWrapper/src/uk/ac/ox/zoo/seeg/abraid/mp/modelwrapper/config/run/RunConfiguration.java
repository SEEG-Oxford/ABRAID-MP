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

    private final File tempDataDir;

    // Sub configuration objects
    private final CodeRunConfiguration codeConfig;
    private final ExecutionRunConfiguration executionConfig;
    private final AdminUnitRunConfiguration adminUnitConfig;

    public RunConfiguration(String runName, File baseDir, File tempDataDir,
                            CodeRunConfiguration codeConfig, ExecutionRunConfiguration executionConfig,
                            AdminUnitRunConfiguration adminUnitConfig) {
        this.runName = runName;
        this.baseDir = baseDir;
        this.tempDataDir = tempDataDir;

        this.codeConfig = codeConfig;
        this.executionConfig = executionConfig;
        this.adminUnitConfig = adminUnitConfig;
    }

    public String getRunName() {
        return runName;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getTempDataDir() {
        return tempDataDir;
    }

    public CodeRunConfiguration getCodeConfig() {
        return codeConfig;
    }

    public ExecutionRunConfiguration getExecutionConfig() {
        return executionConfig;
    }

    public AdminUnitRunConfiguration getAdminUnitConfig() {
        return adminUnitConfig;
    }

    public Path getWorkingDirectoryPath() {
        return Paths.get(getBaseDir().getAbsolutePath(), getRunName());
    }
}
