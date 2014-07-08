package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

/**
 * Interface to provide a mechanism for reporting model completion or failure (and results) to the model output handler.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelStatusReporter {
    /**
     * Report model completion or failure (and results) to the model output handler.
     * @param status The model status.
     * @param outputText The output text from the model.
     * @param errorText The error text (and/or exception text) from the model.
     */
    void report(ModelRunStatus status, String outputText, String errorText);
}
