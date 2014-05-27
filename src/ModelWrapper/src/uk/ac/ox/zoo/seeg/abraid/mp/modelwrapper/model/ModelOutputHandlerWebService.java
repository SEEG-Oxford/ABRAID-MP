package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.io.FileUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;

/**
 * Represents the ModelOutputHandler's web service interface.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelOutputHandlerWebService {
    private WebServiceClient webServiceClient;
    private ConfigurationService configurationService;

    // The ModelWrapper's URL path for the model run (this is hardcoded because it is hardcoded in ModelOutputHandler).
    private static final String MODEL_RUN_URL_PATH = "/modeloutputhandler/handleoutputs";

    public ModelOutputHandlerWebService(WebServiceClient webServiceClient, ConfigurationService configurationService) {
        this.webServiceClient = webServiceClient;
        this.configurationService = configurationService;
    }

    /**
     * Sends the model outputs to the model output handler.
     * @param outputZipFile The output zip file.
     * @throws IOException if there is a problem with reading the output zip file into memory.
     * @throws uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException if the web service call fails.
     * @return Any error message returned by the model output handler. An empty string indicates success.
     */
    public String handleOutputs(File outputZipFile) throws IOException, WebServiceClientException {
        String url = buildUrl();
        byte[] body = FileUtils.readFileToByteArray(outputZipFile);
        return webServiceClient.makePostRequest(url, body);
    }

    private String buildUrl() {
        return UriBuilder.fromUri(configurationService.getModelOutputHandlerRootUrl())
                .path(MODEL_RUN_URL_PATH)
                .build().toString();
    }
}
