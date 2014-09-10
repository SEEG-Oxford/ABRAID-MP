package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers;

import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.PostQCManager;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.QCManager;

import java.util.List;

/**
 * Acquires a disease occurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceDataAcquirer {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceDataAcquirer.class);
    private static final String MULTIPLE_LOCATIONS_MATCH_MESSAGE =
            "More than one location already exists at point (%f,%f) and with precision %s. Arbitrarily using " +
            "location ID %d.";

    private DiseaseService diseaseService;
    private LocationService locationService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;
    private QCManager qcManager;
    private PostQCManager postQcManager;

    public DiseaseOccurrenceDataAcquirer(DiseaseService diseaseService, LocationService locationService,
                                         DiseaseOccurrenceValidationService diseaseOccurrenceValidationService,
                                         QCManager qcManager, PostQCManager postQcManager) {
        this.diseaseService = diseaseService;
        this.locationService = locationService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
        this.qcManager = qcManager;
        this.postQcManager = postQcManager;
    }

    /**
     * Acquires a disease occurrence, saving it to the database if appropriate.
     * @param occurrence The occurrence to acquire.
     * @return True if the disease occurrence was saved, otherwise false.
     */
    public boolean acquire(DiseaseOccurrence occurrence) {
        if (occurrence != null) {
            Location location = continueLocationConversion(occurrence.getLocation());

            if (location != null) {
                occurrence.setLocation(location);

                if (!doesDiseaseOccurrenceAlreadyExist(occurrence)) {
                    // Add validation parameters to the occurrence and save it all. Note that the location is saved with
                    // the disease occurrence.
                    diseaseOccurrenceValidationService.addValidationParametersWithChecks(occurrence);
                    diseaseService.saveDiseaseOccurrence(occurrence);
                    return true;
                }
            }
        }

        return false;
    }

    // Returns the converted location, or null if the location could not be converted further
    private Location continueLocationConversion(Location location) {
        Location foundLocation = null;

        if (location.getId() == null) {
            // This is potentially a new location, but first check whether there is a matching location in the database
            foundLocation = findMatchingLocation(location);
            if (foundLocation == null) {
                // No matching location, so perform QC and post-QC processes
                performQualityControl(location);
                runPostQcProcesses(location);

                // It is possible that these processes have changed the location point. So look again for an
                // existing location at this point and precision. If one is found, use it.
                foundLocation = findMatchingLocation(location);
            }
        }

        return (foundLocation != null) ? foundLocation : location;
    }

    // Finds an existing location whose point and precision are the same as the specified location's.
    private Location findMatchingLocation(Location location) {
        Location foundLocation = null;
        Point point = location.getGeom();
        LocationPrecision precision = location.getPrecision();

        List<Location> locations = locationService.getLocationsByPointAndPrecision(point, precision);

        if (locations.size() > 0) {
            foundLocation = locations.get(0);
            if (locations.size() > 1) {
                // There may be multiple locations at the specified lat/long and location precision. For example:
                // - Location 1 is created at point (x,y) with no GeoNames ID and place_basic_type 'p' (precise)
                // - Location 2 is created at the same point (x,y) with a specified GeoNames ID, whose feature code
                //   indicates a precise location
                // It is valid for these to co-exist, but which one wins in this case is arbitrary. So we just pick
                // the first one and log that fact.
                LOGGER.warn(String.format(MULTIPLE_LOCATIONS_MATCH_MESSAGE, point.getX(), point.getY(), precision,
                        location.getId()));
            }
        }

        return foundLocation;
    }

    private boolean doesDiseaseOccurrenceAlreadyExist(DiseaseOccurrence occurrence) {
        return diseaseService.doesDiseaseOccurrenceExist(occurrence);
    }

    private void performQualityControl(Location location) {
        boolean hasPassedQc = qcManager.performQC(location);
        location.setHasPassedQc(hasPassedQc);
    }

    private void runPostQcProcesses(Location location) {
        postQcManager.runPostQCProcesses(location);
    }
}
