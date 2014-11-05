package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the ModelRunService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunServiceTest {
    @Test
    public void getModelRunByName() {
        // Arrange
        String name = "test";
        ModelRun expectedRun = new ModelRun();
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getByName(name)).thenReturn(expectedRun);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);

        // Act
        ModelRun actualRun = modelRunService.getModelRunByName(name);

        // Assert
        assertThat(actualRun).isSameAs(expectedRun);
    }

    @Test
    public void saveModelRun() {
        // Arrange
        ModelRun run = new ModelRun();
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);

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
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getLastRequestedModelRun(diseaseGroupId)).thenReturn(expectedModelRun);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);

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
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getLastCompletedModelRun(diseaseGroupId)).thenReturn(expectedModelRun);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);

        // Act
        ModelRun actualModelRun = modelRunService.getLastCompletedModelRun(diseaseGroupId);

        // Assert
        assertThat(actualModelRun).isEqualTo(expectedModelRun);
    }

    @Test
    public void getCompletedModelRunsForDisplay() {
        // Arrange
        Collection<ModelRun> expectedRuns = Arrays.asList(mock(ModelRun.class), mock(ModelRun.class));
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getCompletedModelRunsForDisplay()).thenReturn(expectedRuns);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);

        // Act
        Collection<ModelRun> runs = modelRunService.getCompletedModelRunsForDisplay();

        // Assert
        assertThat(runs).isEqualTo(expectedRuns);
    }

    @Test
    public void hasBatchingEverCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(true);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);

        // Act
        boolean result = modelRunService.hasBatchingEverCompleted(diseaseGroupId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void getModelRunRequestServersByUsage() {
        // Arrange
        List<String> expected = Arrays.asList("A", "B", "C");
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao, 7);
        when(modelRunDao.getModelRunRequestServersByUsage()).thenReturn(expected);

        // Act
        List<String> result = modelRunService.getModelRunRequestServersByUsage();

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void subtractDaysBetweenModelRuns() {
        // Arrange
        ModelRunService modelRunService = new ModelRunServiceImpl(mock(ModelRunDao.class), 1234);
        DateTime inputDateTime = new DateTime("2014-10-09T12:13:14");
        DateTime expectedResult = new DateTime("2011-05-24T00:00:00");

        // Act
        DateTime actualResult = modelRunService.subtractDaysBetweenModelRuns(inputDateTime);

        // Assert
        assertThat(actualResult.getMillis()).isEqualTo(expectedResult.getMillis());
    }
}
