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
    private DiseaseGroup diseaseGroup;

    private static final int DISEASE_GROUP_ID = 87;

    @Before
    public void setFixedTime() {
        diseaseService = mock(DiseaseService.class);
        ModelRunGatekeeper modelRunGatekeeper = new ModelRunGatekeeper(diseaseService);
        modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        modelRunManager = new ModelRunManager(modelRunGatekeeper, modelRunWorkflowService);

        // The default disease group for dengue has automatic model runs set to true
        diseaseGroup = new DiseaseGroup(DISEASE_GROUP_ID);
        diseaseGroup.setAutomaticModelRuns(true);

        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void modelPrepShouldNotRunWhenAutomaticModelRunsNotEnabled() throws Exception {
        diseaseGroup.setAutomaticModelRuns(false);
        expectModelPrepNotToRun(null, true);
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
        mockGetDiseaseGroupById();
        arrangeAndAct(weekHasElapsed, newOccurrenceCountOverThreshold);

        // Assert
        verify(modelRunWorkflowService, times(1)).prepareForAndRequestAutomaticModelRun(eq(DISEASE_GROUP_ID));
    }

    private void expectModelPrepNotToRun(Boolean weekHasElapsed, Boolean newOccurrenceCountOverThreshold) {
        // Arrange and Act
        mockGetDiseaseGroupById();
        arrangeAndAct(weekHasElapsed, newOccurrenceCountOverThreshold);

        // Assert
        verify(modelRunWorkflowService, never()).prepareForAndRequestAutomaticModelRun(eq(DISEASE_GROUP_ID));
    }

    private void arrangeAndAct(Boolean weekHasElapsed, Boolean newOccurrenceCountOverThreshold) {
        // Arrange
        int newOccurrencesCount = 100;
        setLastModelRunPrepDate(weekHasElapsed);
        mockGetNewOccurrencesCountByDiseaseGroup(newOccurrencesCount);
        setModelRunMinNewOccurrences(newOccurrencesCount, newOccurrenceCountOverThreshold);

        // Act
        modelRunManager.prepareForAndRequestModelRun(diseaseGroup.getId());
    }

    private void setLastModelRunPrepDate(Boolean weekHasElapsed) {
        DateTime lastModelRunPrepDate = null;
        if (weekHasElapsed != null) {
            int days = weekHasElapsed ? 7 : 1;
            lastModelRunPrepDate = DateTime.now().minusDays(days);
        }
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
    }

    private void setModelRunMinNewOccurrences(int newOccurrencesCount, Boolean newOccurrenceCountOverThreshold) {
        Integer minNewOccurrences = null;
        if (newOccurrenceCountOverThreshold != null) {
            int thresholdAdjustment = newOccurrenceCountOverThreshold ? -1 : +1;
            minNewOccurrences = newOccurrencesCount + thresholdAdjustment;
        }
        diseaseGroup.setMinNewOccurrencesTrigger(minNewOccurrences);
    }

    private void mockGetDiseaseGroupById() {
        when(diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID)).thenReturn(diseaseGroup);
    }

    private void mockGetNewOccurrencesCountByDiseaseGroup(long newOccurrencesCount) {
        when(diseaseService.getNewOccurrencesCountByDiseaseGroup(DISEASE_GROUP_ID)).thenReturn(newOccurrencesCount);
    }
}
