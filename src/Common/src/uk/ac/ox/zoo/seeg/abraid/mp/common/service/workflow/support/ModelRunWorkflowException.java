package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

/**
 * Indicates that there was a problem during any aspect of the model run request workflow.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunWorkflowException extends RuntimeException {
    public ModelRunWorkflowException(String message) {
        super(message);
    }

    public ModelRunWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
