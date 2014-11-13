package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
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
        ModelStatusReporter expectedModelStatusReporter = mock(ModelStatusReporter.class);
        HashMap<Integer, Integer> expectedWeightings = new HashMap<>();
        ModelProcessHandler expectedResult = mock(ModelProcessHandler.class);

        ModelRunner mockModelRunner = mock(ModelRunner.class);
        when(mockModelRunner.runModel(expectedRunConfig, expectedOccurrences, expectedWeightings, expectedModelStatusReporter)).thenReturn(expectedResult);

        ModelRunnerAsyncWrapper target = new ModelRunnerAsyncWrapperImpl(mockModelRunner);

        // Act
        Future<ModelProcessHandler> future = target.startModel(expectedRunConfig, expectedOccurrences, expectedWeightings, expectedModelStatusReporter);
        // At this stage ModelRunner.runModel may or may not have been called yet (threads)
        // Future.get allows use to wait for the model run thread to complete and get the result
        ModelProcessHandler result = future.get();

        // Assert
        assertThat(result).isSameAs(expectedResult);
        verify(mockModelRunner).runModel(expectedRunConfig, expectedOccurrences, expectedWeightings, expectedModelStatusReporter);
    }

    @Test
    public void startModelReportsErrorsDuringModelSetup() throws Exception {
        // Arrange
        ModelStatusReporter mockModelStatusReporter = mock(ModelStatusReporter.class);

        ModelRunner mockModelRunner = mock(ModelRunner.class);
        when(mockModelRunner.runModel(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class), any(ModelStatusReporter.class)))
                .thenThrow(new IOException("message"));

        ModelRunnerAsyncWrapper target = new ModelRunnerAsyncWrapperImpl(mockModelRunner);

        // Act
        Future<ModelProcessHandler> future = target.startModel(
                mock(RunConfiguration.class), mock(GeoJsonDiseaseOccurrenceFeatureCollection.class), new HashMap<Integer, Integer>(), mockModelStatusReporter);
        future.get();

        // Assert
        verify(mockModelStatusReporter).report(ModelRunStatus.FAILED, "", "Model setup failed: java.io.IOException: message");
    }
}
