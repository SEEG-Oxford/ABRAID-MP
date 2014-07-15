package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * Represents the status of a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public enum ModelRunStatus {
    /**
     * The model run is in progress.
     */
    IN_PROGRESS("requested"),
    /**
     * The model run has completed.
     */
    COMPLETED("completed"),
    /**
     * The model run has failed.
     */
    FAILED("failed");

    private String displayText;

    ModelRunStatus(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
