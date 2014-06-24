package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import static org.mockito.Mockito.*;

/**
 * Tests the ValidationParametersHandlerAsyncWrapper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidationParametersHandlerAsyncWrapperTest {
    @Test
    public void handleValidationParametersSuccessfulCase() throws Exception {
        // Arrange
        ValidationParametersHandler handler = mock(ValidationParametersHandler.class);
        ValidationParametersHandlerAsyncWrapper wrapper = new ValidationParametersHandlerAsyncWrapper(handler);
        ModelRun modelRun = new ModelRun();

        // Act
        wrapper.handleValidationParameters(modelRun).get();

        // Assert
        verify(handler, times(1)).handleValidationParameters(same(modelRun));
    }

    @Test
    public void handleValidationParametersWithExceptionThrown() throws Exception {
        // Arrange
        ValidationParametersHandler handler = mock(ValidationParametersHandler.class);
        ValidationParametersHandlerAsyncWrapper wrapper = new ValidationParametersHandlerAsyncWrapper(handler);
        ModelRun modelRun = new ModelRun(1);

        doThrow(new RuntimeException("Test message")).when(handler).handleValidationParameters(modelRun);

        // Act
        wrapper.handleValidationParameters(modelRun).get();

        // Assert
        verify(handler, times(1)).handleValidationParameters(same(modelRun));
    }
}
