package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseOccurrenceHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandlerTest {
    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;
    private DiseaseService diseaseService;
    private DiseaseOccurrenceHandlerHelper diseaseOccurrenceHandlerHelper;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        diseaseOccurrenceHandlerHelper = mock(DiseaseOccurrenceHandlerHelper.class);
        diseaseOccurrenceHandler = new DiseaseOccurrenceHandler(diseaseService, diseaseOccurrenceHandlerHelper);
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void handleValidationParametersHasNoEffectIfModelIncomplete() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.FAILED);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseOccurrenceHandlerHelper, never()).initialiseBatchingIfNecessary(any(ModelRun.class), any(DiseaseGroup.class));
        verify(diseaseService, never()).getDiseaseOccurrenceIDsForBatching(anyInt(), any(DateTime.class));
        verify(diseaseOccurrenceHandlerHelper, never()).setValidationParametersForOccurrencesBatch(anyListOf(Integer.class));
        verify(diseaseOccurrenceHandlerHelper, never()).setBatchingParameters(any(ModelRun.class), anyInt());
    }

    @Test
    public void handleValidationParametersHasNoEffectIfDiseaseGroupIsNotBeingSetUp() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRuns(true);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseOccurrenceHandlerHelper, never()).initialiseBatchingIfNecessary(any(ModelRun.class), any(DiseaseGroup.class));
        verify(diseaseService, never()).getDiseaseOccurrenceIDsForBatching(anyInt(), any(DateTime.class));
        verify(diseaseOccurrenceHandlerHelper, never()).setValidationParametersForOccurrencesBatch(anyListOf(Integer.class));
        verify(diseaseOccurrenceHandlerHelper, never()).setBatchingParameters(any(ModelRun.class), anyInt());
    }

    @Test
    public void handleValidationParametersDoesNotBatchIfThereIsNoBatchEndDate() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseOccurrenceHandlerHelper, times(1)).initialiseBatchingIfNecessary(any(ModelRun.class), any(DiseaseGroup.class));
        verify(diseaseService, never()).getDiseaseOccurrenceIDsForBatching(anyInt(), any(DateTime.class));
        verify(diseaseOccurrenceHandlerHelper, never()).setValidationParametersForOccurrencesBatch(anyListOf(Integer.class));
        verify(diseaseOccurrenceHandlerHelper, never()).setBatchingParameters(any(ModelRun.class), anyInt());
    }

    @Test
    public void handlingSucceedsIfThereAreNoOccurrencesToBatch() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchEndDate = DateTime.now().minusYears(1);
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchEndDate(batchEndDate);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setName("Dengue");

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseOccurrenceIDsForBatching(diseaseGroupId, batchEndDate)).thenReturn(new ArrayList<Integer>());

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseOccurrenceHandlerHelper, times(1)).initialiseBatchingIfNecessary(same(modelRun), same(diseaseGroup));
        verify(diseaseOccurrenceHandlerHelper, never()).setValidationParametersForOccurrencesBatch(anyListOf(Integer.class));
        verify(diseaseOccurrenceHandlerHelper, times(1)).setBatchingParameters(same(modelRun), eq(0));
    }

    @Test
    public void handlingSucceedsIfThereAre100OccurrencesToBatch() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchEndDate = new DateTime("2013-07-30T14:15:16");
        DateTime batchEndDateWithMaximumTime = new DateTime("2013-07-30T23:59:59.999");
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchEndDate(batchEndDate);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setName("Dengue");
        int batchSize = 100;
        List<Integer> occurrenceIDs = createRandomList(batchSize);

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseOccurrenceIDsForBatching(diseaseGroupId, batchEndDateWithMaximumTime))
                .thenReturn(occurrenceIDs);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseOccurrenceHandlerHelper, times(1)).initialiseBatchingIfNecessary(same(modelRun), same(diseaseGroup));
        verify(diseaseOccurrenceHandlerHelper, times(1)).setValidationParametersForOccurrencesBatch(eq(occurrenceIDs));
        verify(diseaseOccurrenceHandlerHelper, times(1)).setBatchingParameters(same(modelRun), eq(batchSize));
    }

    @Test
    public void handlingSucceedsIfThereAre101OccurrencesToBatch() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchEndDate = new DateTime("2013-07-30T14:15:16");
        DateTime batchEndDateWithMaximumTime = new DateTime("2013-07-30T23:59:59.999");
        ModelRun modelRun = createModelRun(diseaseGroupId, ModelRunStatus.COMPLETED);
        modelRun.setBatchEndDate(batchEndDate);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setName("Dengue");
        int batchSize = 101;
        List<Integer> occurrenceIDs = createRandomList(batchSize);
        List<Integer> occurrenceIDsTransaction1 = occurrenceIDs.subList(0, 100);
        List<Integer> occurrenceIDsTransaction2 = Arrays.asList(occurrenceIDs.get(100));

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseOccurrenceIDsForBatching(diseaseGroupId, batchEndDateWithMaximumTime))
                .thenReturn(occurrenceIDs);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        verify(diseaseOccurrenceHandlerHelper, times(1)).initialiseBatchingIfNecessary(same(modelRun), same(diseaseGroup));
        verify(diseaseOccurrenceHandlerHelper, times(1)).setValidationParametersForOccurrencesBatch(eq(occurrenceIDsTransaction1));
        verify(diseaseOccurrenceHandlerHelper, times(1)).setValidationParametersForOccurrencesBatch(eq(occurrenceIDsTransaction2));
        verify(diseaseOccurrenceHandlerHelper, times(1)).setBatchingParameters(same(modelRun), eq(batchSize));
    }

    private ModelRun createModelRun(int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(1);
        modelRun.setName("test");
        modelRun.setDiseaseGroupId(diseaseGroupId);
        modelRun.setRequestDate(DateTime.now());
        modelRun.setStatus(status);
        return modelRun;
    }

    private List<Integer> createRandomList(int size) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double randomNumber = Math.random() * 1000.0;
            list.add((int) randomNumber);
        }
        return list;
    }
}
