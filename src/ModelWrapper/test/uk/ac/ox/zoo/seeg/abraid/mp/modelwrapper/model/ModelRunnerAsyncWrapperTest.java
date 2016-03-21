package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        ModelStatusReporter expectedModelStatusReporter = mock(ModelStatusReporter.class);
        ModelProcessHandler expectedResult = mock(ModelProcessHandler.class);

        ModelRunner mockModelRunner = mock(ModelRunner.class);
        when(mockModelRunner.runModel(expectedRunConfig, expectedModelStatusReporter)).thenReturn(expectedResult);

        ModelRunnerAsyncWrapper target = new ModelRunnerAsyncWrapperImpl(mockModelRunner);

        // Act
        Future<ModelProcessHandler> future = target.startModel(expectedRunConfig, expectedModelStatusReporter);
        // At this stage ModelRunner.runModel may or may not have been called yet (threads)
        // Future.get allows use to wait for the model run thread to complete and get the result
        ModelProcessHandler result = future.get();

        // Assert
        assertThat(result).isSameAs(expectedResult);
        verify(mockModelRunner).runModel(expectedRunConfig, expectedModelStatusReporter);
    }

    @Test
    public void startModelReportsErrorsDuringModelSetup() throws Exception {
        // Arrange
        ModelStatusReporter mockModelStatusReporter = mock(ModelStatusReporter.class);

        ModelRunner mockModelRunner = mock(ModelRunner.class);
        when(mockModelRunner.runModel(any(RunConfiguration.class), any(ModelStatusReporter.class)))
                .thenThrow(new IOException("message"));

        ModelRunnerAsyncWrapper target = new ModelRunnerAsyncWrapperImpl(mockModelRunner);

        // Act
        Future<ModelProcessHandler> future = target.startModel(
                mock(RunConfiguration.class), mockModelStatusReporter);
        future.get();

        // Assert
        verify(mockModelStatusReporter).report(ModelRunStatus.FAILED, "", "Model setup failed: java.io.IOException: message");
    }

    private List<String> actions = new ArrayList<>();
    private boolean pauseModelRun = true;
    private boolean readyToAssert = false;
    @Test
    public void startModelDoesNotTriggerASecondModelRunUntilTheFirstHasCompleted() throws Exception {
        // Arrange
        actions.clear();
        ModelStatusReporter expectedModelStatusReporter = mock(ModelStatusReporter.class);

        final RunConfiguration firstExpectedRunConfig = mock(RunConfiguration.class);
        final ModelProcessHandler firstExpectedHandler = mock(ModelProcessHandler.class);

        final RunConfiguration secondExpectedRunConfig = mock(RunConfiguration.class);
        final ModelProcessHandler secondExpectedHandler = mock(ModelProcessHandler.class);

        ModelRunner mockModelRunner = mock(ModelRunner.class);
        when(mockModelRunner.runModel(firstExpectedRunConfig, expectedModelStatusReporter)).thenAnswer(
                new Answer<ModelProcessHandler>() {
                    @Override
                    public ModelProcessHandler answer(InvocationOnMock invocationOnMock) throws Throwable {
                        actions.add("first run started");
                        return firstExpectedHandler;
                    }
                });
        when(firstExpectedHandler.waitForCompletion()).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                actions.add("first run paused");
                readyToAssert = true;
                while (pauseModelRun) {
                    Thread.sleep(1);
                }
                actions.add("first run completed");
                return 0;
            }
        });
        when(mockModelRunner.runModel(secondExpectedRunConfig, expectedModelStatusReporter)).thenAnswer(
                new Answer<ModelProcessHandler>() {
                    @Override
                    public ModelProcessHandler answer(InvocationOnMock invocationOnMock) throws Throwable {
                        actions.add("second run started");
                        return secondExpectedHandler;
                    }
                });
        when(secondExpectedHandler.waitForCompletion()).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                actions.add("second run completed");
                return 0;
            }
        });

        ModelRunnerAsyncWrapper target = new ModelRunnerAsyncWrapperImpl(mockModelRunner);

        // Act
        actions.add("first run triggered");
        Future<ModelProcessHandler> future1 = target.startModel(firstExpectedRunConfig, expectedModelStatusReporter);
        actions.add("second run triggered");
        Future<ModelProcessHandler> future2 = target.startModel(secondExpectedRunConfig, expectedModelStatusReporter);

        // Assert
        while (!readyToAssert) {
            Thread.sleep(1);
        }
        assertThat(actions).containsOnly("first run triggered", "second run triggered", "first run started", "first run paused");
        pauseModelRun = false;
        future1.get();
        future2.get();
        assertThat(actions).containsSequence("first run completed", "second run started", "second run completed");
    }
}
