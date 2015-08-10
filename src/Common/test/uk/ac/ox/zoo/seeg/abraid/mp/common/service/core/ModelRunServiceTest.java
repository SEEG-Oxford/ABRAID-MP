package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.LocalDate;
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
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

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
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

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
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

        // Act
        ModelRun actualModelRun = modelRunService.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(actualModelRun).isEqualTo(expectedModelRun);
    }

    @Test
    public void getMostRecentlyRequestedModelRunWhichCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun expectedModelRun = new ModelRun();
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId)).thenReturn(expectedModelRun);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

        // Act
        ModelRun actualModelRun = modelRunService.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(actualModelRun).isEqualTo(expectedModelRun);
    }

    @Test
    public void getMostRecentlyFinishedModelRunWhichCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun expectedModelRun = new ModelRun();
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId)).thenReturn(expectedModelRun);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

        // Act
        ModelRun actualModelRun = modelRunService.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);

        // Assert
        assertThat(actualModelRun).isEqualTo(expectedModelRun);
    }

    @Test
    public void getCompletedModelRunsForDisplay() {
        // Arrange
        Collection<ModelRun> expectedRuns = Arrays.asList(mock(ModelRun.class), mock(ModelRun.class));
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getCompletedModelRunsForDisplay()).thenReturn(expectedRuns);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

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
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

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
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);
        when(modelRunDao.getModelRunRequestServersByUsage()).thenReturn(expected);

        // Act
        List<String> result = modelRunService.getModelRunRequestServersByUsage();

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getFilteredModelRuns() {
        // Arrange
        String name = "name";
        int diseaseGroupId = 87;
        LocalDate minResponseDate = new LocalDate("2015-01-01");
        LocalDate maxResponseDate = new LocalDate("2015-01-02");

        Collection<ModelRun> expectedRuns = Arrays.asList(mock(ModelRun.class), mock(ModelRun.class));
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getFilteredModelRuns(name, diseaseGroupId, minResponseDate, maxResponseDate)).thenReturn(expectedRuns);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

        // Act
        Collection<ModelRun> result = modelRunService.getFilteredModelRuns(name, diseaseGroupId, minResponseDate, maxResponseDate);

        // Assert
        assertThat(result).isEqualTo(expectedRuns);
        verify(modelRunDao).getFilteredModelRuns(name, diseaseGroupId, minResponseDate, maxResponseDate);
    }

    @Test
    public void getModelRunsForDiseaseGroup() {
        // Arrange
        Collection<ModelRun> expectedRuns = Arrays.asList(mock(ModelRun.class), mock(ModelRun.class));
        ModelRunDao modelRunDao = mock(ModelRunDao.class);
        when(modelRunDao.getModelRunsForDiseaseGroup(anyInt())).thenReturn(expectedRuns);
        ModelRunService modelRunService = new ModelRunServiceImpl(modelRunDao);

        // Act
        Collection<ModelRun> result = modelRunService.getModelRunsForDiseaseGroup(87);

        // Assert
        assertThat(result).isEqualTo(expectedRuns);
        verify(modelRunDao).getModelRunsForDiseaseGroup(87);
    }
}
