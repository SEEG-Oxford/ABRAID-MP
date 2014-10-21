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
     * Updates the disease_extent table for the specified disease. This is done by using the
     * admin_unit_disease_extent_class table to aggregate the relevant geometries in the admin_unit_global/tropical
     * table.
     * @param diseaseGroupId The disease group ID.
     * @param isGlobal True if the disease is global, false if tropical.
     */
    void updateAggregatedDiseaseExtent(int diseaseGroupId, boolean isGlobal);

    /**
     * Finds the suitability of the environment for the specified disease group to exist in the specified location.
     * This is taken from the mean prediction raster of the latest model run for the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param point The location.
     * @return The environmental suitability, or null if not found.
     */
    Double findEnvironmentalSuitability(int diseaseGroupId, Point point);

    /**
     * Calculates the distance between the specified point and the boundaries of the disease extent of the specified
     * disease group. If the specified point is within the disease extent, it returns zero.
     * @param diseaseGroupId The ID of the disease group.
     * @param point The location.
     * @return The distance outside the disease extent, or null if not found.
     */
    Double findDistanceOutsideDiseaseExtent(int diseaseGroupId, Point point);

    /**
     * Finds the nominal distance to be used for a point that is within a disease extent, based on the disease
     * extent class of the containing geometry.
     * @param diseaseGroupId The ID of the disease group.
     * @param isGlobal True if the disease is global, false if tropical.
     * @param point The location.
     * @return The nominal distance within the disease extent, or null if the point is not within the disease extent.
     */
    Double findDistanceWithinDiseaseExtent(int diseaseGroupId, boolean isGlobal, Point point);
}
