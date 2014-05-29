package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

/**
 * An exception to identify issues caused when running an external thread.
 * Copyright (c) 2014 University of Oxford
 */
public class ProcessException extends Exception {
    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessException(Throwable cause) {
        super(cause);
    }
}
