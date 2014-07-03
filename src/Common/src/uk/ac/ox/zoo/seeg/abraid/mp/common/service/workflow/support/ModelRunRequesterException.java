package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

/**
 * Indicates that there was a problem during any aspect of model run management.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterException extends RuntimeException {
    public ModelRunRequesterException(String message) {
        super(message);
    }

    public ModelRunRequesterException(String message, Throwable cause) {
        super(message, cause);
    }
}
