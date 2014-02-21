package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the WebServiceClient class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WebServiceClientTest {
    @Test(expected=WebServiceClientException.class)
    public void getUnknownHost() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        client.request("http://uywnevoweiumoiunasdkjhaskjdhiouyncwiuec.be");

        // Assert: see annotation for expected exception
    }

    @Test(expected=WebServiceClientException.class)
    public void getMalformedURL() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        client.request("this is malformed");

        // Assert: see annotation for expected exception
    }

    @Test(expected=WebServiceClientException.class)
    public void getUnknownPage() {
        // Arrange
        WebServiceClient client = new WebServiceClient();

        // Act
        client.request("http://www.google.co.uk/kjhdfgoiunewrpoimclsd");

        // Assert: see annotation for expected exception
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
