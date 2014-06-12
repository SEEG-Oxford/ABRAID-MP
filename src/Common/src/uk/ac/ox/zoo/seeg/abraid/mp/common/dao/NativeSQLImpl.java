package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

/**
 * Contains routines that interact with the PostGIS database using native SQL.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLImpl implements NativeSQL {
    // Query to find the first admin unit that contains the specified point. We use ST_INTERSECTS rather than
    // ST_CONTAINS because the former returns true if the point is on the geometry border.
    private static final String ADMIN_UNIT_CONTAINS_POINT_QUERY =
            "SELECT MIN(gaul_code) FROM %s WHERE ST_Intersects(geom, :point)";
    private static final String ADMIN_UNIT_CONTAINS_POINT_LEVEL_FILTER = " AND level = :adminLevel";
    private static final String ADMIN_UNIT_GLOBAL_TABLE_NAME = "admin_unit_global";
    private static final String ADMIN_UNIT_TROPICAL_TABLE_NAME = "admin_unit_tropical";

    // Queries to load and save a model run's output raster. Converts to and from a GDAL raster format.
    private static final String LOAD_RASTER_QUERY =
            "SELECT ST_AsGDALRaster(%s, :gdalFormat) FROM model_run WHERE id = :id";
    private static final String UPDATE_RASTER_QUERY =
            "UPDATE model_run SET %s = ST_FromGDALRaster(:gdalRaster, :srid) WHERE id = :id";
    // This is PostGIS's name for the ESRI ASCII Raster format
    private static final String GDAL_RASTER_FORMAT = "AAIGrid";

    /** The column name of the mean prediction raster in the model_run table. */
    public static final String MEAN_PREDICTION_RASTER_COLUMN_NAME = "mean_prediction_raster";
    /** The column name of the prediction uncertainty raster in the model_run table. */
    public static final String PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME = "prediction_uncertainty_raster";

    private SessionFactory sessionFactory;

    public NativeSQLImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Finds the first admin unit for global diseases that contains the specified point.
     * @param point The point.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    @Override
    public Integer findAdminUnitGlobalThatContainsPoint(Point point, Character adminLevel) {
        return findAdminUnitThatContainsPoint(point, adminLevel, ADMIN_UNIT_GLOBAL_TABLE_NAME);
    }

    /**
     * Finds the first admin unit for tropical diseases that contains the specified point.
     * @param point The point.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first tropical admin unit that contains the specified point, or null if no
     * admin units found.
     */
    @Override
    public Integer findAdminUnitTropicalThatContainsPoint(Point point, Character adminLevel) {
        return findAdminUnitThatContainsPoint(point, adminLevel, ADMIN_UNIT_TROPICAL_TABLE_NAME);
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
        return (byte[]) createSQLQuery(query)
                .setParameter("gdalFormat", GDAL_RASTER_FORMAT)
                .setParameter("id", modelRunId)
                .uniqueResult();
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
        createSQLQuery(query)
                .setParameter("id", modelRunId)
                .setParameter("gdalRaster", gdalRaster)
                .setParameter("srid", GeometryUtils.SRID_FOR_WGS_84)
                .executeUpdate();
    }

    private Integer findAdminUnitThatContainsPoint(Point point, Character adminLevel, String tableName) {
        String queryString = String.format(ADMIN_UNIT_CONTAINS_POINT_QUERY, tableName);
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

    private SQLQuery createSQLQuery(String query) {
        return sessionFactory.getCurrentSession().createSQLQuery(query);
    }
}
