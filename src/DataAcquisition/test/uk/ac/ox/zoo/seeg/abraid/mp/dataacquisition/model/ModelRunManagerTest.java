package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import static org.mockito.Mockito.*;

/**
 * Tests the ModelRunManager.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerTest {
    private DiseaseService diseaseService;
    private ModelRunManager modelRunManager;
    private ModelRunWorkflowService modelRunWorkflowService;

    private static final int DISEASE_GROUP_ID = 87;

    @Before
    public void setFixedTime() {
        diseaseService = mock(DiseaseService.class);
        ModelRunGatekeeper modelRunGatekeeper = new ModelRunGatekeeper(diseaseService);
        modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        modelRunManager = new ModelRunManager(modelRunGatekeeper, modelRunWorkflowService, diseaseService);

        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void modelPrepShouldRunWhenLastModelRunPrepDateIsNullAndNewOccurrencesIsOverThreshold() throws Exception {
        expectModelPrepToRun(null, true);
    }

    @Test
    public void modelPrepShouldRunWhenLastModelRunPrepDateIsNullAndNewOccurrencesIsUnderThreshold() throws Exception {
        expectModelPrepToRun(null, false);
    }

    @Test
    public void modelPrepShouldRunWhenAWeekHasElapsedAndNewOccurrencesIsOverThreshold() throws Exception {
        expectModelPrepToRun(true, true);
    }

    @Test
    public void modelPrepShouldRunWhenAWeekHasElapsedAndNewOccurrencesIsUnderThreshold() throws Exception {
        expectModelPrepToRun(true, false);
    }

    @Test
    public void modelPrepShouldRunWhenAWeekHasNotElapsedAndNewOccurrencesIsOverThreshold() throws Exception {
        expectModelPrepToRun(false, true);
    }

    @Test
    public void modelPrepShouldNotRunWhenAWeekHasNotPassedAndNewOccurrencesIsUnderThreshold() throws Exception {
        expectModelPrepNotToRun(false, false);
    }

    @Test
    public void modelPrepShouldNotRunWhenAWeekHasNotPassedAndThresholdIsNull() throws Exception {
        expectModelPrepNotToRun(false, null);
    }

    @Test
    public void modelPrepShouldNotRunWhenAWeekHasPassedAndThresholdIsNull() throws Exception {
        expectModelPrepNotToRun(true, null);
    }

    @Test
    public void modelPrepShouldNotRunWhenLastModelRunPrepDateIsNullAndThresholdIsNull() throws Exception {
        expectModelPrepNotToRun(null, null);
    }

    private void expectModelPrepToRun(Boolean weekHasElapsed, Boolean newOccurrenceCountOverThreshold) {
        // Arrange and Act
        DiseaseGroup diseaseGroup = mockGetDiseaseGroupById();
        arrangeAndAct(diseaseGroup, weekHasElapsed, newOccurrenceCountOverThreshold);

        // Assert
        verify(modelRunWorkflowService, times(1)).prepareForAndRequestModelRun(eq(DISEASE_GROUP_ID));
    }

    private void expectModelPrepNotToRun(Boolean weekHasElapsed, Boolean newOccurrenceCountOverThreshold) {
        // Arrange and Act
        DiseaseGroup diseaseGroup = mockGetDiseaseGroupById();
        arrangeAndAct(diseaseGroup, weekHasElapsed, newOccurrenceCountOverThreshold);

        // Assert
        verify(modelRunWorkflowService, never()).prepareForAndRequestModelRun(eq(DISEASE_GROUP_ID));
    }

    private void arrangeAndAct(DiseaseGroup diseaseGroup, Boolean weekHasElapsed,
                                   Boolean newOccurrenceCountOverThreshold) {
        // Arrange
        int newOccurrencesCount = 100;
        setLastModelRunPrepDate(diseaseGroup, weekHasElapsed);
        mockGetNewOccurrencesCountByDiseaseGroup(newOccurrencesCount);
        setModelRunMinNewOccurrences(diseaseGroup, newOccurrencesCount, newOccurrenceCountOverThreshold);

        // Act
        modelRunManager.prepareForAndRequestModelRun(diseaseGroup.getId());
    }

    private void setLastModelRunPrepDate(DiseaseGroup diseaseGroup, Boolean weekHasElapsed) {
        DateTime lastModelRunPrepDate = null;
        if (weekHasElapsed != null) {
            int days = weekHasElapsed ? 7 : 1;
            lastModelRunPrepDate = DateTime.now().minusDays(days);
        }
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
    }

    private void setModelRunMinNewOccurrences(DiseaseGroup diseaseGroup, int newOccurrencesCount,
                                              Boolean newOccurrenceCountOverThreshold) {
        Integer minNewOccurrences = null;
        if (newOccurrenceCountOverThreshold != null) {
            int thresholdAdjustment = newOccurrenceCountOverThreshold ? -1 : +1;
            minNewOccurrences = newOccurrencesCount + thresholdAdjustment;
        }
        diseaseGroup.setModelRunMinNewOccurrences(minNewOccurrences);
    }

    private DiseaseGroup mockGetDiseaseGroupById() {
        DiseaseGroup diseaseGroup = new DiseaseGroup(DISEASE_GROUP_ID);
        when(diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID)).thenReturn(diseaseGroup);
        return diseaseGroup;
    }

    private void mockGetNewOccurrencesCountByDiseaseGroup(long newOccurrencesCount) {
        when(diseaseService.getNewOccurrencesCountByDiseaseGroup(DISEASE_GROUP_ID)).thenReturn(newOccurrencesCount);
    }
}
