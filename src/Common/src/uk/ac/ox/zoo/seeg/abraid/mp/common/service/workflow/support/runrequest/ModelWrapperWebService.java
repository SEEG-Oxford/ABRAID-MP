package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
/**
 * Represents the ModelWrapper's web service interface.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelWrapperWebService {
    private WebServiceClient webServiceClient;

    // The ModelWrapper's URL path for the model run (this is hardcoded because it is hardcoded in ModelWrapper).
    private static final String MODEL_RUN_URL_PATH = "/model/run";

    public ModelWrapperWebService(WebServiceClient webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    /**
     * Starts a model run.
     * @param modelWrapperUrl The base url path for the model wrapper instance on which to start a run.
     * @param modelRunPackage The zip file to submit containing the model run workspace.
     * @return The response from the webservice.
     * @throws WebServiceClientException If the web service call fails.
     * @throws IOException If the model run package can not be read.
     */
    public JsonModelRunResponse startRun(URI modelWrapperUrl, File modelRunPackage)
            throws WebServiceClientException, IOException {
        String url = buildStartRunUrl(modelWrapperUrl);
        String response = webServiceClient.makePostRequestWithBinary(url, modelRunPackage);
        return parseResponseJson(response);
    }

    private String buildStartRunUrl(URI rootUrl) {
        return UriBuilder.fromUri(rootUrl)
                .path(MODEL_RUN_URL_PATH)
                .build().toString();
    }

    private JsonModelRunResponse parseResponseJson(String json) throws JsonParserException {
        return new JsonParser().parse(json, JsonModelRunResponse.class);
    }
}
