package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

/**
 * The Quality Control (QC) manager. Performs quality control on a location.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCManager {
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
        if (!performQCStage1(location)) {
            return 0;
        }

        return 1;
    }

    private boolean performQCStage1(Location location) {
        boolean passed = true;

        if (isLocationAdmin1OrAdmin2(location)) {
            // Location is an admin1 or admin2, so find the closest admin unit to the location
            // (as long as it is close enough - see class AdminUnitFinder for details)
            AdminUnitFinder adminUnitFinder = new AdminUnitFinder();
            adminUnitFinder.findClosestAdminUnit(location, qcLookupData.getAdminUnits());
            location.setAdminUnit(adminUnitFinder.getClosestAdminUnit());
            location.setQcMessage(adminUnitFinder.getMessage());
            passed = (adminUnitFinder.getClosestAdminUnit() != null);
        } else {
            location.setAdminUnit(null);
            location.setQcMessage(null);
        }

        return passed;
    }

    private boolean isLocationAdmin1OrAdmin2(Location location) {
        return (location.getPrecision() == LocationPrecision.ADMIN1) ||
                (location.getPrecision() == LocationPrecision.ADMIN2);
    }
}
