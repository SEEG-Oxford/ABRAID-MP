package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.Date;
import java.util.List;

/**
 * Converts the retrieved HealthMap data into the ABRAID data structure.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataConverter {
    private AlertService alertService;
    private DiseaseService diseaseService;
    private HealthMapLocationConverter locationConverter;
    private HealthMapAlertConverter diseaseOccurrenceConverter;
    private HealthMapLookupData healthMapLookupData;

    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquisition.class);
    private static final String CONVERSION_MESSAGE = "Converting %d HealthMap location(s) with corresponding alerts";
    private static final String COUNT_MESSAGE = "Saved %d HealthMap disease occurrence(s) in %d location(s)";

    public HealthMapDataConverter(HealthMapLocationConverter locationConverter,
                                  HealthMapAlertConverter diseaseOccurrenceConverter,
                                  AlertService alertService, DiseaseService diseaseService,
                                  HealthMapLookupData healthMapLookupData) {
        this.locationConverter = locationConverter;
        this.diseaseOccurrenceConverter = diseaseOccurrenceConverter;
        this.alertService = alertService;
        this.diseaseService = diseaseService;
        this.healthMapLookupData = healthMapLookupData;
    }

    /**
     * Converts a list of HealthMap locations into ABRAID objects, and saves them to the database.
     * @param healthMapLocations A list of HealthMap locations.
     * @param retrievalDate The date that the HealthMap locations were retrieved.
     */
    public void convert(List<HealthMapLocation> healthMapLocations, Date retrievalDate) {
        LOGGER.info(String.format(CONVERSION_MESSAGE, healthMapLocations.size()));

        int locationsCount = 0;
        int diseaseOccurrencesCount = 0;
        for (HealthMapLocation healthMapLocation : healthMapLocations) {
            // Convert the location
            Location location = locationConverter.convert(healthMapLocation);
            boolean isFirstOccurrenceInThisLocation = true;

            // Convert each alert
            for (HealthMapAlert healthMapAlert : healthMapLocation.getAlerts()) {
                DiseaseOccurrence occurrence = diseaseOccurrenceConverter.convert(healthMapAlert, location);
                if (occurrence != null) {
                    if (isFirstOccurrenceInThisLocation) {
                        // Add the location precision (using GeoNames) when we know that the location is definitely
                        // going to be saved
                        locationConverter.addPrecisionIfNewLocation(location, healthMapLocation);
                        locationsCount++;
                        isFirstOccurrenceInThisLocation = false;
                    }

                    diseaseService.saveDiseaseOccurrence(occurrence);
                    diseaseOccurrencesCount++;
                    locationsCount++;
                }
            }
        }

        writeLastRetrievedDate(retrievalDate);

        LOGGER.info(String.format(COUNT_MESSAGE, diseaseOccurrencesCount, locationsCount));
    }

    private void writeLastRetrievedDate(Date retrievalDate) {
        Provenance provenance = healthMapLookupData.getHealthMapProvenance();
        provenance.setLastRetrievedDate(retrievalDate);
        alertService.saveProvenance(provenance);
    }
}
