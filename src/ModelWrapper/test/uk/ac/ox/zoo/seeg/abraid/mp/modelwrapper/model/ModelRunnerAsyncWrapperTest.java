package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.util.HashMap;
import java.util.concurrent.Future;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelRunnerAsyncWrapperImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunnerAsyncWrapperTest {
    @Test
    public void startModelTriggersAModelRun() throws Exception {
        // Arrange
        RunConfiguration expectedRunConfig = mock(RunConfiguration.class);
        GeoJsonDiseaseOccurrenceFeatureCollection expectedOccurrences = mock(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        HashMap<Integer, Integer> expectedWeightings = new HashMap<>();
        ModelProcessHandler expectedResult = mock(ModelProcessHandler.class);

        ModelRunner mockModelRunner = mock(ModelRunner.class);
        when(mockModelRunner.runModel(expectedRunConfig, expectedOccurrences, expectedWeightings)).thenReturn(expectedResult);

        ModelRunnerAsyncWrapper target = new ModelRunnerAsyncWrapperImpl(mockModelRunner);

        // Act
        Future<ModelProcessHandler> future = target.startModel(expectedRunConfig, expectedOccurrences, expectedWeightings);
        // At this stage ModelRunner.runModel may or may not have been called yet (threads)
        // Future.get allows use to wait for the model run thread to complete and get the result
        ModelProcessHandler result = future.get();

        // Assert
        assertThat(result).isSameAs(expectedResult);
        verify(mockModelRunner, times(1)).runModel(expectedRunConfig, expectedOccurrences, expectedWeightings);
    }
}
