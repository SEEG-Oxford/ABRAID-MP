package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.ModellingJsonView;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.util.Collection;
import java.util.List;

/**
 * Represents the ModelWrapper's web service interface.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebService {
    private WebServiceClient webServiceClient;

    // The root URL for the ModelWrapper web service (i.e. the non-parameterised part).
    private String rootUrl;

    // The ModelWrapper's URL path for the model run (this is hardcoded because it is hardcoded in ModelWrapper).
    private static final String MODEL_RUN_URL_PATH = "/model/run";

    public ModelWrapperWebService(WebServiceClient webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    /**
     * Starts a model run.
     * @return The model run name, or null if the run did not start successfully.
     */
    public JsonModelRunResponse startRun(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> occurrences,
                                         Collection<Integer> diseaseExtent)
            throws WebServiceClientException, JsonParserException {
        String url = buildUrl();
        JsonModelRun body = createJsonModelRun(diseaseGroup, occurrences, diseaseExtent);
        String bodyAsJson = createBodyAsJson(body);
        String response = webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
        return parseJson(response);
    }

    private String buildUrl() {
        return UriBuilder.fromUri(rootUrl)
                .path(MODEL_RUN_URL_PATH)
                .build().toString();
    }

    private JsonModelRun createJsonModelRun(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> occurrences,
                                            Collection<Integer> diseaseExtent) {
        JsonModelDisease jsonModelDisease = new JsonModelDisease(diseaseGroup);
        GeoJsonDiseaseOccurrenceFeatureCollection jsonOccurrences =
                new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences);
        return new JsonModelRun(jsonModelDisease, jsonOccurrences, diseaseExtent);
    }

    private String createBodyAsJson(Object body) {
        // To create the JSON body for the POST request:
        // - use the GeoJsonObjectMapper (because the request contains GeoJson)
        // - only serialize properties that are annotated with ModellingJsonView or are not annotated at all
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();
        ObjectWriter writer = objectMapper.writerWithView(ModellingJsonView.class);
        try {
            return writer.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ProcessingException(e);
        }
    }

    private JsonModelRunResponse parseJson(String json) throws JsonParserException {
        return new JsonParser().parse(json, JsonModelRunResponse.class);
    }
}
