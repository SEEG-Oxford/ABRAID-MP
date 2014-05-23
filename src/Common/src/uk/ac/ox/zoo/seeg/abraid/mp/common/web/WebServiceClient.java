package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.apache.log4j.Logger;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Acts as a web service client.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClient {
    private static final String GET_WEB_SERVICE_MESSAGE = "Making GET request to web service URL \"%s\"";
    private static final String POST_WEB_SERVICE_MESSAGE = "Making POST request to web service URL \"%s\"";
    private static final String CALLED_WEB_SERVICE_MESSAGE =  "Call to web service URL \"%s\" took %d ms";
    private static final String STATUS_UNSUCCESSFUL_MESSAGE =
            "Web service returned status code %d (\"%s\"). Web service URL: \"%s\"";
    private static final String GENERAL_ERROR_MESSAGE =
            "Error when accessing web service with URL \"%s\": %s";

    private static final Logger LOGGER = Logger.getLogger(WebServiceClient.class);

    private int connectTimeoutMilliseconds;
    private int readTimeoutMilliseconds;

    public void setConnectTimeoutMilliseconds(int connectTimeoutMilliseconds) {
        this.connectTimeoutMilliseconds = connectTimeoutMilliseconds;
    }

    public void setReadTimeoutMilliseconds(int readTimeoutMilliseconds) {
        this.readTimeoutMilliseconds = readTimeoutMilliseconds;
    }

    /**
     * Calls a web service by making a GET request.
     * @param url The web service URL to call.
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" is returned.
     */
    public String makeGetRequest(String url) throws WebServiceClientException {
        LOGGER.debug(String.format(GET_WEB_SERVICE_MESSAGE, url));
        return request(url, new SyncInvokerAction() {
            @Override
            public Response invoke(SyncInvoker invoker) {
                return invoker.get();
            }
        });
    }

    /**
     * Calls a web service by making a POST request.
     * @param url The web service URL to call.
     * @param bodyAsJson A string in JSON format that will be the body of the POST request.
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" is returned.
     */
    public String makePostRequestWithJSON(String url, final String bodyAsJson) throws WebServiceClientException {
        LOGGER.debug(String.format(POST_WEB_SERVICE_MESSAGE, url));
        return request(url, new SyncInvokerAction() {
            @Override
            public Response invoke(SyncInvoker invoker) {
                return invoker.post(Entity.entity(bodyAsJson, MediaType.APPLICATION_JSON_TYPE));
            }
        });
    }

    private String request(String url, SyncInvokerAction action) throws WebServiceClientException {
        try {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            Client client = ClientBuilder.newClient(clientConfig);
            client.property(ClientProperties.CONNECT_TIMEOUT, connectTimeoutMilliseconds);
            client.property(ClientProperties.READ_TIMEOUT, readTimeoutMilliseconds);

            // Buffer the request body and send it in one go instead of "chunking". This allows Jersey to handle
            // authentication challenges (e.g. if the server is using HTTP basic auth).
            client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);

            DateTime startDate = DateTime.now();
            Response response = action.invoke(client.target(url).request());

            // If the response's status code is not in the "successful" family, throw an exception
            Response.StatusType statusType = response.getStatusInfo();
            if (!statusType.getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                String message = String.format(STATUS_UNSUCCESSFUL_MESSAGE, statusType.getStatusCode(),
                        statusType.getReasonPhrase(), url);
                throw new WebServiceClientException(message);
            }

            DateTime endDate = DateTime.now();
            long callDuration = new Duration(startDate, endDate).getMillis();
            LOGGER.debug(String.format(CALLED_WEB_SERVICE_MESSAGE, url, callDuration));

            return response.readEntity(String.class);
        } catch (ProcessingException e) {
            // Jersey wraps javax.ws.rs.ProcessingException around all sorts of exceptions (unknown host, illegal
            // argument, time out, protocol not supported, etc.). We convert this to our WebServiceClientException;
            // as well as being consistent and friendly, it hides callers from the javax.ws.rs library.
            String message = String.format(GENERAL_ERROR_MESSAGE, url, getInnermostExceptionMessage(e));
            throw new WebServiceClientException(message, e);
        }
    }

    private String getInnermostExceptionMessage(Throwable t) {
        if (t.getCause() == null) {
            return t.getMessage();
        } else {
            return getInnermostExceptionMessage(t.getCause());
        }
    }

    /**
     * Performs an action on a SyncInvoker object. This is used to perform the desired type of HTTP request.
     */
    private interface SyncInvokerAction {
        Response invoke(SyncInvoker invoker);
    }
}
