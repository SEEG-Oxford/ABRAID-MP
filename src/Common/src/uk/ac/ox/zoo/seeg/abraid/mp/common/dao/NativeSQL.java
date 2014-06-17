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
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    Integer findAdminUnitThatContainsPoint(Point point, boolean isGlobal, Character adminLevel);

    /**
     * Loads the mean prediction raster for a model run.
     * @param modelRunId The model run's ID.
     * @param rasterColumnName The column name of the raster in the model_run table.
     * @return The mean prediction raster, in ASCII raster format.
     */
    byte[] loadRasterForModelRun(int modelRunId, String rasterColumnName);

    /**
     * Updates the specified model run to include the specified mean prediction raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The raster, in any GDAL format supported by the PostGIS database.
     * @param rasterColumnName The column name of the raster in the model_run table.
     */
    void updateRasterForModelRun(int modelRunId, byte[] gdalRaster, String rasterColumnName);

    /**
     * Updates the disease_extent table for the specified disease. This is done by using the
     * admin_unit_disease_extent_class table to aggregate the relevant geometries in the admin_unit_global/tropical
     * table.
     * @param diseaseGroupId The disease group ID.
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
}
