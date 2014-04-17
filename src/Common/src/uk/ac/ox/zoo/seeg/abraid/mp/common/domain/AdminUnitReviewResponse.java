package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * The answer the expert has submitted as review of an administrative unit polygon.
 * Copyright (c) 2014 University of Oxford
 */
public enum AdminUnitReviewResponse {
    /**
     * The disease group is definitely present in the admin unit.
     */
    PRESENCE,
    /**
     * The disease group may be present in the admin unit.
     */
    POSSIBLE_PRESENCE,
    /**
     * It is unknown whether the disease group is present or absent.
     */
    UNCERTAIN,
    /**
     * The disease group may not be present in the admin unit.
     */
    POSSIBLE_ABSENCE,
    /**
     * The disease group is definitely not present in the admin unit.
     */
    ABSENCE
}
