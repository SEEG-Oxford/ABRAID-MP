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

    /** Query: Finds the first admin unit that contains the specified point. We use ST_INTERSECTS rather than
        ST_CONTAINS because the former returns true if the point is on the geometry border. */
    public static final String ADMIN_UNIT_CONTAINS_POINT_QUERY =
            "SELECT MIN(gaul_code) FROM admin_unit_%s WHERE ST_Intersects(geom, :point)";

    /** Query: Finds the first country that contains the specified point, using ST_INTERSECTS as above. */
    public static final String COUNTRY_CONTAINS_POINT_QUERY =
            "SELECT MIN(gaul_code) FROM country WHERE ST_Intersects(geom, :point)";

    private static final String GDAL_OPTIONS = "'COMPRESS=DEFLATE', 'ZLEVEL=9'";

    /** Query: Loads a model run's output raster. Converts to the specified GDAL raster format. */
    public static final String LOAD_RASTER_QUERY =
            "SELECT ST_AsGDALRaster(%s, :gdalFormat, ARRAY[" + GDAL_OPTIONS + "]) FROM model_run WHERE id = :id";
    /** Query: Saves a model run's output raster. Converts from any supported GDAL raster format. */
    public static final String UPDATE_RASTER_QUERY =
            "UPDATE model_run SET %s = ST_FromGDALRaster(:gdalRaster, :srid) WHERE id = :id";

    /** Clause for selecting relevant disease extent rows in the queries below. */
    private static final String DISEASE_EXTENT_CLAUSE =
            "FROM admin_unit_disease_extent_class c " +
            "JOIN admin_unit_%1$s a ON c.%1$s_gaul_code = a.gaul_code " +
            "WHERE c.disease_group_id = :diseaseGroupId " +
            "AND c.disease_extent_class IN ('" + DiseaseExtentClass.POSSIBLE_PRESENCE + "', '" +
                                                 DiseaseExtentClass.PRESENCE + "')";

    /** Query: Updates a disease extent by aggregating the geometries in admin_unit_global/tropical, based on
     relevant rows in admin_unit_disease_extent_class. The geometries are aggregated by applying ST_DUMP (split
     multipolygons into polygons) then ST_COLLECT (concatenate polygons into a multipolygon); this is much quicker
     than ST_UNION which dissolves boundaries between polygons. */
    public static final String UPDATE_DISEASE_EXTENT_QUERY =
            "UPDATE disease_extent " +
            "SET geom = (SELECT ST_COLLECT(geom) FROM " +
                    "(SELECT (ST_DUMP(a.geom)).geom " + DISEASE_EXTENT_CLAUSE + ") x) " +
            "WHERE disease_group_id = :diseaseGroupId";

    /** Query: Finds the environmental suitability for a disease group to exist at a point. This is taken from the
        mean prediction raster of the latest successful model run for the disease group. */
    public static final String ENV_SUITABILITY_QUERY =
            "SELECT ST_Value(mean_prediction_raster, :geom) " +
            "FROM model_run " +
            "WHERE id IN" +
            "    (SELECT DISTINCT ON (disease_group_id) id" +
            "    FROM model_run" +
            "    WHERE disease_group_id = :diseaseGroupId" +
            "    AND status = '" + ModelRunStatus.COMPLETED + "'" +
            "    AND mean_prediction_raster IS NOT NULL" +
            "    ORDER BY disease_group_id, response_date DESC)";

    /** Query: Calculates the distance between the specified point and the disease extent of the specified disease
               group, as follows:
               1. ST_ClosestPoint: Find the closest point on the disease extent to the specified point. If the point
                  is within the disease extent, this returns the specified point itself (giving a distance of 0).
               2. ST_Distance: Find the orthodromic (surface) distance between the closest point and the specified
                  point. In theory this can operate without the use of ST_ClosestPoint, but it is much slower.
               3. Return the value in kilometres by dividing by 1000. */
    public static final String DISTANCE_OUTSIDE_DISEASE_EXTENT =
            "SELECT ST_Distance(GEOGRAPHY(ST_ClosestPoint(geom, :geom)), GEOGRAPHY(:geom)) / 1000 " +
            "FROM disease_extent " +
            "WHERE disease_group_id = :diseaseGroupId";

    /** Query: Finds the nominal distance to be used for a point that is within a disease extent, based on the
               disease extent class of the containing geometry. */
    public static final String DISTANCE_WITHIN_DISEASE_EXTENT =
            "SELECT distance_if_within_extent " +
            "FROM disease_extent_class " +
            "WHERE name IN" +
            "    (SELECT c.disease_extent_class " + DISEASE_EXTENT_CLAUSE +
            "     AND ST_Intersects(a.geom, :geom))";

    /** Column name: model_run.mean_prediction_raster. */
    public static final String MEAN_PREDICTION_RASTER_COLUMN_NAME = "mean_prediction_raster";
    /** Column name: model_run.prediction_uncertainty_raster. */
    public static final String PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME = "prediction_uncertainty_raster";

    /** Other: Global. */
    public static final String GLOBAL = "global";
    /** Other: Tropical. */
    public static final String TROPICAL = "tropical";
    /** Other: PostGIS's name for the GeoTiff raster format. */
    public static final String GEOTIFF_RASTER_FORMAT = "GTiff";
}
