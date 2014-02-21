package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.*;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import javax.ws.rs.core.UriBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    // The default start date. This is used if HealthMap has not previously been queried, according to the
    // Provenance.LastRetrievedDate field.
    private Date defaultStartDate;

    // The date/time format used in both the request URL and the response JSON.
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    public HealthMapWebService(WebServiceClient webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public void setDefaultStartDate(String defaultStartDateString) {
        if (StringUtils.hasText(defaultStartDateString)) {
            try {
                defaultStartDate = dateFormat.parse(defaultStartDateString);
            }
            catch(ParseException e) {
                throw new RuntimeException("Cannot parse HealthMap default start date: " + defaultStartDateString);
            }
        }
    }

    public Date getDefaultStartDate() {
        return defaultStartDate;
    }

    /**
     * Sends a request to the HealthMap web service.
     * @param startDate The start of the date range for HealthMap alert retrieval.
     * @param endDate The end of the date range for HealthMap alert retrieval.
     * @return A list of HealthMap locations, containing alerts.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public List<HealthMapLocation> sendRequest(Date startDate, Date endDate)
            throws WebServiceClientException, JsonParserException {
        String url = buildUrl(startDate, endDate);
        String json = webServiceClient.request(url);
        return parseJson(json);
    }

    private String buildUrl(Date startDate, Date endDate) {
        // The root URL and authorization code have already been set by the global configuration
        UriBuilder builder = UriBuilder.fromUri(rootUrl).queryParam("auth", authorizationCode);
        // Add the start date and end date if they are non-null
        addDateIfNotNull(builder, "sdate", startDate);
        addDateIfNotNull(builder, "edate", endDate);
        return builder.build().toString();
    }

    private List<HealthMapLocation> parseJson(String json) {
        // Sets the date format that we expect to receive in the JSON
        JsonParser parser = new JsonParser(new ObjectMapperConfigurer() {
            @Override
            public void configure(ObjectMapper mapper) {
                mapper.setDateFormat(dateFormat);
            }
        });

        // Because our desired type uses generics, we need to wrap it in a TypeReference
        // (we cannot use List<HealthMapLocation>.class)
        return parser.parse(json, new TypeReference<List<HealthMapLocation>>() { });
    }

    private void addDateIfNotNull(UriBuilder builder, String parameterName, Date date) {
        if (date != null) {
            builder.queryParam(parameterName, dateFormat.format(date));
        }
    }
}
