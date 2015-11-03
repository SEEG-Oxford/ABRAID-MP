package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.apache.http.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.http.*;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;

/**
 * Acts as a web service client.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClient {
    private static final String GET_WEB_SERVICE_MESSAGE = "Making GET request to web service URL \"%s\"";
    private static final String POST_WEB_SERVICE_MESSAGE = "Making POST request to web service URL \"%s\" (%s %s)";
    private static final String PUT_WEB_SERVICE_MESSAGE = "Making PUT request to web service URL \"%s\" (%s %s)";
    private static final String CALLED_WEB_SERVICE_MESSAGE =  "Call to web service URL \"%s\" took %d ms";
    private static final String STATUS_UNSUCCESSFUL_MESSAGE =
            "Web service returned status code %d (\"%s\"). Web service URL: \"%s\"";
    private static final String INVALID_URL_MESSAGE =
            "Error when accessing web service with URL \"%s\": Invalid URL - %s";
    private static final String GENERAL_ERROR_MESSAGE =
            "Error when accessing web service with URL \"%s\": %s";

    private static final Logger LOGGER = Logger.getLogger(WebServiceClient.class);

    private CloseableHttpClient httpClient;
    private BasicResponseHandler responseHandler;

    public WebServiceClient(int connectTimeoutMilliseconds, int readTimeoutMilliseconds) {
        this.httpClient = createHttpClient(connectTimeoutMilliseconds, readTimeoutMilliseconds);
        this.responseHandler = new BasicResponseHandler();
    }

    private CloseableHttpClient createHttpClient(int connectTimeoutMilliseconds, int readTimeoutMilliseconds) {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(connectTimeoutMilliseconds);
        requestBuilder = requestBuilder.setSocketTimeout(readTimeoutMilliseconds);
        RequestConfig requestConfig = requestBuilder.build();
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder = clientBuilder.setDefaultRequestConfig(requestConfig);
        clientBuilder = clientBuilder.disableAutomaticRetries();
        clientBuilder = clientBuilder.addInterceptorFirst(new PreemptiveAuthInterceptor());
        return clientBuilder.build();
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
        return request(createRequest(url, HttpMethod.GET).build());
    }

    /**
     * Calls a web service by making a POST request.
     * @param url The web service URL to call.
     * @param body A string in JSON format that will be the body of the POST request.
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" is returned.
     */
    public String makePostRequestWithJSON(String url, final String body) throws WebServiceClientException {
        if (body == null) {
            throw new IllegalArgumentException("POST body must be non-null");
        }

        LOGGER.debug(String.format(POST_WEB_SERVICE_MESSAGE, url, body.length(), "characters"));
        return request(createRequest(url, HttpMethod.POST, body, ContentType.APPLICATION_JSON).build());
    }

    /**
     * Calls a web service by making a POST request.
     * @param url The web service URL to call.
     * @param body The body as an array of bytes.
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" is returned.
     */
    public String makePostRequestWithBinary(String url, final File body) throws WebServiceClientException {
        if (body == null || body.length() == 0) {
            throw new IllegalArgumentException("POST body must be non-null");
        }

        LOGGER.debug(String.format(POST_WEB_SERVICE_MESSAGE, url, body.length(), "bytes"));
        RequestBuilder request = createRequest(url, HttpMethod.POST, body);

        HttpUriRequest build = request.build();

        return request(build);
    }

    /**
     * Calls a web service by making a PUT request.
     * @param url The web service URL to call.
     * @param body The body of the request.
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" (or anything else in the 200 family) is returned.
     */
    public String makePutRequest(String url, final String body) throws WebServiceClientException {
        if (body == null) {
            throw new IllegalArgumentException("PUT body must be non-null");
        }

        LOGGER.debug(String.format(PUT_WEB_SERVICE_MESSAGE, url, body.length(), "characters"));
        return request(createRequest(url, HttpMethod.PUT, body, ContentType.TEXT_PLAIN).build());
    }

    /**
     * Calls a web service by making a POST request with a "text/xml" "Content-type" header.
     * @param url The web service URL to call.
     * @param body The body of the request (should be xml).
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" (or anything else in the 200 family) is returned.
     */
    public String makePostRequestWithXML(String url, final String body) throws WebServiceClientException {
        if (body == null) {
            throw new IllegalArgumentException("POST body must be non-null");
        }

        LOGGER.debug(String.format(POST_WEB_SERVICE_MESSAGE, url, body.length(), "characters"));
        return request(createRequest(url, HttpMethod.POST, body, ContentType.TEXT_XML).build());
    }

    /**
     * Calls a web service by making a PUT request with a "text/xml" "Content-type" header.
     * @param url The web service URL to call.
     * @param body The body of the request (should be xml).
     * @return The web service response as a string.
     * @throws WebServiceClientException If a response could not be obtained from the web service for whatever reason,
     * or if a response status code other than "successful" (or anything else in the 200 family) is returned.
     */
    public String makePutRequestWithXML(String url, final String body) throws WebServiceClientException {
        if (body == null) {
            throw new IllegalArgumentException("PUT body must be non-null");
        }

        LOGGER.debug(String.format(PUT_WEB_SERVICE_MESSAGE, url, body.length(), "characters"));
        return request(createRequest(url, HttpMethod.PUT, body, ContentType.TEXT_XML).build());
    }

    private RequestBuilder createRequest(String url, HttpMethod method) {
        try {
            return RequestBuilder.create(method.name()).setUri(url);
        } catch (IllegalArgumentException e) {
            String message = String.format(INVALID_URL_MESSAGE, url, getInnermostExceptionMessage(e));
            throw new WebServiceClientException(message, e);
        }
    }

    private RequestBuilder createRequest(String url, HttpMethod method, HttpEntity body) {
        return createRequest(url, method).setEntity(body);
    }

    private RequestBuilder createRequest(String url, HttpMethod method, String body, ContentType contentType) {
        return createRequest(url, method, new StringEntity(body, contentType));
    }
    private RequestBuilder createRequest(String url, HttpMethod method, File body) {
        return createRequest(url, method, MultipartEntityBuilder.create().addPart("file", new FileBody(body)).build());
    }

    private String request(HttpUriRequest request) {
        try {
            DateTime startDate = DateTime.now();
            String response = httpClient.execute(request, responseHandler);
            DateTime endDate = DateTime.now();

            long callDuration = new Duration(startDate, endDate).getMillis();
            LOGGER.debug(String.format(CALLED_WEB_SERVICE_MESSAGE, request.getURI(), callDuration));

            return response;
        } catch (HttpResponseException e) {
            String status = HttpStatus.valueOf(e.getStatusCode()).getReasonPhrase();
            String message = String.format(STATUS_UNSUCCESSFUL_MESSAGE, e.getStatusCode(), status, request.getURI());
            throw new WebServiceClientException(message);
        } catch (IOException e) {
            // We convert this to our WebServiceClientException; as well as being consistent and friendly,
            // it hides callers from the http client implementation library.
            String message = String.format(GENERAL_ERROR_MESSAGE, request.getURI(), getInnermostExceptionMessage(e));
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
     * A HttpRequestInterceptor to enable preemptive basic auth (ie 1 req, not 2) if credential specified in the url.
     */
    private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

            Credentials creds = getCredentials(context);
            AuthScheme authScheme = getAuthScheme(authState);
            if (creds != null && authScheme == null) {
                // If credentials have been provided (i.e. basic auth in the URI), but there isn't an auth scheme setup
                // then preemptively set up basic auth.
                authState.update(new BasicScheme(), creds);
            }
        }

        private AuthScheme getAuthScheme(AuthState authState) {
            return authState == null ? null : authState.getAuthScheme();
        }

        private Credentials getCredentials(HttpContext context) {
            CredentialsProvider credsProvider =
                    (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
            HttpHost targetHost = (HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
            return credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
        }
    }
}
