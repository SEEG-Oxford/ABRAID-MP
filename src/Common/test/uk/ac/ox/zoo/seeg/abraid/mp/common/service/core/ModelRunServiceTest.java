package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the ModelRunService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunServiceTest extends AbstractCommonSpringUnitTests {
    @Autowired
    private ModelRunService modelRunService;

    @Test
    public void getModelRunByName() {
        // Arrange
        String name = "test";
        ModelRun expectedRun = new ModelRun();
        when(modelRunDao.getByName(name)).thenReturn(expectedRun);

        // Act
        ModelRun actualRun = modelRunService.getModelRunByName(name);

        // Assert
        assertThat(actualRun).isSameAs(expectedRun);
    }

    @Test
    public void getMeanPredictionRasterForModelRun() {
        // Arrange
        int modelRunId = 1;
        byte[] expectedRaster = new byte[1];

        when(nativeSQL.getRasterForModelRun(modelRunId, NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME))
                .thenReturn(expectedRaster);

        // Act
        byte[] actualRaster = modelRunService.getMeanPredictionRasterForModelRun(modelRunId);

        // Assert
        assertThat(actualRaster).isSameAs(expectedRaster);
    }

    @Test
    public void updateMeanPredictionRasterForModelRun() {
        // Arrange
        int modelRunId = 1;
        byte[] raster = new byte[1];

        // Act
        modelRunService.updateMeanPredictionRasterForModelRun(modelRunId, raster);

        // Assert
        verify(nativeSQL).updateRasterForModelRun(eq(modelRunId), eq(raster),
                eq(NativeSQLConstants.MEAN_PREDICTION_RASTER_COLUMN_NAME));
    }

    @Test
    public void updatePredictionUncertaintyRasterForModelRun() {
        // Arrange
        int modelRunId = 1;
        byte[] raster = new byte[1];

        // Act
        modelRunService.updatePredictionUncertaintyRasterForModelRun(modelRunId, raster);

        // Assert
        verify(nativeSQL).updateRasterForModelRun(eq(modelRunId), eq(raster),
                eq(NativeSQLConstants.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME));
    }

    @Test
    public void saveModelRun() {
        // Arrange
        ModelRun run = new ModelRun();

        // Act
        modelRunService.saveModelRun(run);

        // Assert
        verify(modelRunDao).save(eq(run));
    }

    @Test
    public void getLastRequestedModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun expectedModelRun = new ModelRun();
        when(modelRunDao.getLastRequestedModelRun(diseaseGroupId)).thenReturn(expectedModelRun);

        // Act
        ModelRun actualModelRun = modelRunService.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(actualModelRun).isEqualTo(expectedModelRun);
    }

    @Test
    public void getLastCompletedModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun expectedModelRun = new ModelRun();
        when(modelRunDao.getLastCompletedModelRun(diseaseGroupId)).thenReturn(expectedModelRun);

        // Act
        ModelRun actualModelRun = modelRunService.getLastCompletedModelRun(diseaseGroupId);

        // Assert
        assertThat(actualModelRun).isEqualTo(expectedModelRun);
    }

    @Test
    public void getCompletedModelRuns() {
        // Arrange
        Collection<ModelRun> expectedRuns = Arrays.asList(mock(ModelRun.class), mock(ModelRun.class));
        when(modelRunDao.getCompletedModelRuns()).thenReturn(expectedRuns);

        // Act
        Collection<ModelRun> runs = modelRunService.getCompletedModelRuns();

        // Assert
        assertThat(runs).isEqualTo(expectedRuns);
    }

    @Test
    public void hasBatchingEverCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        when(modelRunDao.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(true);

        // Act
        boolean result = modelRunService.hasBatchingEverCompleted(diseaseGroupId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void subtractDaysBetweenModelRuns() {
        // Arrange
        DateTime inputDateTime = new DateTime("2014-10-09T12:13:14");
        DateTime expectedResult = new DateTime("2014-10-02T00:00:00");

        // Act
        DateTime actualResult = modelRunService.subtractDaysBetweenModelRuns(inputDateTime);

        // Assert
        assertThat(actualResult.getMillis()).isEqualTo(expectedResult.getMillis());
    }
}
