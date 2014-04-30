package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;

/**
 * Manages processes that run after QC but before the location is written to the database.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class PostQCManager {
    private LocationService locationService;

    public PostQCManager(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Runs all post-QC processes on the specified location.
     * @param location The location
     */
    public void runPostQCProcesses(Location location) {
        assignDiseaseExtentAdminUnits(location);
    }

    /**
     * Finds and assigns the disease extent admin units that contain the location's point.
     * @param location The location.
     */
    private void assignDiseaseExtentAdminUnits(Location location) {
        if (location.getGeom() != null) {
            Integer adminUnitGlobal = locationService.findAdminUnitGlobalThatContainsPoint(location.getGeom());
            location.setAdminUnitGlobalGaulCode(adminUnitGlobal);

            Integer adminUnitTropical = locationService.findAdminUnitTropicalThatContainsPoint(location.getGeom());
            location.setAdminUnitTropicalGaulCode(adminUnitTropical);
        }
    }
}
