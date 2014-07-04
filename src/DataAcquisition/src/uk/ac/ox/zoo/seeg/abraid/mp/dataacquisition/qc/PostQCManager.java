package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

/**
 * Manages processes that run after QC but before the location is written to the database.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class PostQCManager {
    private static final char ADMIN_LEVEL_ZERO = '0';

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
        assignCountry(location);
        setResolutionWeighting(location);
    }

    /**
     * Finds and assigns the disease extent admin units that contain the location's point.
     * @param location The location.
     */
    private void assignDiseaseExtentAdminUnits(Location location) {
        validateLocation(location);

        // If the location is a country, ensure that only admin 0's are searched. This is so that we do not
        // erroneously associate a country with an admin 1 if the location happens to fall within it (e.g. if the
        // location is "United States" and the location's point lies within Kansas, we should not assign the Kansas
        // GAUL code to the location).
        Character adminLevel = (location.getPrecision() == LocationPrecision.COUNTRY) ? ADMIN_LEVEL_ZERO : null;

        // Find and assign the disease extent admin units that contain the location's point
        Integer adminUnitGlobalGaulCode =
                locationService.findAdminUnitGlobalThatContainsPoint(location.getGeom(), adminLevel);
        location.setAdminUnitGlobalGaulCode(adminUnitGlobalGaulCode);

        Integer adminUnitTropicalGaulCode =
                locationService.findAdminUnitTropicalThatContainsPoint(location.getGeom(), adminLevel);
        location.setAdminUnitTropicalGaulCode(adminUnitTropicalGaulCode);
    }

    /**
     * Find and assign the country that contains the location's point.
     * @param location The location.
     */
    private void assignCountry(Location location) {
        Integer countryGaulCode = locationService.findCountryThatContainsPoint(location.getGeom());
        location.setCountryGaulCode(countryGaulCode);
    }

    private void setResolutionWeighting(Location location) {
        double weighting = location.getPrecision().getWeighting();
        location.setResolutionWeighting(weighting);
    }

    private void validateLocation(Location location) {
        if (location.getGeom() == null || location.getPrecision() == null) {
            throw new IllegalArgumentException("Location must have a point and a precision");
        }
    }
}
