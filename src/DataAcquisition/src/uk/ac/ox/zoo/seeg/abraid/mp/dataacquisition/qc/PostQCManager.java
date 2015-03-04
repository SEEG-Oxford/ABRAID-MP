package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Manages processes that run after QC but before the location is written to the database.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class PostQCManager {
    private static final double MAX_AREA_FOR_MODEL_ELIGIBLE_COUNTRY = 115000.0;
    private static final double PIXEL_EQUIVALENT_AREA = 25.0;
    // ALPHA & BETA chosen such that f(25)=1 & f(115000)=0
    private static final double ALPHA = 1.0 / log(PIXEL_EQUIVALENT_AREA / MAX_AREA_FOR_MODEL_ELIGIBLE_COUNTRY);
    private static final double BETA = 1.0 / MAX_AREA_FOR_MODEL_ELIGIBLE_COUNTRY;


    private final LocationService locationService;
    private final QCLookupData qcLookupData;

    public PostQCManager(LocationService locationService, QCLookupData qcLookupData) {
        this.locationService = locationService;
        this.qcLookupData = qcLookupData;
    }

    /**
     * Runs all post-QC processes on the specified location.
     * @param location The location
     */
    public void runPostQCProcesses(Location location) {
        assignDiseaseExtentAdminUnits(location);
        assignCountry(location);
        failQCIfNotOnLand(location);
        failQCIfAnyGaulCodesAreMissing(location);
        setResolutionWeighting(location);
        setModelEligibility(location);
    }

    /**
     * Finds and assigns the disease extent admin units that contain the location's point.
     * @param location The location.
     */
    private void assignDiseaseExtentAdminUnits(Location location) {
        validateLocation(location);

        // Find and assign the disease extent admin units that contain the location's point
        Integer adminUnitGlobalGaulCode =
                locationService.findAdminUnitGlobalThatContainsPoint(location.getGeom());
        location.setAdminUnitGlobalGaulCode(adminUnitGlobalGaulCode);

        Integer adminUnitTropicalGaulCode =
                locationService.findAdminUnitTropicalThatContainsPoint(location.getGeom());
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

    private void failQCIfNotOnLand(Location location) {
        // A sanity check - this should only happen if, after QC stage 2, the point is adjusted to be off land
        if (!locationService.doesLandSeaBorderContainPoint(location.getGeom())) {
            location.setHasPassedQc(false);
        }
    }

    private void failQCIfAnyGaulCodesAreMissing(Location location) {
        // A sanity check - this should only happen if the shapefiles are inconsistent or the snapping routines fail
        if (location.getAdminUnitGlobalGaulCode() == null || location.getAdminUnitTropicalGaulCode() == null ||
                location.getCountryGaulCode() == null) {
            location.setHasPassedQc(false);
        }
    }

    private void setResolutionWeighting(Location location) {
        if (location.hasPassedQc()) {
            location.setResolutionWeighting(calculateResolutionWeighting(location));
        } else {
            location.setResolutionWeighting(null);
        }
    }

    private Double calculateResolutionWeighting(Location location) {
        LocationPrecision precision = location.getPrecision();
        if (precision.equals(LocationPrecision.PRECISE)) {
            return 1.0;
        } else {
            double area = extractArea(location, precision);
            area = min(max(area, PIXEL_EQUIVALENT_AREA), MAX_AREA_FOR_MODEL_ELIGIBLE_COUNTRY);

            // ALPHA & BETA chosen such that f(25)=1 & f(115000)=0
            return ALPHA * log(BETA * area);
        }
    }

    private double extractArea(Location location, LocationPrecision precision) {
        if (precision.equals(LocationPrecision.COUNTRY)) {
            return qcLookupData.getCountryMap().get(location.getCountryGaulCode()).getArea();
        } else {
            return qcLookupData.getAdminUnitsMap().get(location.getAdminUnitQCGaulCode()).getArea();
        }
    }

    private void setModelEligibility(Location location) {
        if (location.hasPassedQc()) {
            if (location.getPrecision().equals(LocationPrecision.COUNTRY)) {
                Double area = qcLookupData.getCountryGeometryMap().get(location.getCountryGaulCode()).getArea();
                location.setIsModelEligible(area <= MAX_AREA_FOR_MODEL_ELIGIBLE_COUNTRY);
            } else {
                location.setIsModelEligible(true);
            }
        } else {
            location.setIsModelEligible(false);
        }

    }

    private void validateLocation(Location location) {
        if (location.getGeom() == null || location.getPrecision() == null) {
            throw new IllegalArgumentException("Location must have a point and a precision");
        }
    }
}
