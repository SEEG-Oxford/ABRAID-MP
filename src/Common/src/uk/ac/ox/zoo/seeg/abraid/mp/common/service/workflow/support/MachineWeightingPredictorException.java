package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

/**
 * Indicates that there was a problem during any aspect of machine weighting training or prediction.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MachineWeightingPredictorException extends RuntimeException {
    public MachineWeightingPredictorException(String message) {
        super(message);
    }
}
