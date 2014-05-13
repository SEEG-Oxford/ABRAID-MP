package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence;

/**
 * Tests for ModelRunController.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunControllerTest {
    @Test
    public void startRunDoesNotAcceptNull() {
        // Arrange
        ModelRunController target = new ModelRunController(mock(RunConfigurationFactory.class), mock(ModelRunner.class));

        // Act
        ResponseEntity result = target.startRun(null);

        // Assert
        assertResponseEntity(result, null, "Run data must be provided and be valid.", HttpStatus.BAD_REQUEST);
    }

    @Test
    public void startRunAcceptsModelDataAndTriggersRun() throws Exception {
        // Arrange
        String runName = "foo_2014-04-24-10-50-27_cd0efc75-42d3-4d96-94b4-287e28fbcdac";
        RunConfigurationFactory mockFactory = mock(RunConfigurationFactory.class);
        RunConfiguration mockConf = mock(RunConfiguration.class);
        ModelRunner mockRunner = mock(ModelRunner.class);
        when(mockConf.getRunName()).thenReturn(runName);
        when(mockFactory.createDefaultConfiguration(anyInt(), anyBoolean(), anyString(), anyString())).thenReturn(mockConf);

        ModelRunController target = new ModelRunController(mockFactory, mockRunner);

        GeoJsonDiseaseOccurrenceFeatureCollection occurrence = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));
        Map<Integer, Integer> extent = new HashMap<>();

        // Act
        ResponseEntity result = target.startRun(new JsonModelRun(
                new JsonModelDisease(1, true, "foo", "foo"), occurrence, extent));

        // Assert
        verify(mockRunner, times(1)).runModel(mockConf, occurrence, extent);
        assertResponseEntity(result, runName, null, HttpStatus.OK);
    }

    @Test
    public void startRunHandlesExceptions() {
        // Arrange
        ModelRunController target = new ModelRunController(null, null);

        GeoJsonDiseaseOccurrenceFeatureCollection object = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));

        // Act
        ResponseEntity result = target.startRun(new JsonModelRun(
                new JsonModelDisease(1, true, "foo", "foo"), object, new HashMap<Integer, Integer>()));

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
}
