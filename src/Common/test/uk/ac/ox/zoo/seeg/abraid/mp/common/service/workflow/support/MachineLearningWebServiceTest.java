package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the Machine Learning Web Service.
 * Copyright (c) 2014 University of Oxford
 */
public class MachineLearningWebServiceTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private AbraidJsonObjectMapper objectMapper;

    @Test(expected = WebServiceClientException.class)
    public void sendTrainingDataThrowsWebServiceClient() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenThrow(new WebServiceClientException(""));
        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, objectMapper);

        // Act
        webService.sendTrainingData(diseaseGroupId, occurrences);
    }

    @Test(expected = MachineWeightingPredictorException.class)
    public void getPredictionThrowsMachineWeightingPredictorExceptionForOccurrenceWithoutDiseaseGroup() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getDiseaseGroup()).thenReturn(null);

        MachineLearningWebService webService = new MachineLearningWebService(mock(WebServiceClient.class), objectMapper);

        // Act
        webService.getPrediction(occurrence);
    }

    @Test(expected = WebServiceClientException.class)
    public void getPredictionThrowsWebServiceClientException() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenThrow(new WebServiceClientException(""));

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, objectMapper);
        DiseaseOccurrence occurrence = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(87).get(0);

        // Act
        webService.getPrediction(occurrence);
    }

    @Test
    public void getPredictionReturnsExpectedDouble() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn("0.6");

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, objectMapper);
        DiseaseOccurrence occurrence = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(87).get(0);

        // Act
        Double prediction = webService.getPrediction(occurrence);

        // Assert
        assertThat(prediction).isEqualTo(0.6);
    }

    @Test(expected = NumberFormatException.class)
    public void getPredictionThrowsNumberFormatExceptionIfResponseCannotBeParsed() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn("fdsa");

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, objectMapper);
        DiseaseOccurrence occurrence = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(87).get(0);

        // Act
        webService.getPrediction(occurrence);
    }

    @Test
    public void getPredictionReturnsNullIfNoPredictionIsReturnedFromWebService() throws Exception {
        // Arrange
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        when(webServiceClient.makePostRequestWithJSON(anyString(), anyString())).thenReturn("No prediction");

        MachineLearningWebService webService = new MachineLearningWebService(webServiceClient, objectMapper);
        DiseaseOccurrence occurrence = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(87).get(0);

        // Act
        Double prediction = webService.getPrediction(occurrence);

        // Assert
        assertThat(prediction).isNull();
    }
}
