package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

/**
 * Ensures that the location is within the specified geometry.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class Snapper {
    private static final String WITHIN_MESSAGE = "location already within %s";
    private static final String SNAPPED_MESSAGE = "location (%.5f,%.5f) snapped to %s (distance %.3fkm)";
    private static final String DISCARDED_MESSAGE = "location too distant from %s (closest point is (%.5f,%.5f) at " +
            "distance %.3fkm)";
    private static final String CANNOT_BE_SNAPPED_MESSAGE = "location cannot be snapped";

    private Point closestPoint;
    private String message;
    private boolean passed = true;
    private String geometryDescription;
    private double maximumDistance;

    public Snapper(String geometryDescription, double maximumDistance) {
        this.geometryDescription = geometryDescription;
        this.maximumDistance = maximumDistance;
    }

    /**
     * Ensures that the location is within the geometry. Calling getClosestPoint() after this method will return:
     * - the location's existing point, if already within the geometry
     * - the closest point on the geometry's borders to the location, if within the maximum distance
     * - null, if outside the maximum distance or there was a problem while snapping to borders
     * @param location The location.
     * @param geometry The geometry.
     */
    public void ensureWithinGeometry(Location location, MultiPolygon geometry) {
        validateLocation(location);

        Point locationPoint = location.getGeom();
        closestPoint = GeometryUtils.findClosestPointOnGeometry(geometry, locationPoint);
        if (closestPoint == null) {
            message = CANNOT_BE_SNAPPED_MESSAGE;
            passed = false;
        } else if (closestPoint.equalsExact(locationPoint)) {
            message = String.format(WITHIN_MESSAGE, geometryDescription);
        } else {
            double distance = GeometryUtils.findOrthodromicDistance(closestPoint, locationPoint);
            if (distance > maximumDistance) {
                message = String.format(DISCARDED_MESSAGE, geometryDescription, closestPoint.getX(),
                        closestPoint.getY(), distance);
                closestPoint = null;
                passed = false;
            } else {
                message = String.format(SNAPPED_MESSAGE, locationPoint.getX(), locationPoint.getY(),
                        geometryDescription, distance);
            }
        }
    }

    public Point getClosestPoint() {
        return closestPoint;
    }

    /**
     * Returns whether or not the location has passed this QC stage.
     * @return Whether or not the location has passed this QC stage.
     */
    public boolean hasPassed() {
        return passed;
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
