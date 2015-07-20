package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Acquires data from HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataAcquirer {
    private static final Logger LOGGER = Logger.getLogger(HealthMapDataAcquirer.class);

    private static final String WEB_SERVICE_ERROR_MESSAGE = "Could not read HealthMap web service response: %s";
    private static final String FILE_ERROR_MESSAGE = "Could not read file \"%s\"";
    private static final String JSON_ERROR_MESSAGE = "Could not read JSON from file \"%s\"";
    private static final String RETRIEVING_FROM_FILE_MESSAGE = "Retrieving HealthMap data from file \"%s\"";

    private final HealthMapWebService healthMapWebService;
    private final HealthMapDataConverter healthMapDataConverter;
    private final HealthMapLookupData healthMapLookupData;

    public HealthMapDataAcquirer(HealthMapWebService healthMapWebService,
                                 HealthMapDataConverter healthMapDataConverter,
                                 HealthMapLookupData healthMapLookupData) {
        this.healthMapWebService = healthMapWebService;
        this.healthMapDataConverter = healthMapDataConverter;
        this.healthMapLookupData = healthMapLookupData;
    }

    /**
     * Acquires HealthMap data from the HealthMap web service.
     */
    public void acquireDataFromWebService() {
        DateTime startDate = getStartDate();
        DateTime endDate = getEndDate(startDate);
        List<HealthMapLocation> healthMapLocations = retrieveDataFromWebService(startDate, endDate);
        convert(healthMapLocations, endDate);
    }

    /**
     * Acquires HealthMap data from a file.
     * @param jsonFileName The name of a file that contains HealthMap JSON.
     */
    public void acquireDataFromFile(String jsonFileName) {
        LOGGER.info(String.format(RETRIEVING_FROM_FILE_MESSAGE, jsonFileName));
        List<HealthMapLocation> healthMapLocations = retrieveDataFromFile(jsonFileName);
        convert(healthMapLocations, null);
    }

    private List<HealthMapLocation> retrieveDataFromWebService(DateTime startDate, DateTime endDate) {
        try {
            return healthMapWebService.sendRequest(startDate, endDate);
        } catch (WebServiceClientException|JsonParserException e) {
            LOGGER.fatal(String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage()), e);
            throw new DataAcquisitionException(e.getMessage(), e);
        }
    }

    private List<HealthMapLocation> retrieveDataFromFile(String jsonFileName) {
        String json;
        try {
            json = FileUtils.readFileToString(new File(jsonFileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.fatal(String.format(FILE_ERROR_MESSAGE, e.getMessage()), e);
            throw new DataAcquisitionException(e.getMessage(), e);
        }

        try {
            return healthMapWebService.parseJson(json);
        } catch (JsonParserException e) {
            LOGGER.fatal(String.format(JSON_ERROR_MESSAGE, e.getMessage()), e);
            throw new DataAcquisitionException(e.getMessage(), e);
        }
    }

    private Set<DiseaseOccurrence> convert(List<HealthMapLocation> healthMapLocations, DateTime endDate) {
        if (healthMapLocations != null) {
            Set<DiseaseOccurrence> occurrences = healthMapDataConverter.convert(healthMapLocations, endDate);
            healthMapLookupData.clearLookups();
            return occurrences;
        }
        return null;
    }

    /**
     * Gets the start date for the HealthMap alerts retrieval. This is the first of these that is non-null:
     * 1. The end date of the last retrieval, as stored in database field provenance.last_retrieval_end_date
     * 2. The default start date
     * 3. n days before now, where n is specified as the parameter "default start date days before now"
     * 4. 7 days before now
     * @return The start date for the HealthMap alerts retrieval.
     */
    private DateTime getStartDate() {
        Provenance provenance = healthMapLookupData.getHealthMapProvenance();
        if (provenance != null && provenance.getLastRetrievalEndDate() != null) {
            return provenance.getLastRetrievalEndDate();
        } else if (healthMapWebService.getDefaultStartDate() != null) {
            return healthMapWebService.getDefaultStartDate();
        } else {
            return DateTime.now().minusDays(healthMapWebService.getDefaultStartDateDaysBeforeNow());
        }
    }

    private DateTime getEndDate(DateTime startDate) {
        DateTime endDate = DateTime.now();

        Integer endDateDaysAfterStartDate = healthMapWebService.getEndDateDaysAfterStartDate();
        if (endDateDaysAfterStartDate != null) {
            // Set the end date to the specified number of days after the start date, as long as this does not push
            // the date into the future
            DateTime endDateAfterStartDate = startDate.plusDays(endDateDaysAfterStartDate);
            if (endDateAfterStartDate.isBefore(endDate)) {
                endDate = endDateAfterStartDate;
            }
        }

        return endDate;
    }
}
