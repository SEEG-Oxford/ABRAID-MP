package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DiseaseOccurrenceDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts the retrieved HealthMap data into the ABRAID data structure.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataConverter {
    private AlertService alertService;
    private HealthMapLocationConverter locationConverter;
    private HealthMapAlertConverter alertConverter;
    private HealthMapLookupData lookupData;
    private DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer;

    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquirer.class);
    private static final String CONVERSION_MESSAGE =
            "Converting %d HealthMap location(s), with %d alert(s) and %d GeoNames ID(s)";
    private static final String COUNT_MESSAGE = "Saved %d HealthMap disease occurrence(s) in %d location(s)";

    public HealthMapDataConverter(HealthMapLocationConverter locationConverter,
                                  HealthMapAlertConverter alertConverter,
                                  AlertService alertService,
                                  HealthMapLookupData lookupData,
                                  DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer) {
        this.locationConverter = locationConverter;
        this.alertConverter = alertConverter;
        this.alertService = alertService;
        this.lookupData = lookupData;
        this.diseaseOccurrenceDataAcquirer = diseaseOccurrenceDataAcquirer;
    }

    /**
     * Converts a list of HealthMap locations into ABRAID objects, and saves them to the database.
     * Returns the occurrences for test purposes.
     *
     * @param healthMapLocations A list of HealthMap locations.
     * @param endDate The end date for this HealthMap retrieval.
     * @return The saved occurrences.
     */
    public Set<DiseaseOccurrence> convert(List<HealthMapLocation> healthMapLocations, DateTime endDate) {
        LOGGER.info(String.format(CONVERSION_MESSAGE, healthMapLocations.size(),
                countHealthMapAlerts(healthMapLocations), countGeoNamesIdOccurrences(healthMapLocations)));

        Set<DiseaseOccurrence> occurrences = convertHealthMapLocations(healthMapLocations);
        writeLastRetrievalEndDate(endDate);

        LOGGER.info(String.format(COUNT_MESSAGE, occurrences.size(), countUniqueLocations(occurrences)));
        return occurrences;
    }

    private Set<DiseaseOccurrence> convertHealthMapLocations(List<HealthMapLocation> healthMapLocations) {
        Set<DiseaseOccurrence> convertedOccurrences = new HashSet<>();
        for (HealthMapLocation healthMapLocation : healthMapLocations) {
            Location location = locationConverter.convert(healthMapLocation);
            if (location != null) {
                convertHealthMapAlert(healthMapLocation, location, convertedOccurrences);
            }
        }
        return convertedOccurrences;
    }

    private void convertHealthMapAlert(HealthMapLocation healthMapLocation, Location location,
                                       Set<DiseaseOccurrence> convertedOccurrences) {
        if (healthMapLocation.getAlerts() != null) {
            for (HealthMapAlert healthMapAlert : healthMapLocation.getAlerts()) {
                DiseaseOccurrence occurrence = alertConverter.convert(healthMapAlert, location);
                if (diseaseOccurrenceDataAcquirer.acquire(occurrence)) {
                    convertedOccurrences.add(occurrence);
                }
            }
        }
    }

    private void writeLastRetrievalEndDate(DateTime retrievalDate) {
        Provenance provenance = lookupData.getHealthMapProvenance();
        provenance.setLastRetrievalEndDate(retrievalDate);
        alertService.saveProvenance(provenance);
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

    private int countUniqueLocations(Set<DiseaseOccurrence> occurrences) {
        Set<Integer> locationIDs = new HashSet<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            if (occurrence.getLocation() != null) {
                locationIDs.add(occurrence.getLocation().getId());
            }
        }
        return locationIDs.size();
    }
}
