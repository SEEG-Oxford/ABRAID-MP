package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;

/**
 * Interface for routines that interact with the PostGIS database using native SQL.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface NativeSQL {
    /**
     * Finds the first admin unit that contains the specified point.
     * @param point The point.
     * @param isGlobal True to use admin units for global diseases, otherwise false.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    Integer findAdminUnitThatContainsPoint(Point point, boolean isGlobal);

    /**
     * Finds the country that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the country that contains the specified point.
     */
    Integer findCountryThatContainsPoint(Point point);

    /**
     * Determines whether one of the land-sea border geometries contains the point.
     * @param point The point.
     * @return True if the point is on land, otherwise false.
     */
    boolean doesLandSeaBorderContainPoint(Point point);

    /**
     * Updates the disease_extent table for the specified disease. This is done by using the
     * admin_unit_disease_extent_class table to aggregate the relevant geometries in the admin_unit_global/tropical
     * table.
     * @param diseaseGroupId The disease group ID.
     * @param isGlobal True if the disease is global, false if tropical.
     */
    void updateAggregatedDiseaseExtent(int diseaseGroupId, boolean isGlobal);

    /**
     * Calculates the distance between the specified location and the boundaries of the disease extent of the specified
     * disease group. If the specified point is within the disease extent, it returns zero.
     * @param diseaseGroupId The ID of the disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param locationId The location.
     * @return The distance outside the disease extent, or 0.
     */
    Double findDistanceOutsideDiseaseExtent(int diseaseGroupId, boolean isGlobal, int locationId);

    /**
     * Calculates the distance between the specified location and the boundaries of the nearest admin unit not in the
     * disease extent of the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param locationId The location.
     * @return The distance inside the disease extent, or 0.
     */
    Double findDistanceInsideDiseaseExtent(int diseaseGroupId, boolean isGlobal, int locationId);
}

