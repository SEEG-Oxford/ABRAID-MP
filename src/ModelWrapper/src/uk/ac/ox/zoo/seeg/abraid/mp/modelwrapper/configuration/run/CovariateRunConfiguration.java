package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run;

import java.util.Collection;
import java.util.Collections;

/**
 * An immutable data structure to hold the covariate related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateRunConfiguration {
    private final String covariateDirectory;
    private final Collection<String> covariateFilePaths;

    public CovariateRunConfiguration(String covariateDirectory, Collection<String> covariateFilePaths) {
        this.covariateDirectory = covariateDirectory;
        this.covariateFilePaths = Collections.unmodifiableCollection(covariateFilePaths);
    }

    public String getCovariateDirectory() {
        return covariateDirectory;
    }

    public Collection<String> getCovariateFilePaths() {
        return covariateFilePaths;
    }
}
