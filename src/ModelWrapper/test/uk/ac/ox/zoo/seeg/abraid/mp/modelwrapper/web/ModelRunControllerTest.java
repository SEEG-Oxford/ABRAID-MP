package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.ExecutionRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelOutputHandlerWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunnerAsyncWrapperImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelStatusReporter;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.api.ModelRunController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence;

/**
 * Tests for ModelRunController.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunControllerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void startRunDoesNotAcceptNull() {
        // Arrange
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);
        ModelRunController target = new ModelRunController(mock(RunConfigurationFactory.class), mock(ModelRunnerAsyncWrapperImpl.class), mock(ModelOutputHandlerWebService.class), objectMapper);

        // Act
        ResponseEntity result = target.startRun(null);

        // Assert
        assertResponseEntity(result, null, "Run data must be provided and be valid.", HttpStatus.BAD_REQUEST);
    }

    @Test
    public void startRunAcceptsModelDataAndTriggersRun() throws Exception {
        // Arrange
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);
        String runName = "foo_2014-04-24-10-50-27_cd0efc75-42d3-4d96-94b4-287e28fbcdac";
        RunConfigurationFactory mockFactory = mock(RunConfigurationFactory.class);
        RunConfiguration mockConf = mock(RunConfiguration.class);
        ModelRunnerAsyncWrapperImpl mockRunner = mock(ModelRunnerAsyncWrapperImpl.class);
        when(mockConf.getRunName()).thenReturn(runName);
        when(mockConf.getExecutionConfig()).thenReturn(mock(ExecutionRunConfiguration.class));
        when(mockFactory.createDefaultConfiguration(any(File.class), anyBoolean(), anyString())).thenReturn(mockConf);

        ModelRunController target = new ModelRunController(mockFactory, mockRunner, mock(ModelOutputHandlerWebService.class), objectMapper);

        GeoJsonDiseaseOccurrenceFeatureCollection occurrence = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));
        Map<Integer, Integer> extent = new HashMap<>();

        when(objectMapper.readValue(eq("metadata"), eq(JsonModelRun.class))).thenReturn(new JsonModelRun(new JsonModelDisease(1, true, "foo", "foo"), occurrence, extent));

        // Act
        ResponseEntity result = target.startRun(fakeData());

        // Assert
        // start model correctly
        verify(mockRunner).startModel(eq(mockConf), eq(occurrence), eq(extent), any(ModelStatusReporter.class));
        // extracted covariates correctly
        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        verify(mockFactory).createDefaultConfiguration(captor.capture(), eq(true), eq("foo"));
        String tempDir = captor.getValue().getAbsolutePath();
        assertThat(Paths.get(tempDir, "covariates", "a").toFile()).exists();
        assertThat(Paths.get(tempDir, "covariates", "a").toFile()).hasContent("c1");
        assertThat(Paths.get(tempDir, "covariates", "c", "b").toFile()).exists();
        assertThat(Paths.get(tempDir, "covariates", "c", "b").toFile()).hasContent("c2");
        // give correct result
        assertResponseEntity(result, runName, null, HttpStatus.OK);
    }

    @Test
    public void startRunHandlesExceptions() throws IOException, ZipException {
        // Arrange
        AbraidJsonObjectMapper objectMapper = mock(AbraidJsonObjectMapper.class);
        ModelRunController target = new ModelRunController(null, null, null, objectMapper);

        GeoJsonDiseaseOccurrenceFeatureCollection object = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));

        when(objectMapper.readValue(eq("metadata"), eq(JsonModelRun.class))).thenReturn(new JsonModelRun(new JsonModelDisease(1, true, "foo", "foo"), object, new HashMap<Integer, Integer>()));

        // Act
        ResponseEntity result = target.startRun(fakeData());

        // Assert
        assertResponseEntity(result, null, "Could not start model run. See server logs for more details.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void assertResponseEntity(ResponseEntity response,
                                      String expectedModelRunName,
                                      String expectedErrorText,
                                      HttpStatus expectedStatus) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getBody()).isInstanceOf(JsonModelRunResponse.class);
        JsonModelRunResponse responseBody = (JsonModelRunResponse) response.getBody();
        assertThat(responseBody.getModelRunName()).isEqualTo(expectedModelRunName);
        assertThat(responseBody.getErrorText()).isEqualTo(expectedErrorText);
    }

    private byte[] fakeData() throws IOException, ZipException {
        File file = testFolder.newFile();
        Files.delete(file.toPath());
        ZipFile zipFile = new ZipFile(file);
        File dir = testFolder.newFolder();
        File metadata = Paths.get(dir.getAbsolutePath(), "metadata.json").toFile();
        FileUtils.writeStringToFile(metadata, "metadata");
        File c1 = Paths.get(dir.getAbsolutePath(), "covariates", "a").toFile();
        File c2 = Paths.get(dir.getAbsolutePath(), "covariates", "c", "b").toFile();
        FileUtils.writeStringToFile(c1, "c1");
        FileUtils.writeStringToFile(c2, "c2");
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setIncludeRootFolder(false);
        zipFile.createZipFileFromFolder(dir, zipParameters, false, 0);
        return FileUtils.readFileToByteArray(file);
    }
}
