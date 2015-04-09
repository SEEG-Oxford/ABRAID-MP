package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import static org.mockito.Mockito.*;

/**
 * Tests the HandlersAsyncWrapper class.
 * Copyright (c) 2014 University of Oxford
 */
public class HandlersAsyncWrapperTest {
    @Test
    public void handlersAreCalledSuccessfully() throws Exception {
        // Arrange
        DiseaseOccurrenceHandler diseaseOccurrenceHandler = mock(DiseaseOccurrenceHandler.class);
        HandlersAsyncWrapper wrapper = new HandlersAsyncWrapper(diseaseOccurrenceHandler);

        ModelRun modelRun = new ModelRun();

        // Act
        wrapper.handle(modelRun).get();

        // Assert
        verify(diseaseOccurrenceHandler).handle(same(modelRun));
    }

    @Test
    public void exceptionThrownByAHandlerIsCaught() throws Exception {
        // Arrange
        DiseaseOccurrenceHandler diseaseOccurrenceHandler = mock(DiseaseOccurrenceHandler.class);
        HandlersAsyncWrapper wrapper = new HandlersAsyncWrapper(diseaseOccurrenceHandler);

        ModelRun modelRun = new ModelRun();

        doThrow(new RuntimeException("Test message")).when(diseaseOccurrenceHandler).handle(modelRun);

        // Act
        wrapper.handle(modelRun).get();

        // Assert
        verify(diseaseOccurrenceHandler, never()).handle(same(modelRun));
    }
}
