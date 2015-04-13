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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseProcessGatekeeper.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseProcessGatekeeperTest {
    private DiseaseService diseaseService;
    private DiseaseProcessGatekeeper diseaseProcessGatekeeper;
    private DiseaseGroup diseaseGroup;

    private static final int DISEASE_GROUP_ID = 87;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        ModelRunService modelRunService = new ModelRunServiceImpl(mock(ModelRunDao.class), 7);
        diseaseProcessGatekeeper = new DiseaseProcessGatekeeper(diseaseService, modelRunService);

        // The default disease group for dengue has automatic model runs set to true
        diseaseGroup = new DiseaseGroup(DISEASE_GROUP_ID);
        diseaseGroup.setName("Dengue");
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());

        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void modelShouldRunWhenLastModelRunPrepDateIsNullAndNewLocationsIsOverThreshold() throws Exception {
        expectModelShouldRun(null, true);
    }

    @Test
    public void modelShouldRunWhenLastModelRunPrepDateIsNullAndNewLocationsIsUnderThreshold() throws Exception {
        expectModelShouldRun(null, false);
    }

    @Test
    public void modelShouldRunWhenLastModelRunPrepDateIsNullAndThresholdIsNull() throws Exception {
        expectModelShouldRun(null, null);
    }

    @Test
    public void modelShouldRunWhenEnoughDaysHaveElapsedAndNewLocationsIsOverThreshold() throws Exception {
        expectModelShouldRun(true, true);
    }

    @Test
    public void modelShouldRunWhenEnoughDaysHaveElapsedAndNewLocationsIsUnderThreshold() throws Exception {
        expectModelShouldRun(true, false);
    }

    @Test
    public void modelShouldRunWhenEnoughDaysHaveElapsedAndThresholdIsNull() throws Exception {
        expectModelShouldRun(true, null);
    }

    @Test
    public void modelShouldRunWhenEnoughDaysHaveNotElapsedAndNewLocationsIsOverThreshold() throws Exception {
        validationParametersThresholdsDefined();
        expectModelShouldRun(false, true);
    }

    @Test
    public void modelShouldNotRunWhenEnoughDaysHaveNotPassedAndNewLocationsIsUnderThreshold() throws Exception {
        validationParametersThresholdsDefined();
        expectModelShouldNotToRun(false, false);
    }

    @Test
    public void modelShouldNotRunWhenEnoughDaysHaveNotPassedAndLocationsThresholdIsNull() throws Exception {
        validationParametersThresholdsDefined();
        expectModelShouldNotToRun(false, null);
    }

    private void validationParametersThresholdsDefined() {
        diseaseGroup.setMinDistanceFromDiseaseExtent(0.0);
        diseaseGroup.setMinEnvironmentalSuitability(0.0);
    }

    @Test
    public void modelShouldNotRunWhenEnoughDaysHaveNotElapsedAndMinEnvSuitabilityOrMinDistanceFromExtentNotDefined() throws Exception {
        expectModelShouldNotToRun(false, true);
    }

    private void expectModelShouldRun(Boolean weekHasElapsed, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        mockGetDiseaseGroupById();
        boolean result = arrangeAndAct(weekHasElapsed, newLocationCountOverThreshold);

        // Assert
        assertThat(result).isTrue();
    }

    private void expectModelShouldNotToRun(Boolean weekHasElapsed, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        mockGetDiseaseGroupById();
        boolean result = arrangeAndAct(weekHasElapsed, newLocationCountOverThreshold);

        // Assert
        assertThat(result).isFalse();
    }

    private boolean arrangeAndAct(Boolean weekHasElapsed, Boolean newLocationCountOverThreshold) {
        // Arrange
        long newLocationsCount = 10;
        setLastModelRunPrepDate(weekHasElapsed);
        when(diseaseService.getDistinctLocationsCountForTriggeringModelRun(
                eq(DISEASE_GROUP_ID), any(DateTime.class), any(DateTime.class))).thenReturn(newLocationsCount);
        setMinNewLocations(newLocationsCount, newLocationCountOverThreshold);

        // Act
        return diseaseProcessGatekeeper.modelShouldRun(DISEASE_GROUP_ID);
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
