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
    COUNTRY("c"),
    /**
     * The location is the centroid of a first-level administrative unit (e.g. state, province).
     */
    ADMIN1("l", '1'),
    /**
     * The location is the centroid of a second-level administrative unit.
     */
    ADMIN2('2'),
    /**
     * The location is a point, or the centroid of an area, that is equal to or smaller than "admin 3" level
     * (for that area).
     */
    PRECISE("p");

    /**
     * The location precision string used by HealthMap (where it is called "place basic type").
     */
    private String healthMapPlaceBasicType;

    /**
     * The administrative level as specified in the shapefile tables (e.g. admin_unit).
     */
    private char shapefileTableAdminLevel;

    private LocationPrecision(String healthMapPlaceBasicType) {
        this.healthMapPlaceBasicType = healthMapPlaceBasicType;
    }

    private LocationPrecision(char shapefileTableAdminLevel) {
        this.shapefileTableAdminLevel = shapefileTableAdminLevel;
    }

    private LocationPrecision(String healthMapPlaceBasicType, char shapefileTableAdminLevel) {
        this.healthMapPlaceBasicType = healthMapPlaceBasicType;
        this.shapefileTableAdminLevel = shapefileTableAdminLevel;
    }

    public String getHealthMapPlaceBasicType() {
        return healthMapPlaceBasicType;
    }

    public char getShapefileTableAdminLevel() {
        return shapefileTableAdminLevel;
    }

    /**
     * Finds the location precision that has the specified HealthMap place basic type.
     * @param healthMapPlaceBasicType The HealthMap place basic type.
     * @return The first matching location precision, or null if not found.
     */
    public static LocationPrecision findByHealthMapPlaceBasicType(String healthMapPlaceBasicType) {
        for (LocationPrecision precision : values()) {
            String placeBasicType = precision.getHealthMapPlaceBasicType();
            if (placeBasicType != null && placeBasicType.equals(healthMapPlaceBasicType)) {
                return precision;
            }
        }
        return null;
    }
}
