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
public class BatchingHandlerHelperTest {
    private BatchingHandlerHelper helper;
    private DiseaseService diseaseService;
    private ModelRunService modelRunService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        modelRunService = mock(ModelRunService.class);
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        helper = new BatchingHandlerHelper(diseaseService, modelRunService,
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
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isNull();
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
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isNull();
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
        when(diseaseService.getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId)).thenReturn(occurrences);

        // Act
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isEqualTo(DateTime.now());
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
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isNull();
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
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isNull();
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
        DateTime batchStartDate = new DateTime("2012-11-13T00:00:00.000");
        DateTime batchEndDate = new DateTime("2012-11-14T23:59:59.999");
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setName("Dengue");

        when(modelRunService.getModelRunByName(modelRun.getName())).thenReturn(modelRun);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(false);
        when(diseaseService.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDate, batchEndDate))
                .thenReturn(new ArrayList<DiseaseOccurrence>());

        // Act
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isEqualTo(DateTime.now());
        verify(diseaseService).getDiseaseOccurrencesForBatching(
                eq(diseaseGroupId), eq(batchStartDate), eq(batchEndDate));
        verify(diseaseOccurrenceValidationService, never()).addValidationParameters(anyListOf(DiseaseOccurrence.class));
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
        verify(modelRunService).saveModelRun(modelRun);
    }

    @Test
    public void handlingSucceedsIfThereAreOccurrencesToBatch() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchStartDate = new DateTime("2013-07-29T00:00:00.000");
        DateTime batchEndDate = new DateTime("2013-07-30T23:59:59.999");
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
        when(modelRunService.hasBatchingEverCompleted(diseaseGroupId)).thenReturn(false);
        when(diseaseService.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDate, batchEndDate))
                .thenReturn(occurrences);

        // Act
        DateTime batchingInitialisationDate = helper.handle(modelRun);

        // Assert
        assertThat(batchingInitialisationDate).isEqualTo(DateTime.now());
        verify(diseaseService).getDiseaseOccurrencesForBatching(
                eq(diseaseGroupId), eq(batchStartDate), eq(batchEndDate));
        verify(diseaseOccurrenceValidationService).addValidationParameters(same(occurrences));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence2));
        verify(modelRunService).saveModelRun(modelRun);
    }

    @Test
    public void continueBatchingInitialisationDoesNothingIfBatchingInitialisationDateIsNull() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        helper.continueBatchingInitialisation(diseaseGroupId, null);

        // Assert
        verify(diseaseService, never()).getDiseaseOccurrencesByDiseaseGroupIdAndStatuses(anyInt(),
                any(DiseaseOccurrenceStatus.class));
        verify(diseaseService, never()).saveDiseaseOccurrence(any(DiseaseOccurrence.class));
    }

    @Test
    public void continueBatchingInitialisationSavesOccurrencesAfterInitialisationDate() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseOccurrence occurrence1 = createDiseaseOccurrence(-1);
        DiseaseOccurrence occurrence2 = createDiseaseOccurrence(0);
        DiseaseOccurrence occurrence3 = createDiseaseOccurrence(1);
        List<DiseaseOccurrence> occurrences = Arrays.asList(occurrence1, occurrence2, occurrence3);

        // Act
        when(diseaseService.getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId)).thenReturn(occurrences);
        helper.continueBatchingInitialisation(diseaseGroupId, DateTime.now());

        // Assert
        assertThat(occurrence1.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
        assertThat(occurrence2.getStatus()).isEqualTo(DiseaseOccurrenceStatus.AWAITING_BATCHING);
        assertThat(occurrence3.getStatus()).isEqualTo(DiseaseOccurrenceStatus.AWAITING_BATCHING);

        verify(diseaseService, never()).saveDiseaseOccurrence(same(occurrence1));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence2));
        verify(diseaseService).saveDiseaseOccurrence(same(occurrence3));
    }

    private ModelRun createModelRun(int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(1);
        modelRun.setName("test");
        modelRun.setDiseaseGroupId(diseaseGroupId);
        modelRun.setRequestDate(DateTime.now());
        modelRun.setStatus(status);
        return modelRun;
    }

    private DiseaseOccurrence createDiseaseOccurrence(int daysOffset) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence(DateTime.now().plusDays(daysOffset));
        occurrence.setStatus(DiseaseOccurrenceStatus.READY);
        return occurrence;
    }
}
