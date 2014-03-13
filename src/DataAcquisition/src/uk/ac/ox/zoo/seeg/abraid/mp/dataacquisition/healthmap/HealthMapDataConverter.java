package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

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

    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquisition.class);
    private static final String CONVERSION_MESSAGE = "Converting %d HealthMap location(s) with %d alert(s)";
    private static final String COUNT_MESSAGE = "Saved %d HealthMap disease occurrence(s) in %d location(s)";

    public HealthMapDataConverter(HealthMapLocationConverter locationConverter,
                                  HealthMapAlertConverter alertConverter,
                                  AlertService alertService, DiseaseService diseaseService,
                                  HealthMapLookupData lookupData) {
        this.locationConverter = locationConverter;
        this.alertConverter = alertConverter;
        this.alertService = alertService;
        this.diseaseService = diseaseService;
        this.lookupData = lookupData;
    }

    /**
     * Converts a list of HealthMap locations into ABRAID objects, and saves them to the database.
     * @param healthMapLocations A list of HealthMap locations.
     * @param endDate The end date for this HealthMap retrieval.
     */
    public void convert(List<HealthMapLocation> healthMapLocations, Date endDate) {
        LOGGER.info(String.format(CONVERSION_MESSAGE, healthMapLocations.size(),
                countHealthMapAlerts(healthMapLocations)));

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
                    if (continueLocationConversion(healthMapLocation, location)) {
                        // Location was converted successfully, so save it all. Note that the location is saved with the
                        // disease occurrence.
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

    private boolean continueLocationConversion(HealthMapLocation healthMapLocation, Location location) {
        if (location.getId() == null) {
            // Location is new, so add precision (using GeoNames). Do it at this point so that we only call out to
            // GeoNames if we know that we have at least one disease occurrence that was converted successfully.
            locationConverter.addPrecision(healthMapLocation, location);

            // NB: Call to data QC will go here (if this conditional is true)
            return (location.getPrecision() != null);
        } else {
            // Location already exists, so conversion was successful
            return true;
        }
    }

    private void writeLastRetrievalEndDate(Date retrievalDate) {
        Provenance provenance = lookupData.getHealthMapProvenance();
        provenance.setLastRetrievalEndDate(retrievalDate);
        alertService.saveProvenance(provenance);
    }
}
