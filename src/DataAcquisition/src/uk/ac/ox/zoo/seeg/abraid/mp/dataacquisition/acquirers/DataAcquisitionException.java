package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers;

/**
 * Indicates a problem during data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DataAcquisitionException extends RuntimeException {
    public DataAcquisitionException(String message) {
        super(message);
    }

    public DataAcquisitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
