package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable data structure to hold the covariate related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateRunConfiguration {
    private final Map<String, String> covariateFiles = new HashMap<>();

    public CovariateRunConfiguration(final String covariateDirectory, final Map<String, String> covariateNames) {
        // Convert the file path key of every entry to include the absolute path, suitable for use in R.
        for (Map.Entry<String, String> entry : covariateNames.entrySet()) {
            Path path = Paths.get(covariateDirectory, entry.getKey());
            String absolutePath = escapeFilePathForR(path.toString());
            covariateFiles.put(absolutePath, entry.getValue());
        }
    }

    public Map<String, String> getCovariateFiles() {
        return covariateFiles;
    }

    private static String escapeFilePathForR(String path) {
        return path.replace("\\", "/");
    }
}

