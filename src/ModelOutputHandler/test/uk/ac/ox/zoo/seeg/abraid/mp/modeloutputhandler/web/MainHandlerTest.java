package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the MainHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainHandlerTest {
    private ModelRunService modelRunService = mock(ModelRunService.class);
    private MainHandler mainHandler = new MainHandler(modelRunService);

    @Test
    public void handleValidMetadataJson() {
        // Arrange
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        String modelRunName = "test name";
        String metadataJson = String.format("{\"modelRunName\":\"%s\"}", modelRunName);
        ModelRun expectedRun = new ModelRun(modelRunName, DateTime.now().minusDays(7));
        when(modelRunService.getModelRunByName(modelRunName)).thenReturn(expectedRun);

        // Act
        ModelRun actualRun = mainHandler.handleMetadataJson(metadataJson);

        // Assert
        assertThat(actualRun).isSameAs(expectedRun);
        assertThat(actualRun.getResponseDate()).isEqualTo(DateTime.now());
        verify(modelRunService).saveModelRun(eq(expectedRun));
    }

    @Test
    public void handleMalformedMetadataJson() {
        // Arrange
        String metadataJson = "malformed JSON";

        // Act
        catchException(mainHandler).handleMetadataJson(metadataJson);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
        verify(modelRunService, never()).saveModelRun(any(ModelRun.class));
    }

    @Test
    public void handleMetadataJsonForNonExistentModelRun() {
        // Arrange
        String modelRunName = "test name";
        String metadataJson = String.format("\"modelRunName\":\"%s\"", modelRunName);
        when(modelRunService.getModelRunByName(modelRunName)).thenReturn(null);

        // Act
        catchException(mainHandler).handleMetadataJson(metadataJson);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void handleMeanPredictionRaster() {
        // Arrange
        int modelRunId = 1;
        ModelRun modelRun = new ModelRun(modelRunId);
        byte[] raster = new byte[1];

        // Act
        mainHandler.handleMeanPredictionRaster(modelRun, raster);

        // Assert
        verify(modelRunService).updateMeanPredictionRasterForModelRun(eq(modelRunId), eq(raster));
    }

    @Test
    public void handlePredictionUncertaintyRaster() {
        // Arrange
        int modelRunId = 1;
        ModelRun modelRun = new ModelRun(modelRunId);
        byte[] raster = new byte[1];

        // Act
        mainHandler.handlePredictionUncertaintyRaster(modelRun, raster);

        // Assert
        verify(modelRunService).updatePredictionUncertaintyRasterForModelRun(eq(modelRunId), eq(raster));
    }
}
