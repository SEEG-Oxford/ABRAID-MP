package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

/**
 * Interface to provide an entry point for model runs.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunner {
    /**
     * Starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @throws ProcessException Thrown in response to errors in the model.
     */
    void runModel(RunConfiguration configuration) throws ProcessException;
}
