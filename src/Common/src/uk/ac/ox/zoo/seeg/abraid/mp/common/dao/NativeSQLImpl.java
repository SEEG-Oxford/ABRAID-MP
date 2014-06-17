package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import static uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLConstants.*;

/**
 * Contains routines that interact with the PostGIS database using native SQL.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLImpl implements NativeSQL {
    private SessionFactory sessionFactory;

    public NativeSQLImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Finds the first admin unit that contains the specified point.
     * @param point The point.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    @Override
    public Integer findAdminUnitThatContainsPoint(Point point, boolean isGlobal, Character adminLevel) {
        String queryString = String.format(ADMIN_UNIT_CONTAINS_POINT_QUERY, isGlobal ? GLOBAL : TROPICAL);
        if (adminLevel != null) {
            queryString += ADMIN_UNIT_CONTAINS_POINT_LEVEL_FILTER;
        }

        SQLQuery query = createSQLQuery(queryString);
        query.setParameter("point", point);
        if (adminLevel != null) {
            query.setParameter("adminLevel", adminLevel);
        }

        return (Integer) query.uniqueResult();
    }

    /**
     * Loads the mean prediction raster for a model run.
     * @param modelRunId The model run's ID.
     * @param rasterColumnName The column name of the raster in the model_run table.
     * @return The mean prediction raster, in ASCII raster format.
     */
    @Override
    public byte[] loadRasterForModelRun(int modelRunId, String rasterColumnName) {
        String query = String.format(LOAD_RASTER_QUERY, rasterColumnName);
        return (byte[]) uniqueResult(query, "gdalFormat", GDAL_RASTER_FORMAT, "id", modelRunId);
    }

    /**
     * Updates the specified model run to include the specified mean prediction raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The prediction uncertainty raster, in any GDAL format supported by the PostGIS database.
     * @param rasterColumnName The column name of the raster in the model_run table.
     */
    @Override
    public void updateRasterForModelRun(int modelRunId, byte[] gdalRaster, String rasterColumnName) {
        String query = String.format(UPDATE_RASTER_QUERY, rasterColumnName);
        executeUpdate(query, "id", modelRunId, "gdalRaster", gdalRaster, "srid", GeometryUtils.SRID_FOR_WGS_84);
    }

    /**
     * Updates the disease_extent table for the specified disease. This is done by using the
     * admin_unit_disease_extent_class table to aggregate the relevant geometries in the admin_unit_global/tropical
     * table.
     * @param diseaseGroupId The disease group ID.
     * @param isGlobal True if the disease is global, false if tropical.
     */
    @Override
    public void updateAggregatedDiseaseExtent(int diseaseGroupId, boolean isGlobal) {
        executeUpdate(DELETE_DISEASE_EXTENT_QUERY, "diseaseGroupId", diseaseGroupId);
        String insertQuery = String.format(INSERT_DISEASE_EXTENT_QUERY, isGlobal ? GLOBAL : TROPICAL);
        executeUpdate(insertQuery, "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Finds the suitability of the environment for the specified disease group to exist in the specified location.
     * This is taken from the mean prediction raster of the latest model run for the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param point The location.
     * @return The environmental suitability, or null if not found.
     */
    @Override
    public Double findEnvironmentalSuitability(int diseaseGroupId, Point point) {
        return (Double) uniqueResult(ENV_SUITABILITY_QUERY, "diseaseGroupId", diseaseGroupId, "geom", point);
    }

    private SQLQuery createSQLQuery(String queryString) {
        return sessionFactory.getCurrentSession().createSQLQuery(queryString);
    }

    private Object uniqueResult(String queryString, Object... parameterNamesAndValues) {
        return getParameterisedSQLQuery(queryString, parameterNamesAndValues).uniqueResult();
    }

    private Object executeUpdate(String queryString, Object... parameterNamesAndValues) {
        return getParameterisedSQLQuery(queryString, parameterNamesAndValues).executeUpdate();
    }

    private SQLQuery getParameterisedSQLQuery(String queryString, Object... parameterNamesAndValues) {
        SQLQuery query = createSQLQuery(queryString);
        for (int i = 0; i < parameterNamesAndValues.length; i += 2) {
            query.setParameter((String) parameterNamesAndValues[i], parameterNamesAndValues[i + 1]);
        }
        return query;
    }
}
