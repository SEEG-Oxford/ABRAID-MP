package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Acquires data from HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataAcquisition {
    private HealthMapWebService healthMapWebService;
    private HealthMapDataConverter healthMapDataConverter;
    private AlertService alertService;
    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquisition.class);

    private static final String WEB_SERVICE_ERROR_MESSAGE = "Could not read HealthMap web service response: %s";

    public HealthMapDataAcquisition(HealthMapWebService healthMapWebService,
                                    HealthMapDataConverter healthMapDataConverter, AlertService alertService) {
        this.healthMapWebService = healthMapWebService;
        this.healthMapDataConverter = healthMapDataConverter;
        this.alertService = alertService;
    }

    /**
     * Acquires HealthMap data.
     */
    @Transactional
    public void acquireData() {
        Date startDate = getStartDate();
        Date endDate = Calendar.getInstance().getTime();

        List<HealthMapLocation> healthMapLocations = retrieveData(startDate, endDate);
        if (healthMapLocations != null) {
            healthMapDataConverter.convert(healthMapLocations, endDate);
            // TODO: Data QC, model input, data to geo-wiki
        }
    }

    private List<HealthMapLocation> retrieveData(Date startDate, Date endDate) {
        try {
            return healthMapWebService.sendRequest(startDate, endDate);
        } catch (WebServiceClientException|JsonParserException e) {
            LOGGER.error(String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage()), e);
            return null;
        }
    }

    /**
     * Gets the start date for the HealthMap alerts retrieval. This is the first of these that is non-null:
     * 1. The last retrieval date as stored in database field Provenance.LastRetrievalDate
     * 2. The default start date
     * 3. n days before now, where n is specified as the parameter "default start date days before now"
     * 4. 7 days
     * @return The start date for the HealthMap alerts retrieval.
     */
    private Date getStartDate() {
        Provenance provenance = alertService.getProvenanceByName(ProvenanceNames.HEALTHMAP);
        if (provenance != null && provenance.getLastRetrievedDate() != null) {
            return provenance.getLastRetrievedDate();
        } else if (healthMapWebService.getDefaultStartDate() != null) {
            return healthMapWebService.getDefaultStartDate();
        } else {
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.DAY_OF_MONTH, -healthMapWebService.getDefaultStartDateDaysBeforeNow());
            return startDate.getTime();
        }
    }
}
