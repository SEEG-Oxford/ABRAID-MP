package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import java.util.Map;

/**
 * An immutable data structure to hold the covariate related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateRunConfiguration {
    private final String covariateDirectory;
    private final Map<String, String> covariateFiles;

    public CovariateRunConfiguration(String covariateDirectory, Map<String, String> covariateFiles) {
        this.covariateDirectory = covariateDirectory;
        this.covariateFiles = covariateFiles;
    }

    public String getCovariateDirectory() {
        return covariateDirectory;
    }

    public Map<String, String> getCovariateFiles() {
        return covariateFiles;
    }
}

