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

    /** Query: Gets the precise or admin unit geom for a specified location id. */
    private static final String GET_LOCATION_GEOM =
            "SELECT " +
            "  CASE precision " +
            "    WHEN 'PRECISE' THEN l.geom " + // Use the lat/long
            "    WHEN 'ADMIN1' THEN qc.geom " + // Use the qc shape
            "    WHEN 'ADMIN2' THEN qc.geom " + // Use the qc shape
            "    WHEN 'COUNTRY' THEN CASE " +
            "      WHEN l.country_gaul_code IN (" +
            "        SELECT DISTINCT country_gaul_code " +
            "        FROM admin_unit_%1$s_view" +
            "        WHERE country_gaul_code is not null) " +
            "      THEN ( " + // if the country is in the extent map, use the amalgamation of its constituent shapes
            "        SELECT ST_COLLECT(geom) FROM (SELECT (ST_DUMP(geom)).geom " +
            "        FROM admin_unit_%1$s_view " +
            "        WHERE country_gaul_code=l.country_gaul_code) ex) " +
            "      ELSE c.geom " + // otherwise just use the country shape
            "    END " +
            "  END AS location_geom " +
            "FROM location AS l " +
            "LEFT OUTER JOIN admin_unit_qc AS qc ON l.admin_unit_qc_gaul_code=qc.gaul_code " +
            "LEFT OUTER JOIN country AS c ON l.country_gaul_code=c.gaul_code " +
            "WHERE l.id=:locationId ";

    /** Query: Gets the current geom for the disease extent of a specified disease group id. */
    private static final String GET_EXTENT_GEOM =
            "SELECT geom AS extent_geom " +
            "FROM disease_extent " +
            "WHERE disease_group_id=:diseaseGroupId ";

    /* Query: Calculates the distance between the specified location and any location inside of the disease extent of
              the specified disease. */
    public static final String DISTANCE_OUTSIDE_DISEASE_EXTENT =
            "WITH " +
            // Get the correct location geom
            "  location_geom AS ( " + GET_LOCATION_GEOM + " ), " +
            // Get the extent geom
            "  extent_geom AS ( " + GET_EXTENT_GEOM + " ), " +
            // Find the shortest line between the shapes
            "  line AS ( " +
            "    SELECT " +
            "      ST_ShortestLine(extent_geom, location_geom) AS line " +
            "    FROM location_geom " +
            "    CROSS JOIN extent_geom " +
            "  ) " +
            // Measure the line
            "SELECT ST_Length(geography(line))/1000 FROM line";

    /** Query: Calculates the distance between the specified location and any location outside of the disease extent of
              the specified disease. */
    public static final String DISTANCE_INSIDE_DISEASE_EXTENT =
            DISTANCE_OUTSIDE_DISEASE_EXTENT.replace("geom AS extent_geom", "outside_geom AS extent_geom");

    /** Other: Global. */
    public static final String GLOBAL = "global";
    /** Other: Tropical. */
    public static final String TROPICAL = "tropical";
}
