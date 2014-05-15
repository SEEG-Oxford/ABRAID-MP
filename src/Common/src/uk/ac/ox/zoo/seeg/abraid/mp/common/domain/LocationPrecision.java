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
    COUNTRY("c", null, null, 0),
    /**
     * The location is the centroid of a first-level administrative unit (e.g. state, province).
     */
    ADMIN1("l", '1', 1, 0.3),
    /**
     * The location is the centroid of a second-level administrative unit.
     */
    ADMIN2(null, '2', 2, 0.7),
    /**
     * The location is a point, or the centroid of an area, that is equal to or smaller than "admin 3" level
     * (for that area).
     */
    PRECISE("p", null, -999, 1);

    /**
     * The location precision string used by HealthMap (where it is called "place basic type").
     */
    private String healthMapPlaceBasicType;

    /**
     * The administrative level as specified in the shapefile tables (e.g. admin_unit).
     */
    private Character shapefileTableAdminLevel;

    /**
     * The value to be sent to, and used in, the model run.
     */
    private Integer modelValue;

    /**
     * The resolution weighting used in identifying points for validation and when calculating the validation weighting.
     */
    private double weighting;

    private LocationPrecision(String healthMapPlaceBasicType, Character shapefileTableAdminLevel, Integer modelValue, double weighting) {
        this.healthMapPlaceBasicType = healthMapPlaceBasicType;
        this.shapefileTableAdminLevel = shapefileTableAdminLevel;
        this.modelValue = modelValue;
        this.weighting = weighting;
    }

    public String getHealthMapPlaceBasicType() {
        return healthMapPlaceBasicType;
    }

    public char getShapefileTableAdminLevel() {
        return shapefileTableAdminLevel;
    }

    public Integer getModelValue() {
        return modelValue;
    }

    public double getWeighting() {
        return weighting;
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
