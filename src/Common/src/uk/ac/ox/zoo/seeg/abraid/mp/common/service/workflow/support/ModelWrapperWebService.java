package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.ModellingJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;

/**
 * Represents the ModelWrapper's web service interface.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebService {
    private WebServiceClient webServiceClient;
    private final AbraidJsonObjectMapper objectMapper;

    // The root URL for the ModelWrapper web service (i.e. the non-parameterised part).
    private String rootUrl;

    // The ModelWrapper's URL path for the model run (this is hardcoded because it is hardcoded in ModelWrapper).
    private static final String MODEL_RUN_URL_PATH = "/model/run";

    public ModelWrapperWebService(WebServiceClient webServiceClient, AbraidJsonObjectMapper objectMapper) {
        this.webServiceClient = webServiceClient;
        this.objectMapper = objectMapper;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    /**
     * Starts a model run.
     * @param diseaseGroup The disease group for this model run.
     * @param occurrences The disease occurrences for this model run.
     * @param diseaseExtent Ths disease extent for this model run, expressed as a mapping between GAUL codes
     *                      and extent class weightings.
     * @return The model run name, or null if the run did not start successfully.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public JsonModelRunResponse startRun(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> occurrences,
                                         Map<Integer, Integer> diseaseExtent)
            throws WebServiceClientException, JsonParserException {
        String url = buildUrl();
        JsonModelRun body = createJsonModelRun(diseaseGroup, occurrences, diseaseExtent);
        String bodyAsJson = createRequestBodyAsJson(body);
        String response = webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
        return parseResponseJson(response);
    }

    private String buildUrl() {
        return UriBuilder.fromUri(rootUrl)
                .path(MODEL_RUN_URL_PATH)
                .build().toString();
    }

    private JsonModelRun createJsonModelRun(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> occurrences,
                                            Map<Integer, Integer> diseaseExtent) {
        JsonModelDisease jsonModelDisease = new JsonModelDisease(diseaseGroup);
        GeoJsonDiseaseOccurrenceFeatureCollection jsonOccurrences =
                new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences);
        return new JsonModelRun(jsonModelDisease, jsonOccurrences, diseaseExtent);
    }

    private String createRequestBodyAsJson(Object body) {
        // To create the JSON body for the POST request:
        // - use the AbraidJsonObjectMapper (because the request contains GeoJson)
        // - only serialize properties that are annotated with ModellingJsonView or are not annotated at all
        //   (this selects the properties that are relevant to the model)
        ObjectWriter writer = objectMapper.writerWithView(ModellingJsonView.class);
        try {
            return writer.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ProcessingException(e);
        }
    }

    private JsonModelRunResponse parseResponseJson(String json) throws JsonParserException {
        return new JsonParser().parse(json, JsonModelRunResponse.class);
    }
}
