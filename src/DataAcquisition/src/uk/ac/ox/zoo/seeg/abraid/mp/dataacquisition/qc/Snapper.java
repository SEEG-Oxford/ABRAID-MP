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
    private static final String WITHIN_MESSAGE = "QC stage %d passed: location already within %s.";
    private static final String SNAPPED_MESSAGE = "QC stage %d passed: location (%f,%f) snapped to %s (distance " +
            "%.3fkm).";
    private static final String DISCARDED_MESSAGE = "QC stage %d failed: location too distant from %s (closest " +
            "point is (%f,%f) at distance %.3fkm).";

    private Point closestPoint;
    private String message;

    private int qcStage;
    private String geometryDescription;
    private double maximumDistance;

    public Snapper(int qcStage, String geometryDescription, double maximumDistance) {
        this.qcStage = qcStage;
        this.geometryDescription = geometryDescription;
        this.maximumDistance = maximumDistance;
    }

    /**
     * Ensures that the location is within the geometry. Calling getClosestPoint() after this method will return:
     * - the location's existing point, if already within the geometry
     * - the closest point on the geometry's borders to the location, if within the maximum distance
     * - null, if outside the maximum distance
     * @param location The location.
     * @param geometry The geometry.
     */
    public void ensureWithinGeometry(Location location, MultiPolygon geometry) {
        validateLocation(location);

        Point locationPoint = location.getGeom();
        closestPoint = GeometryUtils.findClosestPointOnGeometry(geometry, locationPoint);
        if (closestPoint.equalsExact(locationPoint)) {
            message = String.format(WITHIN_MESSAGE, qcStage, geometryDescription);
        } else {
            double distance = GeometryUtils.findOrthodromicDistance(closestPoint, locationPoint);
            if (distance > maximumDistance) {
                message = String.format(DISCARDED_MESSAGE, qcStage, geometryDescription, closestPoint.getX(),
                        closestPoint.getY(), distance);
                closestPoint = null;
            } else {
                message = String.format(SNAPPED_MESSAGE, qcStage, locationPoint.getX(), locationPoint.getY(),
                        geometryDescription, distance);
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
