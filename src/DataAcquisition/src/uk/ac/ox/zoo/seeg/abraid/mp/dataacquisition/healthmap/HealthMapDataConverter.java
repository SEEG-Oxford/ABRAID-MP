package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.PostQCManager;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.QCManager;

import java.util.*;

/**
 * Converts the retrieved HealthMap data into the ABRAID data structure.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataConverter {
    private AlertService alertService;
    private DiseaseService diseaseService;
    private HealthMapLocationConverter locationConverter;
    private HealthMapAlertConverter alertConverter;
    private HealthMapLookupData lookupData;
    private QCManager qcManager;
    private PostQCManager postQcManager;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquisition.class);
    private static final String CONVERSION_MESSAGE =
            "Converting %d HealthMap location(s), with %d alert(s) and %d GeoNames ID(s)";
    private static final String COUNT_MESSAGE = "Saved %d HealthMap disease occurrence(s) in %d location(s)";

    public HealthMapDataConverter(HealthMapLocationConverter locationConverter,
                                  HealthMapAlertConverter alertConverter,
                                  AlertService alertService, DiseaseService diseaseService,
                                  HealthMapLookupData lookupData, QCManager qcManager,
                                  PostQCManager postQcManager,
                                  DiseaseOccurrenceValidationService diseaseOccurrenceValidationService) {
        this.locationConverter = locationConverter;
        this.alertConverter = alertConverter;
        this.alertService = alertService;
        this.diseaseService = diseaseService;
        this.lookupData = lookupData;
        this.qcManager = qcManager;
        this.postQcManager = postQcManager;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
    }

    /**
     * Converts a list of HealthMap locations into ABRAID objects, and saves them to the database.
     * @param healthMapLocations A list of HealthMap locations.
     * @param endDate The end date for this HealthMap retrieval.
     */
    public void convert(List<HealthMapLocation> healthMapLocations, DateTime endDate) {
        LOGGER.info(String.format(CONVERSION_MESSAGE, healthMapLocations.size(),
                countHealthMapAlerts(healthMapLocations), countGeoNamesIdOccurrences(healthMapLocations)));

        Set<Location> convertedLocations = new HashSet<>();
        Set<DiseaseOccurrence> convertedOccurrences = new HashSet<>();

        convertLocations(healthMapLocations, convertedLocations, convertedOccurrences);
        writeLastRetrievalEndDate(endDate);

        LOGGER.info(String.format(COUNT_MESSAGE, convertedOccurrences.size(), convertedLocations.size()));
    }

    private int countHealthMapAlerts(List<HealthMapLocation> healthMapLocations) {
        int count = 0;
        for (HealthMapLocation healthMapLocation : healthMapLocations) {
            if (healthMapLocation.getAlerts() != null) {
                count += healthMapLocation.getAlerts().size();
            }
        }
        return count;
    }

    private int countGeoNamesIdOccurrences(List<HealthMapLocation> healthMapLocations) {
        int count = 0;
        for (HealthMapLocation healthMapLocation : healthMapLocations) {
            if (healthMapLocation.getGeoNameId() != null) {
                count++;
            }
        }
        return count;
    }

    private void convertLocations(List<HealthMapLocation> healthMapLocations, Set<Location> convertedLocations,
                                  Set<DiseaseOccurrence> convertedOccurrences) {
        for (HealthMapLocation healthMapLocation : healthMapLocations) {
            // This partially converts the HealthMap location. The rest of the conversion is done by
            // continueLocationConversion() when we know that there is at least one successfully-converted alert.
            Location location = locationConverter.convert(healthMapLocation);
            if (location != null) {
                convertAlert(healthMapLocation, location, convertedLocations, convertedOccurrences);
            }
        }
    }

    private void convertAlert(HealthMapLocation healthMapLocation, Location location,
                              Set<Location> convertedLocations, Set<DiseaseOccurrence> convertedOccurrences) {
        if (healthMapLocation.getAlerts() != null) {
            for (HealthMapAlert healthMapAlert : healthMapLocation.getAlerts()) {
                DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);
                if (occurrence != null) {
                    // Now that we know that there is at least one disease occurrence to save, continue location
                    // conversion
                    location = continueLocationConversion(healthMapLocation, location);

                    if (location != null) {
                        // Location was converted successfully, so add validation parameters to the occurrence and
                        // save it all. Note that the location is saved with the disease occurrence.
                        occurrence.setLocation(location);
                        if (automaticModelRunsEnabled(occurrence)) {
                            diseaseOccurrenceValidationService.addValidationParameters(occurrence);
                        }
                        diseaseService.saveDiseaseOccurrence(occurrence);
                        convertedLocations.add(location);
                        convertedOccurrences.add(occurrence);
                    } else {
                        // Location conversion failed, so do not convert any more of this location's alerts
                        break;
                    }
                }
            }
        }
    }

    private boolean automaticModelRunsEnabled(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled();
    }

    // Returns the converted location, or null if the location could not be converted further
    private Location continueLocationConversion(HealthMapLocation healthMapLocation, Location location) {
        Location locationToReturn = location;

        if (locationToReturn.getId() == null) {
            // Location is new, so add precision (using GeoNames). Do it at this point so that we only call out to
            // GeoNames if we know that we have at least one disease occurrence that was converted successfully.
            locationConverter.addPrecision(healthMapLocation, locationToReturn);

            if (location.getPrecision() != null) {
                // Location could be converted, so perform QC and post-QC processes
                performQualityControl(locationToReturn);
                runPostQcProcesses(locationToReturn);

                // It is possible that these processes have changed the location point. So look again for an
                // existing location at this point and precision. If one is found, use it.
                Location foundLocation = locationConverter.findExistingLocation(locationToReturn.getGeom(),
                        locationToReturn.getPrecision());
                if (foundLocation != null) {
                    locationToReturn = foundLocation;
                }
            } else {
                // Location could not be converted, so return null
                locationToReturn = null;
            }
        }

        return locationToReturn;
    }

    private void performQualityControl(Location location) {
        boolean hasPassedQc = qcManager.performQC(location);
        location.setHasPassedQc(hasPassedQc);
    }

    private void runPostQcProcesses(Location location) {
        postQcManager.runPostQCProcesses(location);
    }

    private void writeLastRetrievalEndDate(DateTime retrievalDate) {
        Provenance provenance = lookupData.getHealthMapProvenance();
        provenance.setLastRetrievalEndDate(retrievalDate);
        alertService.saveProvenance(provenance);
    }
}
