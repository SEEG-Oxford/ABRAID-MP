package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

/**
 * Tests the WebServiceClient class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClientTest {
    @Test
    public void getUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).request("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void getMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).request("this is malformed");

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void getUnknownPage() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        catchException(client).request("http://www.google.co.uk/kjhdfgoiunewrpoimclsd");

        // Assert: see annotation for expected exception
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void getGoogleHomePage() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        String response = client.request("http://www.google.co.uk");

        // Assert: see annotation for expected exception
        assertThat(response).containsIgnoringCase("google");
    }
}
