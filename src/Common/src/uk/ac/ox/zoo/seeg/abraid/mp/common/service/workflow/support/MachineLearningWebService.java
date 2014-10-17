package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseOccurrenceDataPoint;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseOccurrenceDataSet;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Web Service for calling out to Machine Learning predictor.
 * Copyright (c) 2014 University of Oxford
 */
public class MachineLearningWebService {
    /** Server response indicating that a trusted prediction was not returned and point should be validated manually. */
    public static final String EXPECTED_PREDICTION_FAILURE_RESPONSE = "No prediction";

    private WebServiceClient webServiceClient;
    private AbraidJsonObjectMapper objectMapper;
    private String rootUrl;

    public MachineLearningWebService(WebServiceClient webServiceClient, AbraidJsonObjectMapper objectMapper,
                                     String rootUrl) {
        this.webServiceClient = webServiceClient;
        this.objectMapper = objectMapper;
        this.rootUrl = rootUrl;
    }

    /**
     * Send the data points of one disease group, with which to train the predictor, as Json.
     * @param diseaseGroupId The ID of the disease group the occurrences belong to.
     * @param occurrences The training data points.
     * @throws JsonProcessingException if the JSON is invalid
     * @throws WebServiceClientException if the web service client cannot execute the request
     */
    public void sendTrainingData(int diseaseGroupId, List<DiseaseOccurrence> occurrences)
            throws JsonProcessingException, WebServiceClientException {
        String url = rootUrl + diseaseGroupId + "/train";
        JsonDiseaseOccurrenceDataSet data = convertToDTO(occurrences);
        String bodyAsJson = writeRequestBodyAsJson(data);
        webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
    }

    /**
     * Find the predicted weighting of the given disease occurrence.
     * @param occurrence The disease occurrence.
     * @return The predicted weighting.
     * @throws JsonProcessingException if the JSON is invalid
     * @throws uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException if the web service client fails to execute request
     * @throws NumberFormatException if the response string cannot be parsed as double
     */
    public Double getPrediction(DiseaseOccurrence occurrence)
            throws JsonProcessingException, WebServiceClientException, NumberFormatException {

        Integer diseaseGroupId = getDiseaseGroupId(occurrence);
        if (diseaseGroupId != null) {
            String url = rootUrl + diseaseGroupId + "/predict";
            JsonDiseaseOccurrenceDataPoint data = new JsonDiseaseOccurrenceDataPoint(occurrence);
            String bodyAsJson = writeRequestBodyAsJson(data);
            String response = webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
            if (response.equals(EXPECTED_PREDICTION_FAILURE_RESPONSE)) {
                return null;
            } else {
                return parseDouble(response);
            }
        } else {
            throw new MachineWeightingPredictorException("No disease group");
        }
    }

    private JsonDiseaseOccurrenceDataSet convertToDTO(List<DiseaseOccurrence> occurrences) {
        List<JsonDiseaseOccurrenceDataPoint> data = new ArrayList<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            data.add(new JsonDiseaseOccurrenceDataPoint(occurrence));
        }
        return new JsonDiseaseOccurrenceDataSet(data);
    }

    private Integer getDiseaseGroupId(DiseaseOccurrence occurrence) {
        return (occurrence.getDiseaseGroup() != null) ? occurrence.getDiseaseGroup().getId() : null;
    }

    private String writeRequestBodyAsJson(Object data) throws JsonProcessingException {
        ObjectWriter writer = objectMapper.writer();
        return writer.writeValueAsString(data);
    }
}
