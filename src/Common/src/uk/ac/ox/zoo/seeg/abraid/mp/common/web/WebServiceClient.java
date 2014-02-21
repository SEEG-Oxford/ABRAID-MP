package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

import static java.lang.String.format;

/**
 * Acts as a web service client.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClient {
    private static final String CALLING_WEB_SERVICE_MESSAGE = "Calling web service URL \"%s\"";
    private static final String STATUS_UNSUCCESSFUL_MESSAGE =
            "Web service returned status code %d (\"%s\"). Web service URL: \"%s\"";
    private static final String GENERAL_ERROR_MESSAGE =
            "Error when accessing web service with URL \"%s\": %s";
    private static final int CONNECT_TIMEOUT_MILLISECONDS = 60000;
    private static final int READ_TIMEOUT_MILLISECONDS = 60000;

    private static final Logger log = Logger.getLogger(WebServiceClient.class);

    /**
     * Calls a web service.
     * @param url The web service URL to call.
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" is returned.
     */
    public String request(String url) throws WebServiceClientException {
        try {
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.connectorProvider(new ApacheConnectorProvider());
            Client client = ClientBuilder.newClient(clientConfig);
            client.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT_MILLISECONDS);
            client.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT_MILLISECONDS);

            log.info(format(CALLING_WEB_SERVICE_MESSAGE, url));
            Response response = client.target(url).request().get();

            // If the response's status code is not in the "successful" family, throw an exception
            Response.StatusType statusType = response.getStatusInfo();
            if (!statusType.getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                String message = format(STATUS_UNSUCCESSFUL_MESSAGE, statusType.getStatusCode(),
                        statusType.getReasonPhrase(), url);
                throw new WebServiceClientException(message);
            }

            return response.readEntity(String.class);
        }
        catch(ProcessingException e) {
            // Jersey wraps javax.ws.rs.ProcessingException around all sorts of exceptions (unknown host, illegal
            // argument, time out, protocol not supported, etc.). We convert this to our WebServiceClientException;
            // as well as being consistent and friendly, it hides callers from the javax.ws.rs library.
            String message = format(GENERAL_ERROR_MESSAGE, url, getInnermostExceptionMessage(e));
            throw new WebServiceClientException(message, e);
        }
    }

    private String getInnermostExceptionMessage(Throwable t) {
        if (t.getCause() == null) {
            return t.getMessage();
        }
        else {
            return getInnermostExceptionMessage(t.getCause());
        }
    }
}
