package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

/**
 * Contains constants (e.g. queries, table/column names) for use with native SQL routines.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class NativeSQLConstants {
    private NativeSQLConstants() {
    }

    /** Query: finds the first admin unit that contains the specified point. We use ST_INTERSECTS rather than
        ST_CONTAINS because the former returns true if the point is on the geometry border. */
    public static final String ADMIN_UNIT_CONTAINS_POINT_QUERY =
            "SELECT MIN(gaul_code) FROM admin_unit_%s WHERE ST_Intersects(geom, :point)";
    /** Filter for above query. */
    public static final String ADMIN_UNIT_CONTAINS_POINT_LEVEL_FILTER = " AND level = :adminLevel";

    /** Query: Loads a model run's output raster. Converts to the specified GDAL raster format. */
    public static final String LOAD_RASTER_QUERY =
            "SELECT ST_AsGDALRaster(%s, :gdalFormat) FROM model_run WHERE id = :id";
    /** Query: Saves a model run's output raster. Converts from any supported GDAL raster format. */
    public static final String UPDATE_RASTER_QUERY =
            "UPDATE model_run SET %s = ST_FromGDALRaster(:gdalRaster, :srid) WHERE id = :id";

    /** Query: Deletes a disease extent. */
    public static final String DELETE_DISEASE_EXTENT_QUERY =
            "DELETE FROM disease_extent WHERE disease_group_id = :diseaseGroupId";

    /** Query: Inserts a disease extent by aggregating the geometries in admin_unit_global/tropical, based on
        relevant rows in admin_unit_disease_extent_class. The geometries are aggregated by applying ST_DUMP (split
        multipolygons into polygons) then ST_COLLECT (concatenate polygons into a multipolygon); this is much quicker
        than ST_UNION which dissolves boundaries between polygons. */
    public static final String INSERT_DISEASE_EXTENT_QUERY =
            "INSERT INTO disease_extent (disease_group_id, geom) " +
            "SELECT :diseaseGroupId, ST_COLLECT(x.geom) " +
            "FROM " +
            "    (SELECT (ST_DUMP(a.geom)).geom " +
            "     FROM admin_unit_disease_extent_class c " +
            "     JOIN admin_unit_%1$s a ON c.%1$s_gaul_code = a.gaul_code " +
            "     WHERE disease_group_id = :diseaseGroupId " +
            "     AND disease_extent_class IN ('" + DiseaseExtentClass.POSSIBLE_PRESENCE + "', '" +
                                                    DiseaseExtentClass.PRESENCE + "')" +
            "    ) x";

    /** Query: Finds the environmental suitability for a disease group to exist at a point. This is taken from the
        mean prediction raster of the latest successful model run for the disease group. */
    public static final String ENV_SUITABILITY_QUERY =
            "SELECT DISTINCT ON (disease_group_id) ST_Value(mean_prediction_raster, :geom) " +
            "FROM model_run " +
            "WHERE disease_group_id = :diseaseGroupId " +
            "AND status = '" + ModelRunStatus.COMPLETED + "' " +
            "AND mean_prediction_raster IS NOT NULL " +
            "ORDER BY disease_group_id, response_date DESC";

    /** Column name: model_run.mean_prediction_raster. */
    public static final String MEAN_PREDICTION_RASTER_COLUMN_NAME = "mean_prediction_raster";
    /** Column name: model_run.prediction_uncertainty_raster. */
    public static final String PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME = "prediction_uncertainty_raster";

    /** Other: Global. */
    public static final String GLOBAL = "global";
    /** Other: Tropical. */
    public static final String TROPICAL = "tropical";
    /** Other: PostGIS's name for the ESRI ASCII Raster format. */
    public static final String GDAL_RASTER_FORMAT = "AAIGrid";
}
