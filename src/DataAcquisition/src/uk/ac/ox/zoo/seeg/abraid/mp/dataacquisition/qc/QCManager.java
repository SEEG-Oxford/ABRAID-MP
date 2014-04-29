package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

/**
 * The Quality Control (QC) manager. Performs quality control on a location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCManager {
    private static final String QC_STAGE_1_PASSED_MESSAGE = "QC stage 1 passed: location not an ADMIN1 or ADMIN2.";
    private static final String QC_STAGE_2_PASSED_MESSAGE = "QC stage 2 passed: location is a country.";
    private static final String QC_STAGE_3_PASSED_MESSAGE = "QC stage 3 passed: no country geometries associated " +
            "with this location.";

    private static final int NO_QC_STAGE = 0;
    private static final int QC_STAGE_1_ID = 1;
    private static final int QC_STAGE_2_ID = 2;
    private static final int QC_STAGE_3_ID = 3;
    private static final int QC_STAGE_2_MAXIMUM_DISTANCE = 5;
    private static final int QC_STAGE_3_MAXIMUM_DISTANCE = 5;

    private QCLookupData qcLookupData;

    public QCManager(QCLookupData qcLookupData) {
        this.qcLookupData = qcLookupData;
    }

    /**
     * Performs quality control (QC) on a location. Each QC step is performed until a step fails.
     * @param location The location.
     * @return The final QC step that passed (1-3), or 0 if no steps passed.
     */
    public int performQC(Location location) {
        initializeLocationForQC(location);

        if (!performQCStage1(location)) {
            return NO_QC_STAGE;
        }

        if (!performQCStage2(location)) {
            return QC_STAGE_1_ID;
        }

        if (!performQCStage3(location)) {
            return QC_STAGE_2_ID;
        }

        return QC_STAGE_3_ID;
    }

    private void initializeLocationForQC(Location location) {
        location.setAdminUnitQC(null);
        location.setQcMessage(null);
    }

    private boolean performQCStage1(Location location) {
        boolean passed = true;

        if (isAdmin1OrAdmin2(location)) {
            // Location is an admin1 or admin2, so find the closest admin unit to the location
            // (as long as it is close enough - see class AdminUnitFinder for details)
            AdminUnitFinder adminUnitFinder = new AdminUnitFinder();
            adminUnitFinder.findClosestAdminUnit(location, qcLookupData.getAdminUnits());
            location.setAdminUnitQC(adminUnitFinder.getClosestAdminUnit());
            appendQcMessage(location, adminUnitFinder.getMessage());
            passed = (adminUnitFinder.getClosestAdminUnit() != null);
        } else {
            appendQcMessage(location, QC_STAGE_1_PASSED_MESSAGE);
        }

        return passed;
    }

    private boolean performQCStage2(Location location) {
        boolean passed = true;

        if (!isCountry(location)) {
            // Ensure that the location is on land. If not, snap to land if within the maximum distance away.
            Snapper snapper = new Snapper(QC_STAGE_2_ID, "land", QC_STAGE_2_MAXIMUM_DISTANCE);
            passed = applySnapperToLocation(snapper, location, qcLookupData.getLandSeaBorders());
        } else {
            appendQcMessage(location, QC_STAGE_2_PASSED_MESSAGE);
        }

        return passed;
    }

    private boolean performQCStage3(Location location) {
        // Ensure that the location is within the geometries associated with the location's HealthMap country. If not,
        // snap to the geometries if within the maximum distance away.
        boolean passed = true;

        if (location.getHealthMapCountryId() != null) {
            MultiPolygon countryGeometry = qcLookupData.getHealthMapCountryGeometryMap().get(
                    location.getHealthMapCountryId());
            if (countryGeometry != null) {
                Snapper snapper = new Snapper(QC_STAGE_3_ID, "HealthMap country", QC_STAGE_3_MAXIMUM_DISTANCE);
                passed = applySnapperToLocation(snapper, location, countryGeometry);
            } else {
                appendQcMessage(location, QC_STAGE_3_PASSED_MESSAGE);
            }
        } else {
            appendQcMessage(location, QC_STAGE_3_PASSED_MESSAGE);
        }

        return passed;
    }

    private boolean isAdmin1OrAdmin2(Location location) {
        return (location.getPrecision() == LocationPrecision.ADMIN1) ||
                (location.getPrecision() == LocationPrecision.ADMIN2);
    }

    private boolean isCountry(Location location) {
        return (location.getPrecision() == LocationPrecision.COUNTRY);
    }

    private boolean applySnapperToLocation(Snapper snapper, Location location, MultiPolygon geometry) {
        boolean passed = true;

        if (geometry != null) {
            snapper.ensureWithinGeometry(location, geometry);
            Point closestPoint = snapper.getClosestPoint();
            if (closestPoint != null) {
                // Set the location to the returned closest point, which has been snapped if necessary
                location.setGeom(closestPoint);
            }
            appendQcMessage(location, snapper.getMessage());

            // Passes if a closest point was returned (if null, the closest point is too far away from the geometry)
            passed = (closestPoint != null);
        }

        return passed;
    }

    private void appendQcMessage(Location location, String qcMessage) {
        if (StringUtils.hasText(qcMessage)) {
            String locationQcMessage = location.getQcMessage();
            if (locationQcMessage == null) {
                locationQcMessage = "";
            }
            if (StringUtils.hasText(locationQcMessage)) {
                // QC messages are separated by a single space
                locationQcMessage += " ";
            }
            locationQcMessage += qcMessage;
            location.setQcMessage(locationQcMessage);
        }
    }
}
