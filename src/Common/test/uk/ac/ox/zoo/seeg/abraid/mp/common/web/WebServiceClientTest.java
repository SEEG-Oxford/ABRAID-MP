package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the WebServiceClient class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClientTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private static final String GET_URL = "http://www.google.co.uk";

    // This is a POST data echo service
    private static final String POST_URL = "https://eu.httpbin.org/post";
    private static final String PUT_URL = "https://eu.httpbin.org/put";

    @Test
    public void makeGetRequestThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makeGetRequest("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makeGetRequest("this is malformed");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestThrowsExceptionIfUnknownPage() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makeGetRequest("http://www.google.co.uk/kjhdfgoiunewrpoimclsd");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makeGetRequestSuccessfullyGetsValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        String response = client.makeGetRequest(GET_URL);

        // Assert
        assertThat(response).containsIgnoringCase("google");
    }

    @Test
    public void makePostRequestWithJSONThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePostRequestWithJSON("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithJSONThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePostRequestWithJSON("this is malformed", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithJSONSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);
        String name = "Harry Hill";

        // Act
        String json = "{ \"name\": \"" + name + "\", \"age\": 49, \"dateOfBirth\": \"1964-10-01\" }";
        String response = client.makePostRequestWithJSON(POST_URL, json);

        // Assert
        assertThat(response).containsIgnoringCase("application/json");
        assertThat(response).containsIgnoringCase(name);
    }

    @Test
    public void makePostRequestWithXMLThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePostRequestWithXML("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithXMLThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePostRequestWithXML("this is malformed", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithXMLSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);
        String xml = "<test><name>Harry Hill</name></test>";

        // Act
        String response = client.makePostRequestWithXML(POST_URL, xml);

        // Assert
        assertThat(response).containsIgnoringCase("\"Content-Type\": \"application/xml\"");
        assertThat(response).containsIgnoringCase("\"data\": \"" + xml + "\"");
        assertThat(response).containsIgnoringCase("\"url\": \"" + POST_URL + "\"");
    }

    @Test
    public void makePutRequestWithXMLThrowsExceptionIfUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePutRequestWithXML("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePutRequestWithXMLThrowsExceptionIfMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePutRequestWithXML("this is malformed", "");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePutRequestWithXMLSuccessfullyPostsToValidURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);
        String xml = "<test><name>Harry Hill</name></test>";

        // Act
        String response = client.makePutRequestWithXML(PUT_URL, xml);

        // Assert
        assertThat(response).containsIgnoringCase("\"Content-Type\": \"application/xml\"");
        assertThat(response).containsIgnoringCase("\"data\": \"" + xml + "\"");
        assertThat(response).containsIgnoringCase("\"url\": \"" + PUT_URL + "\"");
    }

    @Test
    public void makePostRequestWithBinaryThrowsExceptionIfUnknownHost() throws IOException {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePostRequestWithBinary("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be", getFile(null));

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithBinaryThrowsExceptionIfMalformedURL() throws IOException {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        catchException(client).makePostRequestWithBinary("this is malformed", getFile(null));

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void makePostRequestWithBinarySuccessfullyPostsToValidURL() throws IOException {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);
        String bodyAsString = "Test body";

        // Act
        String response = client.makePostRequestWithBinary(POST_URL, getFile(bodyAsString));

        // Assert
        assertThat(response).containsIgnoringCase("multipart/form-data");
        assertThat(response).containsIgnoringCase("\"file\": \"" + bodyAsString + "\"");
    }

    @Test
    public void makeGetRequestWithBasicAuth() throws IOException {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        String response = client.makeGetRequest("https://abc:efg@httpbin.org/basic-auth/abc/efg");

        // Assert
        assertThat(response).containsIgnoringCase("\"authenticated\": true");
    }

    @Test
    public void followRedirect() throws IOException {
        // Arrange
        WebServiceClient client = new WebServiceClient(60000, 60000);

        // Act
        String response = client.makeGetRequest("https://httpbin.org/redirect/6");

        // Assert
        assertThat(response).containsIgnoringCase("\"url\": \"https://httpbin.org/get\"");
    }

    private File getFile(String content) throws IOException {
        File file = testFolder.newFile();
        FileUtils.writeStringToFile(file, content == null ? "1234" : content);
        return file;
    }
}
