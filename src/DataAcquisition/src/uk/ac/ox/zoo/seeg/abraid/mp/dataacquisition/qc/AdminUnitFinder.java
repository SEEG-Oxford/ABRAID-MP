package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.List;

/**
 * QC stage 1: find the admin1 or admin2 that is associated with a location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitFinder {
    // Maximum width and height of the admin units (in degrees)
    private static final int MAX_ADMIN_UNIT_WIDTH = 61;
    private static final int MAX_ADMIN_UNIT_HEIGHT = 32;

    // The maximum distance that a location can be from an admin unit's centroid, expressed as a percentage of the
    // square root of the admin unit's area
    private static final double MAXIMUM_PERCENTAGE_OF_SQUARE_ROOT_OF_AREA = 30;

    private static final String FOUND_MESSAGE = "QC stage 1 passed: closest distance is %.2f%% of the square root of " +
            "the area.";
    private static final String NOT_FOUND_MESSAGE = "QC stage 1 failed: closest distance is %.2f%% of the square " +
            "root of the area (GAUL code %d: \"%s\").";

    private static final double RATIO_TO_PERCENTAGE = 100.0;
    private static final int MAX_LONGITUDE = 180;
    private static final int MAX_LATITUDE = 90;

    // The admin unit that is closest to the location, with the closest distance, within the maximum distance
    // allowed.
    private AdminUnitQC closestAdminUnit;
    private double closestDistance = 0;

    // A message that is created when a close enough admin unit is not found.
    private String message;

    /**
     * Finds the closest admin unit associated with the specified location. To do this, we find the distance between
     * each admin unit's centroid and the specified location, ignoring distances that are greater than the maximum
     * allowed distance for the admin unit (see method getMaximumDistanceFromCentroid).
     *
     * @param location The location. Must be ADMIN1 or ADMIN2.
     * @param adminUnits A list of admin units for comparison.
     */
    public void findClosestAdminUnit(Location location, List<AdminUnitQC> adminUnits) {
        validateLocation(location);
        char adminLevel = location.getPrecision().getShapefileTableAdminLevel();

        // The admin unit that is closest to the location, with the closest distance. This is stored for logging
        // purposes only.
        AdminUnitQC closestAdminUnitForLogging = null;
        double closestDistanceForLogging = 0;

        for (AdminUnitQC adminUnit : adminUnits) {
            if (adminUnit.getAdminLevel() == adminLevel) {
                // This admin unit is at the desired level
                // So find the distance between the input location and the admin unit's centroid
                Point adminUnitCentroid = GeometryUtils.createPoint(
                        adminUnit.getCentroidLongitude(), adminUnit.getCentroidLatitude());

                if (!isDistanceBeyondMaximumAdminUnitSize(location.getGeom(), adminUnitCentroid)) {
                    double distance = GeometryUtils.findOrthodromicDistance(location.getGeom(), adminUnitCentroid);
                    // If this is the closest admin unit so far, within the maximum distance allowed, store it
                    if (distance < getMaximumDistanceFromCentroid(adminUnit) &&
                            (closestAdminUnit == null || distance < closestDistance)) {
                        closestAdminUnit = adminUnit;
                        closestDistance = distance;
                    }

                    // If this is the closest admin unit so far, store it so that we can log its details if there are
                    // no admin units within the maximum distance allowed
                    if (closestAdminUnitForLogging == null || distance < closestDistanceForLogging) {
                        closestAdminUnitForLogging = adminUnit;
                        closestDistanceForLogging = distance;
                    }
                }
            }
        }

        // If no sufficiently close admin unit is found, log the closest match
        if (closestAdminUnit != null) {
            double percentage = percentageOfSquareRootOfArea(closestAdminUnit, closestDistance);
            message = String.format(FOUND_MESSAGE, percentage);
        } else if (closestAdminUnitForLogging != null) {
            double percentage = percentageOfSquareRootOfArea(closestAdminUnitForLogging,
                    closestDistanceForLogging);
            message = String.format(NOT_FOUND_MESSAGE, percentage,
                    closestAdminUnitForLogging.getGaulCode(), closestAdminUnitForLogging.getName());
        }
    }

    /**
     * Gets the closest admin unit.
     * @return The admin unit whose centroid is closest to the specified location, as long as the distance is within
     * the maximum allowed. Returns null if no such centroid exists.
     */
    public AdminUnitQC getClosestAdminUnit() {
        return closestAdminUnit;
    }

    /**
     * Gets the message.
     * @return A message explaining the result of this QC stage.
     */
    public String getMessage() {
        return message;
    }

    private void validateLocation(Location location) {
        if (location.getPrecision() != LocationPrecision.ADMIN1 &&
                location.getPrecision() != LocationPrecision.ADMIN2) {
            throw new IllegalArgumentException("Location must be an admin1 or admin2");
        }

        if (location.getGeom() == null) {
            throw new IllegalArgumentException("Location must have a point");
        }
    }

    private double getMaximumDistanceFromCentroid(AdminUnitQC adminUnit) {
        return Math.sqrt(adminUnit.getArea()) * MAXIMUM_PERCENTAGE_OF_SQUARE_ROOT_OF_AREA / RATIO_TO_PERCENTAGE;
    }

    private double percentageOfSquareRootOfArea(AdminUnitQC adminUnit, double distance) {
        return distance * RATIO_TO_PERCENTAGE / Math.sqrt(adminUnit.getArea());
    }

    private boolean isDistanceBeyondMaximumAdminUnitSize(Point location, Point adminUnitCentroid) {
        // The admin units are never greater than 61 degrees wide and 32 degrees high. So if the distance
        // between the points is greater than this, the location cannot possibly be close enough to the centroid.
        // This routine avoids calculating the orthodromic distance for such points.
        return (wrappedDifference(location.getX(), adminUnitCentroid.getX(), MAX_LONGITUDE) > MAX_ADMIN_UNIT_WIDTH) ||
               (wrappedDifference(location.getY(), adminUnitCentroid.getY(), MAX_LATITUDE) > MAX_ADMIN_UNIT_HEIGHT);
    }

    private double wrappedDifference(double a, double b, double maximumDifference) {
        double difference = Math.abs(a - b);
        return (difference > maximumDifference) ? (maximumDifference * 2 - difference) : difference;
    }
}
