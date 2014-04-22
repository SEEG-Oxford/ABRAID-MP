package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

/**
 * Ensures that the location is on land, according to the specified land-sea borders.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LandSnapper {
    private static final String ON_LAND_MESSAGE = "QC stage 2 passed: location already on land.";
    private static final String SNAPPED_MESSAGE = "QC stage 2 passed: location (%f,%f) snapped to land (distance " +
            "%.3fkm).";
    private static final String DISCARDED_MESSAGE = "QC stage 2 failed: location too distant from land (closest " +
            "point is (%f,%f) at distance %.3fkm).";

    private static final double MAXIMUM_DISTANCE = 5;

    private Point closestPoint;
    private String message;

    /**
     * Ensures that the location is on land, according to the specified land-sea borders. Calling getClosestPoint()
     * after this method will return:
     * - the location's existing point, if already on land
     * - the closest point on land to the location, if within the maximum distance to land
     * - null, if outside the maximum distance to land
     * @param location The location.
     * @param landSeaBorders The combined land-sea borders.
     */
    public void ensureOnLand(Location location, MultiPolygon landSeaBorders) {
        validateLocation(location);

        Point locationPoint = location.getGeom();
        closestPoint = GeometryUtils.findClosestPointOnGeometry(landSeaBorders, locationPoint);
        if (closestPoint.equalsExact(locationPoint)) {
            message = ON_LAND_MESSAGE;
        } else {
            double distance = GeometryUtils.findOrthodromicDistance(closestPoint, locationPoint);
            if (distance > MAXIMUM_DISTANCE) {
                message = String.format(DISCARDED_MESSAGE, closestPoint.getX(), closestPoint.getY(), distance);
                closestPoint = null;
            } else {
                message = String.format(SNAPPED_MESSAGE, locationPoint.getX(), locationPoint.getY(), distance);
            }
        }
    }

    public Point getClosestPoint() {
        return closestPoint;
    }

    public String getMessage() {
        return message;
    }

    private void validateLocation(Location location) {
        if (location.getGeom() == null) {
            throw new IllegalArgumentException("Location must have a point");
        }
    }
}
