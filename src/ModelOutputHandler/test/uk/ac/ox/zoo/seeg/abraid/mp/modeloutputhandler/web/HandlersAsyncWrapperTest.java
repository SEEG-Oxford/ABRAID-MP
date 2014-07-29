package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.io.File;

import static org.mockito.Mockito.*;
/**
 * Tests the HandlersAsyncWrapper class.
 * Copyright (c) 2014 University of Oxford
 */
public class HandlersAsyncWrapperTest {
    @Test
    public void handlersAreCalledSuccessfully() throws Exception {
        // Arrange
        DiseaseExtentGenerationHandler diseaseExtentGenerationHandler = mock(DiseaseExtentGenerationHandler.class);
        DiseaseOccurrenceHandler diseaseOccurrenceHandler = mock(DiseaseOccurrenceHandler.class);
        HandlersAsyncWrapper wrapper = new HandlersAsyncWrapper(diseaseExtentGenerationHandler,
                diseaseOccurrenceHandler);

        ModelRun modelRun = new ModelRun();

        // Act
        wrapper.handle(modelRun).get();

        // Assert
        verify(diseaseExtentGenerationHandler, times(1)).handle(same(modelRun));
        verify(diseaseOccurrenceHandler, times(1)).handle(same(modelRun));
    }

    @Test
    public void exceptionThrownByAHandlerIsCaught() throws Exception {
        // Arrange
        DiseaseExtentGenerationHandler diseaseExtentGenerationHandler = mock(DiseaseExtentGenerationHandler.class);
        DiseaseOccurrenceHandler diseaseOccurrenceHandler = mock(DiseaseOccurrenceHandler.class);
        HandlersAsyncWrapper wrapper = new HandlersAsyncWrapper(diseaseExtentGenerationHandler,
                diseaseOccurrenceHandler);

        ModelRun modelRun = new ModelRun();

        doThrow(new RuntimeException("Test message")).when(diseaseExtentGenerationHandler).handle(modelRun);

        // Act
        wrapper.handle(modelRun).get();

        // Assert
        verify(diseaseExtentGenerationHandler, times(1)).handle(same(modelRun));
        verify(diseaseOccurrenceHandler, never()).handle(same(modelRun));
    }
}
