package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;

import java.util.ArrayList;
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
    public void startRunDoesNotAcceptNull() throws Exception {
        // Arrange
        ModelRunController target = new ModelRunController(mock(RunConfigurationFactory.class), mock(ModelRunner.class));

        // Act
        ResponseEntity result = target.startRun(null);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void startRunAcceptsModelDataAndTriggersRun() throws Exception {
        // Arrange
        RunConfigurationFactory mockFactory = mock(RunConfigurationFactory.class);
        RunConfiguration mockConf = mock(RunConfiguration.class);
        ModelRunner mockRunner = mock(ModelRunner.class);
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
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void startRunHandlesExceptions() throws Exception {
        // Arrange
        ModelRunController target = new ModelRunController(null, null);

        GeoJsonDiseaseOccurrenceFeatureCollection object = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));

        // Act
        ResponseEntity result = target.startRun(new JsonModelRun(
                new JsonModelDisease(1, true, "foo", "foo"), object, new HashMap<Integer, Integer>()));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
