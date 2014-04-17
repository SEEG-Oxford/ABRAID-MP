package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geotools.referencing.datum.DefaultEllipsoid;

/**
 * Geometry utilities.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class GeometryUtils {
    // Enforces co-ordinate precision to 5 decimal places
    private static final PrecisionModel PRECISION_MODEL = new PrecisionModel(100000);

    // Constructs a geometry, using the above precision and the co-ordinate reference system SRID 4326
    // (equivalent to WGS 84).
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(PRECISION_MODEL, 4326);

    private static final double METRES_IN_A_KILOMETRE = 1000.0;
    private static final double RADIUS_OF_THE_EARTH_IN_KILOMETRES = 6371;

    private GeometryUtils() {
    }

    /**
     * Creates a point using the system default precision and SRID.
     * @param x The X co-ordinate.
     * @param y The Y co-ordinate.
     * @return A point.
     */
    public static Point createPoint(double x, double y) {
        Coordinate coordinate = new Coordinate(x, y);
        PRECISION_MODEL.makePrecise(coordinate);
        return GEOMETRY_FACTORY.createPoint(coordinate);
    }

    /**
     * Finds the shortest distance between two points on the surface of the Earth. This uses Vincenty's method
     * (assumes the Earth is an oblate ellipsoid). If this method fails to converge, it falls back to the Haversine
     * formula (assumes the Earth is a sphere).
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The orthodromic distance, in kilometres.
     */
    public static double findOrthodromicDistance(Point point1, Point point2) {
        // Ideally we would use GeodeticCalculator.getOrthodromicDistance() for this, but its internal call
        // to computeDirection() fails to converge for many real-life points. Instead we call the method on
        // DefaultEllipsoid directly. Its results largely match PostGIS's ST_DISTANCE function applied to geography
        // types.
        try {
            return DefaultEllipsoid.WGS84.orthodromicDistance(point1.getX(), point1.getY(),
                    point2.getX(), point2.getY()) / METRES_IN_A_KILOMETRE;
        } catch (ArithmeticException e) {
            // Failed to converge, so fall back to using a sphere (like PostGIS does). Uses the Haversine formula.
            double latDistance = Math.toRadians(point2.getY() - point1.getY());
            double lonDistance = Math.toRadians(point2.getX() - point1.getX());
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                       Math.cos(Math.toRadians(point1.getY())) * Math.cos(Math.toRadians(point2.getY())) *
                       Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return RADIUS_OF_THE_EARTH_IN_KILOMETRES * c;
        }
    }
}
