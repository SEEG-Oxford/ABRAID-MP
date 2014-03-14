package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoName;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the GeoNamesWebService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNamesWebServiceTest {
    @Test
    public void successfulGeoNamesCall() {
        // Arrange
        int geoNameId = 898335;
        String json = "{\"alternateNames\":[],\"countryName\":\"Zambia\",\"adminCode1\":\"07\",\"lng\":\"26.76667\"," +
                "\"adminName2\":\"\",\"fcodeName\":\"populated place\",\"adminName3\":\"\",\"timezone\":" +
                "{\"dstOffset\":2,\"gmtOffset\":2,\"timeZoneId\":\"Africa/Lusaka\"},\"adminName4\":\"\"," +
                "\"adminName5\":\"\",\"bbox\":{\"south\":-17.108993648751756,\"east\":26.776076165520003," +
                "\"north\":-17.091006351248247,\"west\":26.75725783448},\"name\":\"Siambara\",\"fcode\":\"PPL\"," +
                "\"geonameId\":898335,\"lat\":\"-17.1\",\"population\":0,\"adminName1\":\"Southern\",\"countryId\":" +
                "\"895949\",\"adminId1\":\"896972\",\"fclName\":\"city, village,...\",\"countryCode\":\"ZM\"," +
                "\"srtm3\":1315,\"toponymName\":\"Siambara\",\"fcl\":\"P\",\"continentCode\":\"AF\"}";

        String url = getGeoNamesUrl(geoNameId);
        WebServiceClient client = getMockWebServiceClient(url, json);
        GeoNamesWebService webService = getGeoNamesWebService(client);

        // Act
        GeoName geoName = webService.getById(geoNameId);

        // Assert
        assertThat(geoName.getFeatureCode()).isEqualTo("PPL");
        assertThat(geoName.getGeoNameId()).isEqualTo(geoNameId);
    }

    @Test
    public void nonExistentGeoName() {
        // Arrange
        int geoNameId = 898337123;
        String json = "{\"status\":{\"message\":\"this geonameid does not exist\",\"value\":15}}";

        String url = getGeoNamesUrl(geoNameId);
        WebServiceClient client = getMockWebServiceClient(url, json);
        GeoNamesWebService webService = getGeoNamesWebService(client);

        // Act
        GeoName geoName = webService.getById(geoNameId);

        // Assert
        assertThat(geoName).isNull();
    }

    @Test
    public void webServiceClientThrewException() {
        // Arrange
        int geoNameId = 12345;

        String url = getGeoNamesUrl(geoNameId);
        WebServiceClient client = mock(WebServiceClient.class);
        //noinspection unchecked
        when(client.request(url)).thenThrow(WebServiceClientException.class);
        GeoNamesWebService webService = getGeoNamesWebService(client);

        // Act
        catchException(webService).getById(geoNameId);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void invalidJsonReturnedByGeoNames() {
        // Arrange
        int geoNameId = 67890;
        String json = "this is invalid JSON";

        String url = getGeoNamesUrl(geoNameId);
        WebServiceClient client = getMockWebServiceClient(url, json);
        GeoNamesWebService webService = getGeoNamesWebService(client);

        // Act
        catchException(webService).getById(geoNameId);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
    }

    private String getGeoNamesUrl(int geoNameId) {
        return "http://api.geonames.org/getJSON?username=testuser&geonameId=" + geoNameId;
    }

    private GeoNamesWebService getGeoNamesWebService(WebServiceClient client) {
        GeoNamesWebService webService = new GeoNamesWebService(client);
        webService.setRootUrlGetJSON("http://api.geonames.org/getJSON");
        webService.setGeoNameIdParameterName("geonameId");
        webService.setUsername("testuser");
        webService.setUsernameParameterName("username");
        return webService;
    }

    private WebServiceClient getMockWebServiceClient(String url, String json) {
        WebServiceClient client = mock(WebServiceClient.class);
        when(client.request(url)).thenReturn(json);
        return client;
    }
}
