package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.ExecuteException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils.captorForClass;

/**
 * Tests the ForwardingExecuteResultHandler class.
 * Copyright (c) 2014 University of Oxford
 */
public class ForwardingExecuteResultHandlerTest {
    @Test
    public void onProcessCompleteShouldBeWrappedCorrectly() throws Exception {
        // Arrange
        ProcessHandler mockProcessHandler = mock(ProcessHandler.class);
        ForwardingExecuteResultHandler target = new ForwardingExecuteResultHandler(mockProcessHandler);

        // Act
        target.onProcessComplete(4321);

        // Assert
        verify(mockProcessHandler).onProcessComplete();
    }

    @Test
    public void onProcessFailedShouldWrapCause() throws Exception {
        // Arrange
        ProcessHandler mockProcessHandler = mock(ProcessHandler.class);
        ForwardingExecuteResultHandler target = new ForwardingExecuteResultHandler(mockProcessHandler);
        ExecuteException expectedCause = new ExecuteException("foo", -123);

        // Act
        target.onProcessFailed(expectedCause);

        // Assert
        ArgumentCaptor<ProcessException> captor = captorForClass(ProcessException.class);
        verify(mockProcessHandler).onProcessFailed(captor.capture());
        Throwable cause = captor.getValue().getCause();
        assertThat(cause).isEqualTo(expectedCause);
    }
}
