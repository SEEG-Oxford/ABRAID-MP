package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

import java.io.InputStream;
import java.io.OutputStream;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelProcessHandler.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelProcessHandlerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void onProcessCompleteCallsStatusReporterCorrectly() throws Exception {
        // Arrange
        ModelStatusReporter mockStatusReporter = mock(ModelStatusReporter.class);
        ModelProcessHandler target = new ModelProcessHandler(mockStatusReporter);

        // Act
        target.onProcessComplete();

        // Assert
        verify(mockStatusReporter, times(1)).report(ModelRunStatus.COMPLETED, "", "");
    }

    @Test
    public void onProcessFailedCallsStatusReporterCorrectly() throws Exception {
        // Arrange
        ModelStatusReporter mockStatusReporter = mock(ModelStatusReporter.class);
        ProcessException mockProcessException = mock(ProcessException.class);
        when(mockProcessException.getMessage()).thenReturn("expectedMessage");
        ModelProcessHandler target = new ModelProcessHandler(mockStatusReporter);

        // Act
        target.onProcessFailed(mockProcessException);

        // Assert
        verify(mockStatusReporter, times(1)).report(ModelRunStatus.FAILED, "", "Error message: expectedMessage. Standard error: ");
    }

    @Test
    public void getOutputStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(mock(ModelStatusReporter.class));

        // Act
        OutputStream result = target.getOutputStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void getInputStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(mock(ModelStatusReporter.class));

        // Act
        InputStream result = target.getInputStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void getErrorStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(mock(ModelStatusReporter.class));

        // Act
        OutputStream result = target.getErrorStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void waitForCompletionShouldThrowIfWaiterHasNotBeenSet() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(mock(ModelStatusReporter.class));

        // Act
        catchException(target).waitForCompletion();
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IllegalStateException.class);
    }
}
