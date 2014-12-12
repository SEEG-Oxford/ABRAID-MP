package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        ModelRunService modelRunService = new ModelRunServiceImpl(mock(ModelRunDao.class), 7);
        ModelRunGatekeeper modelRunGatekeeper = new ModelRunGatekeeper(diseaseService, modelRunService);
        modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        modelRunManager = new ModelRunManager(modelRunGatekeeper, modelRunWorkflowService, diseaseService);

        // The default disease group for dengue has automatic model runs set to true
        diseaseGroup = new DiseaseGroup(DISEASE_GROUP_ID);
        diseaseGroup.setName("Dengue");
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());

        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void getDiseaseGroupIdsForAutomaticModelRunsReturnsCorrectIds() {
        // Arrange
        List<Integer> expectedIDs = new ArrayList<>();
        when(diseaseService.getDiseaseGroupIdsForAutomaticModelRuns()).thenReturn(expectedIDs);

        // Act
        List<Integer> actualIDs = modelRunManager.getDiseaseGroupIdsForAutomaticModelRuns();

        // Assert
        assertThat(actualIDs).isSameAs(expectedIDs);
    }

    @Test
    public void modelPrepShouldRunWhenLastModelRunPrepDateIsNullAndNewLocationsIsOverThreshold() throws Exception {
        expectModelPrepToRun(null, true);
    }

    @Test
    public void modelPrepShouldRunWhenLastModelRunPrepDateIsNullAndNewLocationsIsUnderThreshold() throws Exception {
        expectModelPrepToRun(null, false);
    }

    @Test
    public void modelPrepShouldRunWhenLastModelRunPrepDateIsNullAndThresholdIsNull() throws Exception {
        expectModelPrepToRun(null, null);
    }

    @Test
    public void modelPrepShouldRunWhenEnoughDaysHaveElapsedAndNewLocationsIsOverThreshold() throws Exception {
        expectModelPrepToRun(true, true);
    }

    @Test
    public void modelPrepShouldRunWhenEnoughDaysHaveElapsedAndNewLocationsIsUnderThreshold() throws Exception {
        expectModelPrepToRun(true, false);
    }

    @Test
    public void modelPrepShouldRunWhenEnoughDaysHaveElapsedAndThresholdIsNull() throws Exception {
        expectModelPrepToRun(true, null);
    }

    @Test
    public void modelPrepShouldRunWhenEnoughDaysHaveNotElapsedAndNewLocationsIsOverThreshold() throws Exception {
        validationParametersThresholdsDefined();
        expectModelPrepToRun(false, true);
    }

    @Test
    public void modelPrepShouldNotRunWhenEnoughDaysHaveNotPassedAndNewLocationsIsUnderThreshold() throws Exception {
        validationParametersThresholdsDefined();
        expectModelPrepNotToRun(false, false);
    }

    @Test
    public void modelPrepShouldNotRunWhenEnoughDaysHaveNotPassedAndLocationsThresholdIsNull() throws Exception {
        validationParametersThresholdsDefined();
        expectModelPrepNotToRun(false, null);
    }

    private void validationParametersThresholdsDefined() {
        diseaseGroup.setMinDistanceFromDiseaseExtent(0.0);
        diseaseGroup.setMinEnvironmentalSuitability(0.0);
    }

    @Test
    public void modelPrepShouldNotRunWhenEnoughDaysHaveNotElapsedAndMinEnvSuitabilityOrMinDistanceFromExtentNotDefined() throws Exception {
        expectModelPrepNotToRun(false, true);
    }

    private void expectModelPrepToRun(Boolean weekHasElapsed, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        mockGetDiseaseGroupById();
        arrangeAndAct(weekHasElapsed, newLocationCountOverThreshold);

        // Assert
        verify(modelRunWorkflowService).prepareForAndRequestAutomaticModelRun(eq(DISEASE_GROUP_ID));
    }

    private void expectModelPrepNotToRun(Boolean weekHasElapsed, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        mockGetDiseaseGroupById();
        arrangeAndAct(weekHasElapsed, newLocationCountOverThreshold);

        // Assert
        verify(modelRunWorkflowService, never()).prepareForAndRequestAutomaticModelRun(eq(DISEASE_GROUP_ID));
    }

    private void arrangeAndAct(Boolean weekHasElapsed, Boolean newLocationCountOverThreshold) {
        // Arrange
        long newLocationsCount = 10;
        setLastModelRunPrepDate(weekHasElapsed);
        when(diseaseService.getDistinctLocationsCountForTriggeringModelRun(
                eq(DISEASE_GROUP_ID), any(DateTime.class), any(DateTime.class))).thenReturn(newLocationsCount);
        setMinNewLocations(newLocationsCount, newLocationCountOverThreshold);

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

    private void setMinNewLocations(long newLocationsCount, Boolean newLocationCountOverThreshold) {
        Integer minNewLocations = null;
        if (newLocationCountOverThreshold != null) {
            long thresholdAdjustment = newLocationCountOverThreshold ? -1 : +1;
            minNewLocations = (int) (newLocationsCount + thresholdAdjustment);
        }
        diseaseGroup.setMinNewLocationsTrigger(minNewLocations);
    }

    private void mockGetDiseaseGroupById() {
        when(diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID)).thenReturn(diseaseGroup);
    }
}
