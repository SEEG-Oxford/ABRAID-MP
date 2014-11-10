package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.net.URI;
import java.util.*;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the ModelWrapperWebService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebServiceTest {
    private static final URI ROOT_URL = URI.create("http://localhost:8080/ModelWrapper");

    @Test
    public void startRunWithTypicalParameters() {
        // Arrange
        String expectedUrl = "http://localhost:8080/ModelWrapper/model/run";
        String modelRunName = "foo_2014-04-24-10-50-27_cd0efc75-42d3-4d96-94b4-287e28fbcdac";
        String requestJson = getStartRunRequestJson();
        String responseJson = String.format("{\"modelRunName\":\"%s\"}", modelRunName);
        ModelWrapperWebService webService = getModelWrapperWebService(expectedUrl, requestJson, responseJson);
        DiseaseGroup diseaseGroup = getDiseaseGroup();
        List<DiseaseOccurrence> diseaseOccurrences = getDiseaseOccurrences(diseaseGroup);
        Map<Integer, Integer> extentWeightings = getExtentWeightings();
        JsonModelRunResponse expectedResponse = new JsonModelRunResponse(modelRunName, null);

        // Act
        JsonModelRunResponse actualResponse = webService.startRun(ROOT_URL, diseaseGroup, diseaseOccurrences, extentWeightings);

        // Assert
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void startRunPropagatesWebServiceClientException() {
        // Arrange
        WebServiceClient client = mock(WebServiceClient.class);
        when(client.makePostRequestWithJSON(anyString(), anyString())).thenThrow(new WebServiceClientException(""));
        ModelWrapperWebService webService = getModelWrapperWebService(client);
        DiseaseGroup diseaseGroup = getDiseaseGroup();
        List<DiseaseOccurrence> diseaseOccurrences = getDiseaseOccurrences(diseaseGroup);
        Map<Integer, Integer> extentWeightings = getExtentWeightings();

        // Act
        catchException(webService).startRun(ROOT_URL, diseaseGroup, diseaseOccurrences, extentWeightings);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void startRunWithInvalidResponseJSONThrowsException() {
        // Arrange
        String url = "http://localhost:8080/ModelWrapper/model/run";
        String requestJson = getStartRunRequestJson();
        String responseJson = "this is invalid";
        ModelWrapperWebService webService = getModelWrapperWebService(url, requestJson, responseJson);
        DiseaseGroup diseaseGroup = getDiseaseGroup();
        List<DiseaseOccurrence> diseaseOccurrences = getDiseaseOccurrences(diseaseGroup);
        Map<Integer, Integer> extentWeightings = getExtentWeightings();

        // Act
        catchException(webService).startRun(ROOT_URL, diseaseGroup, diseaseOccurrences, extentWeightings);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
    }

    @Test
    public void publishSingleDiseaseSendsCorrectHTTPRequest() {
        // Arrange
        WebServiceClient client = mock(WebServiceClient.class);
        ModelWrapperWebService target = getModelWrapperWebService(client);
        DiseaseGroup diseaseGroup = getDiseaseGroup();

        // Act
        target.publishSingleDisease(ROOT_URL, diseaseGroup);

        // Assert
        verify(client).makePostRequestWithJSON(ROOT_URL.toString() + "/diseases/" + diseaseGroup.getId(), "{\"id\":188,\"name\":\"Leishmaniases\"}");
    }

    @Test
    public void publishSingleDiseasePropagatesWebServiceClientException() {
        // Arrange
        WebServiceClient client = mock(WebServiceClient.class);
        when(client.makePostRequestWithJSON(anyString(), anyString())).thenThrow(new WebServiceClientException(""));
        ModelWrapperWebService target = getModelWrapperWebService(client);
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);

        // Act
        catchException(target).publishSingleDisease(ROOT_URL, diseaseGroup);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void publishAllDiseasesSendsCorrectHTTPRequest() {
        // Arrange
        WebServiceClient client = mock(WebServiceClient.class);
        ModelWrapperWebService target = getModelWrapperWebService(client);
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(321);
        when(diseaseGroup.getName()).thenReturn("this is a test");
        Collection<DiseaseGroup> diseaseGroups = Arrays.asList(getDiseaseGroup(), diseaseGroup);

        // Act
        target.publishAllDiseases(ROOT_URL, diseaseGroups);

        // Assert
        verify(client).makePostRequestWithJSON(ROOT_URL.toString() + "/diseases", "{\"list\":[{\"id\":188,\"name\":\"Leishmaniases\"},{\"id\":321,\"name\":\"this is a test\"}]}");
    }

    @Test
    public void publishAllDiseasesPropagatesWebServiceClientException() {
        // Arrange
        WebServiceClient client = mock(WebServiceClient.class);
        when(client.makePostRequestWithJSON(anyString(), anyString())).thenThrow(new WebServiceClientException(""));
        ModelWrapperWebService target = getModelWrapperWebService(client);
        Collection<DiseaseGroup> diseaseGroups = Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class));

        // Act
        catchException(target).publishAllDiseases(ROOT_URL, diseaseGroups);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    private ModelWrapperWebService getModelWrapperWebService(String url, String requestJson, String responseJson) {
        WebServiceClient client = mock(WebServiceClient.class);
        when(client.makePostRequestWithJSON(url, requestJson)).thenReturn(responseJson);
        return getModelWrapperWebService(client);
    }

    private ModelWrapperWebService getModelWrapperWebService(WebServiceClient client) {
        return new ModelWrapperWebService(client, new AbraidJsonObjectMapper());
    }

    private DiseaseGroup getDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(188, null, "Leishmaniases", DiseaseGroupType.MICROCLUSTER);
        diseaseGroup.setAbbreviation("leish");
        diseaseGroup.setGlobal(true);
        return diseaseGroup;
    }

    private List<DiseaseOccurrence> getDiseaseOccurrences(DiseaseGroup diseaseGroup) {
        Location location1 = new Location("California, United States", -119.7503, 37.2502, LocationPrecision.ADMIN1);
        location1.setAdminUnitQCGaulCode(100);

        Location location2 = new Location("Bauru, São Paulo, Brazil", -49.06055, -22.31472, LocationPrecision.PRECISE);

        DiseaseOccurrence occurrence1 = new DiseaseOccurrence(1, diseaseGroup, location1,
                new Alert("occurrence1 title", "feed1"), DiseaseOccurrenceStatus.READY, 0.2, new DateTime("2014-03-01"));
        DiseaseOccurrence occurrence2 = new DiseaseOccurrence(2, diseaseGroup, location1,
                new Alert("occurrence2 title", "feed2"), DiseaseOccurrenceStatus.READY, 0.5, new DateTime("2014-03-02"));
        DiseaseOccurrence occurrence3 = new DiseaseOccurrence(3, diseaseGroup, location2,
                new Alert("occurrence3 title", "feed3"), DiseaseOccurrenceStatus.READY, 0.8, new DateTime("2014-03-03"));

        return Arrays.asList(occurrence1, occurrence2, occurrence3);
    }

    private Map<Integer, Integer> getExtentWeightings() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(2, 0);
        map.put(20, 100);
        map.put(50, -100);
        return map;
    }

    private String getStartRunRequestJson() {
        String disease = "\"id\":188,\"name\":\"Leishmaniases\",\"abbreviation\":\"leish\",\"global\":true";
        String occurrencesType = "\"type\":\"FeatureCollection\"";
        String occurrencesCrs = "\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:EPSG::4326\"}}";
        String occurrence1 = "\"type\":\"Feature\",\"id\":1,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-119.7503,37.2502]},\"properties\":{\"locationPrecision\":\"ADMIN1\",\"weighting\":0.2,\"gaulCode\":100}";
        String occurrence2 = "\"type\":\"Feature\",\"id\":2,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-119.7503,37.2502]},\"properties\":{\"locationPrecision\":\"ADMIN1\",\"weighting\":0.5,\"gaulCode\":100}";
        String occurrence3 = "\"type\":\"Feature\",\"id\":3,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-49.06055,-22.31472]},\"properties\":{\"locationPrecision\":\"PRECISE\",\"weighting\":0.8}";
        String extentWeightings = "\"50\":-100,\"2\":0,\"20\":100";

        String jsonFormat = "{\"disease\":{%s},\"occurrences\":{%s,%s,\"features\":[{%s},{%s},{%s}]},\"extentWeightings\":{%s}}";
        return String.format(jsonFormat, disease, occurrencesType, occurrencesCrs, occurrence1, occurrence2,
                occurrence3, extentWeightings);
    }
}
