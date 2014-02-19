package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * Describes how precise a location is.
 *
 * Copyright (c) 2014 University of Oxford
 */
public enum LocationPrecision {
    /**
     * The location is the centroid of a country (also known as "admin 0").
     */
    COUNTRY,
    /**
     * The location is the centroid of a first-level administrative unit (e.g. state, province).
     */
    ADMIN1,
    /**
     * The location is the centroid of a second-level administrative unit.
     */
    ADMIN2,
    /**
     * The location is a point, or the centroid of an area, that is equal to or smaller than "admin 3" level
     * (for that area).
     */
    PRECISE
}
