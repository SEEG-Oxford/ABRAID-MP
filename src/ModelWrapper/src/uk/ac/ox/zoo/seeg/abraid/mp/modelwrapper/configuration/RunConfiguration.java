package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import java.io.File;

/**
 * An immutable data structure to hold the configuration for a single model run.
 * Currently this is just a stub for testing.
 * Copyright (c) 2014 University of Oxford
 */
public class RunConfiguration {
    // The name of the run
    private final String runName;

    // The base directory into which the run directory should be created
    private final File baseDir;

    // The path to the R executable to use
    private final File rPath;

    // The maximum allowable model run time in ms
    private final int maxRuntime;

    // The version of the model to use
    private String modelVersion;

    /**
     * Creates a new RunConfiguration.
     * @param rPath The file path for the R binary to be executed.
     * @param baseDir The executable which should be run.
     * @param runName The name that should be used to identify the run.
     * @param maxRuntime The maximum allowed time (in ms) for which the model should be allow to run.
     * @param modelVersion The version of the model to use.
     */
    public RunConfiguration(File rPath, File baseDir, String runName, int maxRuntime, String modelVersion) {
        this.rPath = rPath;
        this.baseDir = baseDir;
        this.runName = runName;
        this.maxRuntime = maxRuntime;
        this.modelVersion = modelVersion;
    }

    public String getRunName() {
        return runName;
    }

    public File getRPath() {
        return rPath;
    }

    public int getMaxRuntime() {
        return maxRuntime;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public String getModelVersion() {
        return modelVersion;
    }
}
