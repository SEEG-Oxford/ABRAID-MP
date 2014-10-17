package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests the Machine Learning Web Service.
 * Copyright (c) 2014 University of Oxford
 */
public class MachineLearningWebServiceTest {

    private String rootUrl = "rootUrl/";

    @Test
    public void sendTrainingDataMakesPOSTRequestWithCorrectArguments() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        String expectedUrl = rootUrl + diseaseGroupId + "/train";

        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, 0.2, 500, 2);
        occurrence.setExpertWeighting(0.8);
        List<DiseaseOccurrence> occurrences = new ArrayList<>(Arrays.asList(occurrence));

        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, new AbraidJsonObjectMapper(), rootUrl);

        String expectedBodyAsJson = "{\"points\":[{\"distanceFromExtent\":500.0,\"environmentalSuitability\":0.2,\"feedId\":2,\"expertWeighting\":0.8}]}";

        // Act
        webService.sendTrainingData(diseaseGroupId, occurrences);

        // Assert
        verify(webServiceClient).makePostRequestWithJSON(expectedUrl, expectedBodyAsJson);
    }


    @Test
    public void getPredictionThrowsMachineWeightingPredictorExceptionForOccurrenceWithoutDiseaseGroup() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getDiseaseGroup()).thenReturn(null);

        MachineLearningWebService webService = new MachineLearningWebService(mock(WebServiceClient.class), mock(AbraidJsonObjectMapper.class), "");

        // Act
        catchException(webService).getPrediction(occurrence);

        // Assert
        assertThat(caughtException()).isInstanceOf(MachineWeightingPredictorException.class);
        assertThat(caughtException()).hasMessage("No disease group");
    }

    @Test
    public void getPredictionCallsWebServiceClientWithCorrectArguments() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        String expectedUrl = rootUrl + diseaseGroupId + "/predict";

        double envSuitability = 0.4;
        double distanceFromExtent = 1000;
        int feedId = 2;

        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn("0.0");
        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, new AbraidJsonObjectMapper(), rootUrl);
        DiseaseOccurrence occurrence = createDiseaseOccurrence(diseaseGroupId, envSuitability, distanceFromExtent, feedId);

        String expectedBodyAsJson =
                "{\"distanceFromExtent\":" + distanceFromExtent + "," +
                 "\"environmentalSuitability\":" + envSuitability + "," +
                 "\"feedId\":" + feedId + "}";

        // Act
        webService.getPrediction(occurrence);

        // Assert
        verify(webServiceClient).makePostRequestWithJSON(expectedUrl, expectedBodyAsJson);
    }

    @Test
    public void getPredictionReturnsPredictionFromWebService() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        double expectedPrediction = 0.6;
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn(expectedPrediction + "");

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, new AbraidJsonObjectMapper(), "");
        DiseaseOccurrence occurrence = createDefaultOccurrence();

        // Act
        Double prediction = webService.getPrediction(occurrence);

        // Assert
        assertThat(prediction).isEqualTo(expectedPrediction);
    }

    @Test
    public void getPredictionThrowsNumberFormatExceptionIfResponseCannotBeParsed() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn("fdsa");

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, new AbraidJsonObjectMapper(), "");
        DiseaseOccurrence occurrence = createDefaultOccurrence();

        // Act
        catchException(webService).getPrediction(occurrence);

        // Assert
        assertThat(caughtException()).isInstanceOf(NumberFormatException.class);
    }

    @Test
    public void getPredictionReturnsNullIfNoPredictionIsReturnedFromWebService() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn("No prediction");

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, new AbraidJsonObjectMapper(), "");
        DiseaseOccurrence occurrence = createDefaultOccurrence();

        // Act
        Double prediction = webService.getPrediction(occurrence);

        // Assert
        assertThat(prediction).isNull();
    }

    private DiseaseOccurrence createDefaultOccurrence() {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setAlert(new Alert("Title", "Feed name"));
        return occurrence;
    }

    private DiseaseOccurrence createDiseaseOccurrence(int diseaseGroupId, double envSuitability, double distanceFromExtent, int feedId) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setDiseaseGroup(new DiseaseGroup(diseaseGroupId));
        occurrence.setEnvironmentalSuitability(envSuitability);
        occurrence.setDistanceFromDiseaseExtent(distanceFromExtent);

        Alert alert = new Alert();
        alert.setFeed(new Feed(feedId));
        occurrence.setAlert(alert);
        return occurrence;
    }
}
