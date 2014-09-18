package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

/**
 * The Quality Control (QC) manager. Performs quality control on a location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCManager {
    private static final String MESSAGE_FORMAT = "QC stage %d %s: %s.";
    private static final String PASSED = "passed";
    private static final String FAILED = "failed";
    private static final String NOT_ADMIN12_MESSAGE = "location not an ADMIN1 or ADMIN2";
    private static final String NO_COUNTRIES_MESSAGE = "no country geometries associated with this location";

    private static final int STAGE_1_ID = 1;
    private static final int STAGE_2_ID = 2;
    private static final int STAGE_3_ID = 3;
    private static final int STAGE_2_MAXIMUM_DISTANCE = 5;
    private static final int STAGE_3_MAXIMUM_DISTANCE = 5;
    private static final String STAGE_2_GEOMETRY_DESCRIPTION = "land";
    private static final String STAGE_3_GEOMETRY_DESCRIPTION = "country";

    private QCLookupData qcLookupData;

    public QCManager(QCLookupData qcLookupData) {
        this.qcLookupData = qcLookupData;
    }

    /**
     * Performs quality control (QC) on a location. Each QC step is performed until a step fails.
     * @param location The location.
     * @return True if the location passed all QC checks, otherwise false.
     */
    public boolean performQC(Location location) {
        initializeLocationForQC(location);
        return performQCStage1(location) && performQCStage2(location) && performQCStage3(location);
    }

    private void initializeLocationForQC(Location location) {
        location.setAdminUnitQCGaulCode(null);
        location.setQcMessage(null);
    }

    private boolean performQCStage1(Location location) {
        boolean passed = true;
        String message = NOT_ADMIN12_MESSAGE;

        if (isAdmin1OrAdmin2(location)) {
            // Location is an admin1 or admin2, so find the closest admin unit to the location
            // (as long as it is close enough - see class AdminUnitFinder for details)
            AdminUnitFinder adminUnitFinder = new AdminUnitFinder();
            adminUnitFinder.findClosestAdminUnit(location, qcLookupData.getAdminUnits());

            AdminUnitQC closestAdminUnit = adminUnitFinder.getClosestAdminUnit();
            if (closestAdminUnit != null) {
                location.setAdminUnitQCGaulCode(closestAdminUnit.getGaulCode());
            }
            passed = adminUnitFinder.hasPassed();
            message = adminUnitFinder.getMessage();
        }

        appendQcMessage(location, STAGE_1_ID, passed, message);
        return passed;
    }

    private boolean performQCStage2(Location location) {
        boolean passed = true;
        String message;

        // Adjust the centroid of the country if necessary. For example, the centroid of the Philippines is not on
        // land, so replace it with a predefined point in the Philippines that is on land.
        CountryCentroidAdjuster adjuster = new CountryCentroidAdjuster();
        boolean hasBeenAdjusted = adjuster.adjustCountryCentroid(location, qcLookupData.getHealthMapCountryMap());

        if (hasBeenAdjusted) {
            message = adjuster.getMessage();
        } else {
            // Ensure that the location is on land. If not, snap to land if within the maximum distance away.
            Snapper snapper = new Snapper(STAGE_2_GEOMETRY_DESCRIPTION, STAGE_2_MAXIMUM_DISTANCE);
            applySnapperToLocation(snapper, location, qcLookupData.getLandSeaBorders());
            message = snapper.getMessage();
            passed = snapper.hasPassed();
        }

        appendQcMessage(location, STAGE_2_ID, passed, message);
        return passed;
    }

    private boolean performQCStage3(Location location) {
        // Ensure that the location is within the geometries associated with the location's HealthMap country. If not,
        // snap to the geometries if within the maximum distance away.
        boolean passed = true;
        String message = NO_COUNTRIES_MESSAGE;

        MultiPolygon countryGeometry = getCountryGeometryForLocation(location);
        if (countryGeometry != null) {
            Snapper snapper = new Snapper(STAGE_3_GEOMETRY_DESCRIPTION, STAGE_3_MAXIMUM_DISTANCE);
            applySnapperToLocation(snapper, location, countryGeometry);
            passed = snapper.hasPassed();
            message = snapper.getMessage();
        }

        appendQcMessage(location, STAGE_3_ID, passed, message);
        return passed;
    }

    private boolean isAdmin1OrAdmin2(Location location) {
        LocationPrecision precision = location.getPrecision();
        return (precision == LocationPrecision.ADMIN1) || (precision == LocationPrecision.ADMIN2);
    }

    private void applySnapperToLocation(Snapper snapper, Location location, MultiPolygon geometry) {
        if (geometry != null) {
            snapper.ensureWithinGeometry(location, geometry);
            Point closestPoint = snapper.getClosestPoint();
            if (closestPoint != null) {
                // Set the location to the returned closest point, which has been snapped if necessary
                location.setGeom(closestPoint);
            }
        }
    }

    private MultiPolygon getCountryGeometryForLocation(Location location) {
        MultiPolygon countryGeometry = null;

        if (location.getHealthMapCountryId() != null) {
            countryGeometry = qcLookupData.getHealthMapCountryGeometryMap().get(location.getHealthMapCountryId());
        } else if (location.getCountryGaulCode() != null) {
            countryGeometry = qcLookupData.getCountryGeometryMap().get(location.getCountryGaulCode());
        }

        return countryGeometry;
    }

    private void appendQcMessage(Location location, int qcStage, boolean hasPassed, String qcMessage) {
        if (StringUtils.hasText(qcMessage)) {
            qcMessage = String.format(MESSAGE_FORMAT, qcStage, hasPassed ? PASSED : FAILED, qcMessage);

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
