package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * A wrapper to call the HealthMap web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapWebService {
    private WebServiceClient webServiceClient;

    // The root URL for the HealthMap web service (i.e. the non-parameterised part).
    private String rootUrl;

    // The authorization code obtained from HealthMap in order to use their web service.
    private String authorizationCode;

    // Whether or not to strip HTML from the description field.
    private boolean stripHtml;

    // The default start date. This is used if HealthMap has not previously been queried, according to the
    // Provenance.LastRetrievedDate field.
    private DateTime defaultStartDate;

    // If defaultStartDate is not specified, the start date is this number of days before now (i.e. the current date).
    private Integer defaultStartDateDaysBeforeNow = null;

    // If specified, this sets the end date to the number of days after the start date
    private Integer endDateDaysAfterStartDate = null;

    // The date/time format used in both the request URL and the response JSON.
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ");

    // Web service parameter names
    private String authorizationParameterName;
    private String startDateParameterName;
    private String endDateParameterName;
    private String stripHtmlParameterName;

    private static final Logger LOGGER = Logger.getLogger(HealthMapWebService.class);
    private static final String CALLING_WEB_SERVICE_MESSAGE = "Calling HealthMap web service between dates %s and %s";

    public HealthMapWebService(WebServiceClient webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public void setStripHtml(boolean stripHtml) {
        this.stripHtml = stripHtml;
    }

    public DateTime getDefaultStartDate() {
        return defaultStartDate;
    }

    /**
     * Sets the default start date.
     * @param defaultStartDateString The default start date.
     */
    public void setDefaultStartDate(String defaultStartDateString) {
        if (StringUtils.hasText(defaultStartDateString)) {
            try {
                defaultStartDate = dateTimeFormatter.parseDateTime(defaultStartDateString);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Cannot parse HealthMap default start date: " + defaultStartDateString);
            }
        }
    }

    public Integer getDefaultStartDateDaysBeforeNow() {
        return defaultStartDateDaysBeforeNow;
    }

    public void setDefaultStartDateDaysBeforeNow(Integer defaultStartDateDaysBeforeNow) {
        this.defaultStartDateDaysBeforeNow = defaultStartDateDaysBeforeNow;
    }

    public Integer getEndDateDaysAfterStartDate() {
        return endDateDaysAfterStartDate;
    }

    public void setEndDateDaysAfterStartDate(Integer endDateDaysAfterStartDate) {
        this.endDateDaysAfterStartDate = endDateDaysAfterStartDate;
    }

    public void setAuthorizationParameterName(String authorizationParameterName) {
        this.authorizationParameterName = authorizationParameterName;
    }

    public void setStartDateParameterName(String startDateParameterName) {
        this.startDateParameterName = startDateParameterName;
    }

    public void setEndDateParameterName(String endDateParameterName) {
        this.endDateParameterName = endDateParameterName;
    }

    public void setStripHtmlParameterName(String stripHtmlParameterName) {
        this.stripHtmlParameterName = stripHtmlParameterName;
    }

    /**
     * Sends a request to the HealthMap web service.
     * @param startDate The start of the date range for HealthMap alert retrieval.
     * @param endDate The end of the date range for HealthMap alert retrieval.
     * @return A list of HealthMap locations, containing alerts.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public List<HealthMapLocation> sendRequest(DateTime startDate, DateTime endDate)
            throws WebServiceClientException, JsonParserException {
        String formattedStartDate = formatDateWithNullProtection(startDate);
        String formattedEndDate = formatDateWithNullProtection(endDate);

        String url = buildUrl(formattedStartDate, formattedEndDate);

        LOGGER.info(String.format(CALLING_WEB_SERVICE_MESSAGE, formattedStartDate, formattedEndDate));
        String json = webServiceClient.makeGetRequest(url);

        return parseJson(json);
    }

    private String buildUrl(String startDate, String endDate) {
        // The root URL and authorization code have already been set by the global configuration
        UriBuilder builder = UriBuilder.fromUri(rootUrl)
                .queryParam(authorizationParameterName, authorizationCode)
                .queryParam(stripHtmlParameterName, stripHtml);
        // Add the start date and end date if they are non-null
        addParameterIfNotNull(builder, startDateParameterName, startDate);
        addParameterIfNotNull(builder, endDateParameterName, endDate);
        return builder.build().toString();
    }

    /**
     * Parses the supplied HealthMap JSON into objects.
     * @param json The HealthMap JSON.
     * @return A list of HealthMapLocation objects.
     * @throws JsonParserException If the JSON could not be parsed.
     */
    public List<HealthMapLocation> parseJson(String json) throws JsonParserException {
        // Sets the date format that we expect to receive in the JSON
        JsonParser parser = new JsonParser(dateTimeFormatter);

        // Because our desired type uses generics, we need to wrap it in a TypeReference
        // (we cannot use List<HealthMapLocation>.class)
        return parser.parse(json, new TypeReference<List<HealthMapLocation>>() { });
    }

    private String formatDateWithNullProtection(DateTime date) {
        return (date != null) ? dateTimeFormatter.print(date) : null;
    }

    private void addParameterIfNotNull(UriBuilder builder, String parameterName, String parameterValue) {
        if (parameterValue != null) {
            builder.queryParam(parameterName, parameterValue);
        }
    }
}
