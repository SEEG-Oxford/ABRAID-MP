package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * Represents the status of a disease occurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public enum DiseaseOccurrenceStatus {
    /**
     * The occurrence has been discarded because its location failed QC.
     */
    DISCARDED_FAILED_QC,
    /**
     * The occurrence has been discarded because although it was shown on the Data Validator, it was not reviewed.
     */
    DISCARDED_UNREVIEWED,
    /**
     * The occurrence has been discarded because it was unused during disease group set-up.
     */
    DISCARDED_UNUSED,
    /**
     * The occurrence is currently in review on the Data Validator.
     */
    IN_REVIEW,
    /**
     * The occurrence is ready for consideration for a model run or disease extent generation.
     */
    READY,
    /**
     * The occurrence has not yet become part of a batch during disease group set-up.
     */
    AWAITING_BATCHING,
    /**
     * The occurrence should only be used for sample bias modelling data sets.
     */
    BIAS
}
