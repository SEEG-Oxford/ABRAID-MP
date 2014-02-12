package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

/**
 * Interface to provide interaction methods for an external process.
 * Copyright (c) 2014 University of Oxford
 */
public interface ProcessRunner {
    /**
     * Starts the external process.
     * @throws ProcessException Throw in response to problems in the external process.
     */
    void run() throws ProcessException;
}
