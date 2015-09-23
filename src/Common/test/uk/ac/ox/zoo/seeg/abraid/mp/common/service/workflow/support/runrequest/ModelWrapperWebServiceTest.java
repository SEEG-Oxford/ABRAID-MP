package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Tests the ModelWrapperWebService class.
*
* Copyright (c) 2014 University of Oxford
*/
public class ModelWrapperWebServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private static final URI ROOT_URL = URI.create("http://localhost:8080/ModelWrapper");

    @Test
    public void startRunWithTypicalParameters() throws IOException, ZipException {
        // Arrange
        String expectedUrl = "http://localhost:8080/ModelWrapper/model/run";
        File mockZip = testFolder.newFile();
        FileUtils.writeStringToFile(mockZip, "Some fake content");
        String responseJson = "{ \"errorText\": \"Some Error\" }";

        ModelWrapperWebService webService = getModelWrapperWebService(expectedUrl, FileUtils.readFileToByteArray(mockZip), responseJson);

        // Act
        JsonModelRunResponse actualResponse = webService.startRun(ROOT_URL, mockZip);

        // Assert
        assertThat(actualResponse.getErrorText()).isEqualTo("Some Error");
    }

    @Test
    public void startRunPropagatesWebServiceClientException() throws IOException, ZipException {
        // Arrange
        File mockZip = testFolder.newFile();
        FileUtils.writeStringToFile(mockZip, "Some fake content");

        WebServiceClient client = mock(WebServiceClient.class);
        when(client.makePostRequestWithBinary(anyString(), any(byte[].class))).thenThrow(new WebServiceClientException(""));
        ModelWrapperWebService webService = getModelWrapperWebService(client);

        // Act
        catchException(webService).startRun(ROOT_URL, mockZip);

        // Assert
        assertThat(caughtException()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void startRunWithInvalidResponseJSONThrowsException() throws IOException, ZipException {
        // Arrange
        String expectedUrl = "http://localhost:8080/ModelWrapper/model/run";
        File mockZip = testFolder.newFile();
        FileUtils.writeStringToFile(mockZip, "Some fake content");

        String responseJson = "{ asdas }";

        ModelWrapperWebService webService = getModelWrapperWebService(expectedUrl, FileUtils.readFileToByteArray(mockZip), responseJson);

        // Act
        catchException(webService).startRun(ROOT_URL, mockZip);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
    }

    private ModelWrapperWebService getModelWrapperWebService(String expectedUrl, byte[] expectedRequestBody, String responseJson) throws IOException, ZipException {
        WebServiceClient client = mock(WebServiceClient.class);

        when(client.makePostRequestWithBinary(expectedUrl, expectedRequestBody)).thenReturn(responseJson);
        return getModelWrapperWebService(client);
    }

    private ModelWrapperWebService getModelWrapperWebService(WebServiceClient client) {
        return new ModelWrapperWebService(client);
    }
}
