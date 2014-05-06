package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import java.io.File;
import java.util.Collection;

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

    // If the run is for a tropical disease
    private final boolean isTropical;

    // The maximum allowable model run time in ms
    private final int maxRuntime;

    // The version of the model to use
    private String modelVersion;

    // The covariate setup to use
    private final String covariateDirectory;
    private final Collection<String> covariateFilePaths;

    /**
     * Creates a new RunConfiguration.
     * @param rPath The file path for the R binary to be executed.
     * @param baseDir The executable which should be run.
     * @param runName The name that should be used to identify the run.
     * @param isTropical If the run is for a tropical disease.
     * @param maxRuntime The maximum allowed time (in ms) for which the model should be allow to run.
     * @param modelVersion The version of the model to use.
     */
    public RunConfiguration(File rPath, File baseDir, String runName, boolean isTropical, int maxRuntime,
                            String modelVersion, String covariateDirectory, Collection<String> covariateFilePaths) {
        this.rPath = rPath;
        this.baseDir = baseDir;
        this.runName = runName;
        this.isTropical = isTropical;
        this.maxRuntime = maxRuntime;
        this.modelVersion = modelVersion;
        this.covariateDirectory = covariateDirectory;
        this.covariateFilePaths = covariateFilePaths;
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

    public String getCovariateDirectory() {
        return covariateDirectory;
    }

    public Collection<String> getCovariateFilePaths() {
        return covariateFilePaths;
    }

    public boolean isTropical() {
        return isTropical;
    }
}
