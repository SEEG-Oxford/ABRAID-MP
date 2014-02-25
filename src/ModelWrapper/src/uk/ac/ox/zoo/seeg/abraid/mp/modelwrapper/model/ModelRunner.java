package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.IOException;

/**
 * Interface to provide an entry point for model runs.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunner {
    /**
     * Starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @return The process handler for the launched process.
     * @throws ProcessException Thrown in response to errors in the model.
     * @throws IOException Thrown if the workspace can not be correctly provisioned.
     */
    ModelProcessHandler runModel(RunConfiguration configuration) throws ProcessException, IOException;
}
