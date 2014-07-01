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
        String rootUrl = "http://localhost:8080/ModelOutputHandler/";
        String expectedUrl = "http://localhost:8080/ModelOutputHandler/modeloutputhandler/handleoutputs";

        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        ConfigurationService configurationService = mock(ConfigurationService.class);
        ModelOutputHandlerWebService webService = new ModelOutputHandlerWebService(webServiceClient, configurationService);
        when(configurationService.getModelOutputHandlerRootUrl()).thenReturn(rootUrl);
        when(webServiceClient.makePostRequest(expectedUrl, testBody)).thenReturn("");

        // Act
        String actualResponse = webService.handleOutputs(testBodyFile);

        // Assert
        assertThat(actualResponse).isEqualTo("");
    }
}
