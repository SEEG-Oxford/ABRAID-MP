package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.DiseaseSynchronisationJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.ModellingJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

/**
 * Represents the ModelWrapper's web service interface.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebService {
    private WebServiceClient webServiceClient;
    private final AbraidJsonObjectMapper objectMapper;

    // The ModelWrapper's URL path for the model run (this is hardcoded because it is hardcoded in ModelWrapper).
    private static final String MODEL_RUN_URL_PATH = "/model/run";
    private static final String MODEL_DISEASE_UPDATE_URL_PATH = "/diseases";

    public ModelWrapperWebService(WebServiceClient webServiceClient, AbraidJsonObjectMapper objectMapper) {
        this.webServiceClient = webServiceClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Starts a model run.
     * @param modelWrapperUrl The base url path for the model wrapper instance on which to start a run.
     * @param diseaseGroup The disease group for this model run.
     * @param occurrences The disease occurrences for this model run.
     * @param diseaseExtent Ths disease extent for this model run, expressed as a mapping between GAUL codes
     *                      and extent class weightings.
     * @return The model run name, or null if the run did not start successfully.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public JsonModelRunResponse startRun(URI modelWrapperUrl, DiseaseGroup diseaseGroup,
                                         List<DiseaseOccurrence> occurrences, Map<Integer, Integer> diseaseExtent)
            throws WebServiceClientException, JsonParserException {
        String url = buildStartRunUrl(modelWrapperUrl);
        JsonModelRun body = createJsonModelRun(diseaseGroup, occurrences, diseaseExtent);
        // only serialize properties that are annotated with ModellingJsonView or are not annotated at all
        String bodyAsJson = createRequestBodyAsJson(body, ModellingJsonView.class);
        String response = webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
        return parseResponseJson(response);
    }

    private String buildStartRunUrl(URI rootUrl) {
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

    private JsonModelRunResponse parseResponseJson(String json) throws JsonParserException {
        return new JsonParser().parse(json, JsonModelRunResponse.class);
    }

    /**
     * Publish a given disease group to a given model wrapper instance.
     * @param modelWrapperUrl The base url path for the model wrapper instance to which the disease should be published.
     * @param diseaseGroup The disease group to publish.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public void publishSingleDisease(URI modelWrapperUrl, DiseaseGroup diseaseGroup)
            throws WebServiceClientException, JsonParserException {
        String url = buildPublishSingleDiseaseUrl(modelWrapperUrl, diseaseGroup.getId());
        JsonModelDisease body = createJsonDisease(diseaseGroup);
        // Don't serialize properties that are annotated with ModellingJsonView
        String bodyAsJson = createRequestBodyAsJson(body, DiseaseSynchronisationJsonView.class);
        webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
    }

    private String buildPublishSingleDiseaseUrl(URI rootUrl, int diseaseGroupId) {
        return UriBuilder.fromUri(rootUrl)
                .path(MODEL_DISEASE_UPDATE_URL_PATH)
                .path(Integer.toString(diseaseGroupId))
                .build().toString();
    }

    private JsonModelDisease createJsonDisease(DiseaseGroup diseaseGroup) {
        return new JsonModelDisease(diseaseGroup);
    }

    /**
     * Publish a given collection of disease groups to a given model wrapper instance.
     * @param modelWrapperUrl The base url path for the model wrapper instance to which the diseases should be published
     * @param diseaseGroups The disease groups to publish.
     * @throws WebServiceClientException If the web service call fails.
     * @throws JsonParserException If the web service's JSON response cannot be parsed.
     */
    public void publishAllDiseases(URI modelWrapperUrl, Collection<DiseaseGroup> diseaseGroups)
            throws WebServiceClientException, JsonParserException {
        String url = buildPublishAllDiseasesUrl(modelWrapperUrl);
        WrappedList<JsonModelDisease> body = createJsonDiseases(diseaseGroups);
        // Don't serialize properties that are annotated with ModellingJsonView
        String bodyAsJson = createRequestBodyAsJson(body, DiseaseSynchronisationJsonView.class);
        webServiceClient.makePostRequestWithJSON(url, bodyAsJson);
    }

    private WrappedList<JsonModelDisease> createJsonDiseases(final Collection<DiseaseGroup> diseaseGroups) {
        return new WrappedList<>(
            convert(diseaseGroups, new Converter<DiseaseGroup, JsonModelDisease>() {
                @Override
                public JsonModelDisease convert(DiseaseGroup diseaseGroup) {
                    return createJsonDisease(diseaseGroup);
                }
            })
        );
    }

    private String buildPublishAllDiseasesUrl(URI rootUrl) {
        return UriBuilder.fromUri(rootUrl)
                .path(MODEL_DISEASE_UPDATE_URL_PATH)
                .build().toString();
    }

    private String createRequestBodyAsJson(Object body, Class<?> view) {
        ObjectWriter writer = objectMapper.writerWithView(view);
        try {
            return writer.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ProcessingException(e);
        }
    }
}
