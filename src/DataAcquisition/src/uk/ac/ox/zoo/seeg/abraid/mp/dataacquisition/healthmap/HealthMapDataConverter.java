package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;
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

    public HealthMapDataConverter(HealthMapLocationConverter locationConverter,
                                  HealthMapAlertConverter diseaseOccurrenceConverter,
                                  AlertService alertService, DiseaseService diseaseService) {
        this.locationConverter = locationConverter;
        this.diseaseOccurrenceConverter = diseaseOccurrenceConverter;
        this.alertService = alertService;
        this.diseaseService = diseaseService;
    }

    /**
     * Converts a list of HealthMap locations into ABRAID objects, and saves them to the database.
     * @param healthMapLocations A list of HealthMap locations.
     * @param retrievalDate The date that the HealthMap locations were retrieved.
     */
    public void convert(List<HealthMapLocation> healthMapLocations, Date retrievalDate) {
        for(HealthMapLocation healthMapLocation : healthMapLocations) {
            Location location = locationConverter.convert(healthMapLocation);

            for(HealthMapAlert healthMapAlert : healthMapLocation.getAlerts()) {
                DiseaseOccurrence occurrence = diseaseOccurrenceConverter.convert(healthMapAlert, location);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }

        writeLastRetrievedDate(retrievalDate);

        // TODO: Ensure that the transaction commits at this point
    }

    private void writeLastRetrievedDate(Date retrievalDate) {
        Provenance provenance = alertService.getProvenanceByName(ProvenanceNames.HEALTHMAP);
        provenance.setLastRetrievedDate(retrievalDate);
        alertService.saveProvenance(provenance);
    }
}
