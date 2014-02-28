package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoName;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoNameStatus;

import javax.ws.rs.core.UriBuilder;

/**
 * A wrapper to call the GeoNames web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNamesWebService {
    private WebServiceClient webServiceClient;

    // The root URL for the GeoNames getJSON web service (i.e. the non-parameterised part).
    private String rootUrlGetJSON;

    // The username for the GeoNames web service
    private String username;

    // Web service parameter names
    private String usernameParameterName;
    private String geoNameIdParameterName;

    private static final Logger LOGGER = Logger.getLogger(GeoNamesWebService.class);
    private static final String GEONAMES_RETURNED_STATUS_MESSAGE = "GeoNames returned status code %d (\"%s\"). URL: %s";

    public GeoNamesWebService(WebServiceClient webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public void setRootUrlGetJSON(String rootUrlGetJSON) {
        this.rootUrlGetJSON = rootUrlGetJSON;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsernameParameterName(String usernameParameterName) {
        this.usernameParameterName = usernameParameterName;
    }

    public void setGeoNameIdParameterName(String geoNameIdParameterName) {
        this.geoNameIdParameterName = geoNameIdParameterName;
    }

    /**
     * Gets a GeoName by ID.
     * @param geoNameId The GeoName ID.
     * @return The GeoName with the requested ID, or null if it does not exist.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public GeoName getById(int geoNameId) throws WebServiceClientException, JsonParserException {
        String url = buildUrl(geoNameId);
        String json = webServiceClient.request(url);
        GeoName geoName = parseJson(json);

        GeoNameStatus status = geoName.getStatus();
        if (status != null) {
            // If the GeoName status exists, something went wrong (most probably the ID was not found). We have asked
            // GeoNames for a list of status codes and messages, as we may need to throw an exception for some of
            // these.
            LOGGER.warn(String.format(GEONAMES_RETURNED_STATUS_MESSAGE, status.getValue(), status.getMessage(), url));
            return null;
        } else {
            return geoName;
        }
    }

    private String buildUrl(int geoNameId) {
        // The root URL and username have already been set by the global configuration
        return UriBuilder.fromUri(rootUrlGetJSON)
                .queryParam(usernameParameterName, username)
                .queryParam(geoNameIdParameterName, geoNameId)
                .build()
                .toString();
    }

    private GeoName parseJson(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json, GeoName.class);
    }
}
