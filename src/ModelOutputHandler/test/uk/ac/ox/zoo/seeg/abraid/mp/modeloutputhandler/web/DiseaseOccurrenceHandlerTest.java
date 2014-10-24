package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseOccurrenceHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandlerTest {
    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;
    private DiseaseService diseaseService;
    private ModelRunService modelRunService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        modelRunService = mock(ModelRunService.class);
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        diseaseOccurrenceHandler = new DiseaseOccurrenceHandler(diseaseService, modelRunService,
                diseaseOccurrenceValidationService);
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void handleValidationParametersHasNoEffectIfModelIncomplete() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.FAILED);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesByDiseaseGroupId(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesForBatching(anyInt(), any(DateTime.class), any(DateTime.class));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(anyListOf(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(modelRunService, never()).saveModelRun(any(ModelRun.class));
    }

    @Test
    public void handleValidationParametersHasNoEffectIfDiseaseGroupIsNotBeingSetUp() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesByDiseaseGroupId(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesForBatching(anyInt(), any(DateTime.class), any(DateTime.class));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(anyListOf(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(modelRunService, never()).saveModelRun(any(ModelRun.class));
    }

    @Test
    public void handleValidationParametersInitialisesBatchingIfBatchingHasNeverCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchStartDate(DateTime.now().minusDays(1));
        modelRun.setBatchEndDate(DateTime.now());
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        DiseaseOccurrence occurrence1 = new DiseaseOccurrence();
        occurrence1.setStatus(DiseaseOccurrenceStatus.READY);
        occurrence1.setFinalWeighting(0.5);

        DiseaseOccurrence occurrence2 = new DiseaseOccurrence();
        occurrence2.setStatus(DiseaseOccurrenceStatus.READY);
        occurrence2.setFinalWeightingExcludingSpatial(0.7);

        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2);

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(false);
        when(diseaseService.getDiseaseOccurrencesByDiseaseGroupIdAndStatus(diseaseGroupId,
                DiseaseOccurrenceStatus.READY)).thenReturn(occurrences);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence2));
        assertThat(occurrence1.getFinalWeighting()).isNull();
        assertThat(occurrence2.getFinalWeightingExcludingSpatial()).isNull();
    }

    @Test
    public void handleValidationParametersDoesNotInitialiseBatchingIfBatchingHasCompleted() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchStartDate(DateTime.now().minusDays(1));
        modelRun.setBatchEndDate(DateTime.now());
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(true);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(modelRunService).hasBatchingEverCompleted(eq(diseaseGroupId));
        verify(diseaseService, never()).getDiseaseOccurrencesByDiseaseGroupId(anyInt());
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
    }

    @Test
    public void handleValidationParametersDoesNotBatchIfThereIsNoBatchEndDate() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(modelRunService, never()).hasBatchingEverCompleted(anyInt());
        verify(diseaseService, never()).getDiseaseOccurrencesForBatching(anyInt(), any(DateTime.class), any(DateTime.class));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(anyListOf(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(modelRunService, never()).saveModelRun(any(ModelRun.class));
    }

    @Test
    public void handlingSucceedsIfThereAreNoOccurrencesToBatch() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = new DateTime("2012-11-13T15:16:17");
        DateTime batchStartDateWithMinimumTime = new DateTime("2012-11-13T00:00:00.000");
        DateTime batchEndDate = new DateTime("2012-11-14T15:16:17");
        DateTime batchEndDateWithMaximumTime = new DateTime("2012-11-14T23:59:59.999");
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setName("Dengue");

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDateWithMinimumTime, batchEndDateWithMaximumTime))
                .thenReturn(new ArrayList<DiseaseOccurrence>());

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseService).getDiseaseOccurrencesForBatching(
                eq(diseaseGroupId), eq(batchStartDateWithMinimumTime), eq(batchEndDateWithMaximumTime));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(anyListOf(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(modelRunService).saveModelRun(modelRun);
    }

    @Test
    public void handlingSucceedsIfThereAreOccurrencesToBatch() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = new DateTime("2013-07-29T14:15:16");
        DateTime batchStartDateWithMinimumTime = new DateTime("2013-07-29T00:00:00.000");
        DateTime batchEndDate = new DateTime("2013-07-30T14:15:16");
        DateTime batchEndDateWithMaximumTime = new DateTime("2013-07-30T23:59:59.999");
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setName("Dengue");

        DiseaseOccurrence occurrence1 = new DiseaseOccurrence();
        DiseaseOccurrence occurrence2 = new DiseaseOccurrence();
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2);

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDateWithMinimumTime, batchEndDateWithMaximumTime))
                .thenReturn(occurrences);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseService).getDiseaseOccurrencesForBatching(
                eq(diseaseGroupId), eq(batchStartDateWithMinimumTime), eq(batchEndDateWithMaximumTime));
        verify(diseaseOccurrenceValidationService).addValidationParameters(same(occurrences));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence2));
        verify(modelRunService).saveModelRun(modelRun);
    }

    private ModelRun createModelRun(int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(1);
        modelRun.setName("test");
        modelRun.setDiseaseGroupId(diseaseGroupId);
        modelRun.setRequestDate(DateTime.now());
        modelRun.setStatus(status);
        return modelRun;
    }
}
