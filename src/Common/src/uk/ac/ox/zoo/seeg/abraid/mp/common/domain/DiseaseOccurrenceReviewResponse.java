package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * The answer the expert has given as review of a disease occurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public enum DiseaseOccurrenceReviewResponse {
    /**
     * The expert believes the disease occurrence point is valid.
     */
    YES,
    /**
     * The expert believes the disease occurrence point is not valid.
     */
    NO,
    /**
     * The expert is unsure on validity of disease occurrence point.
     */
    UNSURE
}
