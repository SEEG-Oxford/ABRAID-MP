package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

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

    /** Query: Finds the first land-sea border pixel that contains the specified point, using ST_INTERSECTS as above. */
    public static final String LAND_SEA_BORDER_CONTAINS_POINT_QUERY =
            "SELECT MIN(id) FROM land_sea_border WHERE ST_Intersects(geom, :point)";

    /** Query: Create an extent geometry by aggregating the geometries in admin_unit_global/tropical, based on
     relevant rows in admin_unit_disease_extent_class. The geometries are aggregated by applying ST_Dump (split
     multipolygons into polygons) then ST_Collect (concatenate polygons into a multipolygon); this is much quicker
     than ST_Union which dissolves boundaries between polygons. */
    private static final String CREATE_EXTENT_GEOM =
            "SELECT ST_Collect(geom) FROM (" +
                    "SELECT (ST_Dump(a.geom)).geom " +
                    "FROM admin_unit_disease_extent_class c " +
                    "JOIN admin_unit_%1$s a ON c.%1$s_gaul_code = a.gaul_code " +
                    "WHERE c.disease_group_id = :diseaseGroupId " +
                    "AND c.disease_extent_class IN ('" +
                            DiseaseExtentClass.POSSIBLE_PRESENCE + "', '" +
                            DiseaseExtentClass.PRESENCE +
                    "')" +
            ") x";

    /** Query: Updates a disease extent with the current extent and outside extent geoms. */
    public static final String UPDATE_DISEASE_EXTENT_QUERY =
            "UPDATE disease_extent " +
            "SET " +
            "    geom =  (" + CREATE_EXTENT_GEOM + "), " +
            "    outside_geom = (" + CREATE_EXTENT_GEOM.replace(" IN ", " NOT IN ") + ") " +
            "WHERE disease_group_id = :diseaseGroupId";

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

    /** Query: Calculates the distance between the specified location and any location outside of the disease extent of
     the specified disease. */
    public static final String DISTANCE_WITHIN_DISEASE_EXTENT = DISTANCE_OUTSIDE_DISEASE_EXTENT;

    /** Other: Global. */
    public static final String GLOBAL = "global";
    /** Other: Tropical. */
    public static final String TROPICAL = "tropical";
}
