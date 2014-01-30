package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Geometry utilities.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeometryUtils {
    // Enforces co-ordinate precision to 5 decimal places
    private static final PrecisionModel precisionModel = new PrecisionModel(100000);

    // Constructs a geometry, using the above precision and the co-ordinate system SRID 4326 (equivalent to WGS 84).
    private static final GeometryFactory geometryFactory = new GeometryFactory(precisionModel, 4326);

    /**
     * Creates a point using the system default precision and SRID.
     * @param x The X co-ordinate.
     * @param y The Y co-ordinate.
     * @return A point.
     */
    public static Point createPoint(double x, double y) {
        Coordinate coordinate = new Coordinate(x, y);
        precisionModel.makePrecise(coordinate);
        return geometryFactory.createPoint(coordinate);
    }
}
