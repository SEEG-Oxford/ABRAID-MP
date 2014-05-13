package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run;

/**
 * An immutable data structure to hold the model source code related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class CodeRunConfiguration {
    private final String modelVersion;
    private final String modelRepository;

    public CodeRunConfiguration(String modelVersion, String modelRepository) {
        this.modelVersion = modelVersion;
        this.modelRepository = modelRepository;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public String getModelRepository() {
        return modelRepository;
    }
}

