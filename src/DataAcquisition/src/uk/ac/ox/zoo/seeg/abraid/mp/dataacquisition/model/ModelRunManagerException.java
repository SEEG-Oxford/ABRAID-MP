package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

/**
 * Indicates that there was a problem during any aspect of model run management.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerException extends RuntimeException {
    public ModelRunManagerException(String message) {
        super(message);
    }

    public ModelRunManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
