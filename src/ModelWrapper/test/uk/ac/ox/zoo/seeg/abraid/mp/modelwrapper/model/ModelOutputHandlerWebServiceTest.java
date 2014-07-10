package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the ModelOutputHandlerWebService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelOutputHandlerWebServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void handleOutputsCallsWebServiceCorrectly() throws Exception {
        // Arrange
        byte[] testBody = "This is a test POST body".getBytes();
        File testBodyFile = testFolder.newFile();
        FileUtils.writeByteArrayToFile(testBodyFile, testBody);

        ConfigurationService configurationService = mock(ConfigurationService.class);
        String rootUrl = "http://localhost:8080/modeloutputhandler/";
        when(configurationService.getModelOutputHandlerRootUrl()).thenReturn(rootUrl);

        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        String expectedUrl = rootUrl + "modeloutputhandler/handleoutputs";
        when(webServiceClient.makePostRequest(expectedUrl, testBody)).thenReturn("expected Result");

        ModelOutputHandlerWebService target = new ModelOutputHandlerWebService(webServiceClient, configurationService);

        // Act
        String actualResponse = target.handleOutputs(testBodyFile);

        // Assert
        assertThat(actualResponse).isEqualTo("expected Result");
        verify(webServiceClient, times(1)).makePostRequest(expectedUrl, testBody);
    }
}
