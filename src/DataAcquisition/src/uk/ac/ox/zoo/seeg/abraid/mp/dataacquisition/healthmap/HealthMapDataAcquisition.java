package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
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
    private HealthMapLookupData healthMapLookupData;
    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquisition.class);

    private static final String WEB_SERVICE_ERROR_MESSAGE = "Could not read HealthMap web service response: %s";

    public HealthMapDataAcquisition(HealthMapWebService healthMapWebService,
                                    HealthMapDataConverter healthMapDataConverter,
                                    HealthMapLookupData healthMapLookupData) {
        this.healthMapWebService = healthMapWebService;
        this.healthMapDataConverter = healthMapDataConverter;
        this.healthMapLookupData = healthMapLookupData;
    }

    /**
     * Acquires HealthMap data.
     */
    @Transactional
    public void acquireData() {
        Date startDate = getStartDate();
        Date endDate = getEndDate(startDate);

        List<HealthMapLocation> healthMapLocations = retrieveData(startDate, endDate);
        if (healthMapLocations != null) {
            healthMapDataConverter.convert(healthMapLocations, endDate);
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
     * 4. 7 days before now
     * @return The start date for the HealthMap alerts retrieval.
     */
    private Date getStartDate() {
        Provenance provenance = healthMapLookupData.getHealthMapProvenance();
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

    private Date getEndDate(Date startDate) {
        Calendar endCalendar = Calendar.getInstance();

        Integer endDateDaysAfterStartDate = healthMapWebService.getEndDateDaysAfterStartDate();
        if (endDateDaysAfterStartDate != null) {
            // Set the end date to the specified number of days after the start date, as long as this does not push
            // the date into the future
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_MONTH, endDateDaysAfterStartDate);
            if (calendar.compareTo(endCalendar) < 0) {
                endCalendar = calendar;
            }
        }

        return endCalendar.getTime();
    }
}
