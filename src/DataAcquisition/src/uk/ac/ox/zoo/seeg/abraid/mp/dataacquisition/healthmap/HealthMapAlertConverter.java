package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;

/**
 * Converts a HealthMap alert into an ABRAID disease occurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertConverter {
    private static final Logger log = Logger.getLogger(HealthMapAlertConverter.class);
    private static final String ALERT_ID_NOT_FOUND = "Could not extract alert ID from link \"%s\"";

    private AlertService alertService;
    private HealthMapLookupData lookupData;

    public HealthMapAlertConverter(AlertService alertService, HealthMapLookupData lookupData) {
        this.alertService = alertService;
        this.lookupData = lookupData;
    }

    /**
     * Converts a HealthMap alert into an ABRAID disease occurrence.
     * @param healthMapAlert The HealthMap alert to convert.
     * @param location The ABRAID location (already converted from HealthMap).
     * @return A disease occurrence.
     */
    public DiseaseOccurrence convert(HealthMapAlert healthMapAlert, Location location) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        Alert alert = null;

        // Find alert ID
        Long alertId = healthMapAlert.getAlertId();
        if (alertId != null) {
            alert = alertService.getAlertByHealthMapAlertId(alertId);
        }
        else {
            log.warn(String.format(ALERT_ID_NOT_FOUND, healthMapAlert.getLink()));
        }

        if (alert == null) {
            // Alert doesn't exist, so create it
            alert = createAlert(healthMapAlert, alertId);
        }

        // TODO: If the disease ID (when we have it) does not exist, add a new HealthMap disease [marked "of interest"]
        // that is linked to a new top-level disease group (i.e. disease cluster). And if the disease does exist but the
        // name has changed, rename it. Alert this to the system administrator.
        HealthMapDisease healthMapDisease = lookupData.getDiseaseMap().get(healthMapAlert.getDisease());
        occurrence.setDiseaseGroup(healthMapDisease.getDiseaseGroup());
        occurrence.setOccurrenceStartDate(healthMapAlert.getDate());
        occurrence.setAlert(alert);
        occurrence.setLocation(location);

        // TODO: If a disease occurrence already exists with the same disease, alert, location, and occurrence date,
        // then don't write it again to the database

        return occurrence;
    }

    private Alert createAlert(HealthMapAlert healthMapAlert, Long alertId) {
        Alert alert = new Alert();
        // TODO: If the feed ID (when we have it) does not exist, add a new feed. And if the feed does exist but
        // the name has changed, rename it. Log this as an error so that an e-mail is sent to the system
        // administrator to review the feed weighting (TODO - eventually this should not actually be an error but
        // the e-mail should still be sent).
        alert.setFeed(lookupData.getFeedMap().get(healthMapAlert.getFeed()));
        alert.setTitle(healthMapAlert.getSummary());
        alert.setPublicationDate(healthMapAlert.getDate());
        alert.setUrl(healthMapAlert.getOriginal_url());
        alert.setSummary(healthMapAlert.getDescr());
        alert.setHealthMapAlertId(alertId);
        return alert;
    }
}
