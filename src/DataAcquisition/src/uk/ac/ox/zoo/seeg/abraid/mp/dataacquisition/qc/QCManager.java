package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

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
            return 0;
        }

        if (!performQCStage2(location)) {
            return 1;
        }

        return 2;
    }

    private void initializeLocationForQC(Location location) {
        location.setAdminUnit(null);
        location.setQcMessage(null);
    }

    private boolean performQCStage1(Location location) {
        boolean passed = true;

        if (isLocationAdmin1OrAdmin2(location)) {
            // Location is an admin1 or admin2, so find the closest admin unit to the location
            // (as long as it is close enough - see class AdminUnitFinder for details)
            AdminUnitFinder adminUnitFinder = new AdminUnitFinder();
            adminUnitFinder.findClosestAdminUnit(location, qcLookupData.getAdminUnits());
            location.setAdminUnit(adminUnitFinder.getClosestAdminUnit());
            appendQcMessage(location, adminUnitFinder.getMessage());
            passed = (adminUnitFinder.getClosestAdminUnit() != null);
        } else {
            appendQcMessage(location, QC_STAGE_1_PASSED_MESSAGE);
        }

        return passed;
    }

    private boolean performQCStage2(Location location) {
        // Ensure that the location is on land; if not, snap to land if it is within the maximum distance away
        LandSnapper landSnapper = new LandSnapper();
        landSnapper.ensureOnLand(location, qcLookupData.getLandSeaBorders());
        Point closestPoint = landSnapper.getClosestPoint();
        if (closestPoint != null) {
            location.setGeom(closestPoint);
        }
        appendQcMessage(location, landSnapper.getMessage());

        // Passes if a closest point was returned (if null, the closest point is too far away from land)
        return (closestPoint != null);
    }

    private boolean isLocationAdmin1OrAdmin2(Location location) {
        return (location.getPrecision() == LocationPrecision.ADMIN1) ||
                (location.getPrecision() == LocationPrecision.ADMIN2);
    }

    private void appendQcMessage(Location location, String qcMessage) {
        if (StringUtils.hasText(qcMessage)) {
            String locationQcMessage = location.getQcMessage();
            if (locationQcMessage == null) {
                locationQcMessage = "";
            }
            if (StringUtils.hasText(locationQcMessage)) {
                // QC stage messages are separated by a single space
                locationQcMessage += " ";
            }
            locationQcMessage += qcMessage;
            location.setQcMessage(locationQcMessage);
        }
    }
}
