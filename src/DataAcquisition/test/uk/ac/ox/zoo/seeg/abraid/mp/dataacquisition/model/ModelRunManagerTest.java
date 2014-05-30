package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings.WeightingsCalculator;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the ModelRunManager.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ExpertService expertService;

    private static final int DISEASE_GROUP_ID = 87;

    @Before
    public void setFixedTime() {
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
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID);
        arrangeAndAct(diseaseGroup, weekHasElapsed, newOccurrenceCountOverThreshold);

        // Assert
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isNotNull();
    }

    private void expectModelPrepNotToRun(Boolean weekHasElapsed, Boolean newOccurrenceCountOverThreshold) {
        // Arrange and Act
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID);
        DateTime lastModelRunPrepDate = arrangeAndAct(diseaseGroup, weekHasElapsed, newOccurrenceCountOverThreshold);

        // Assert
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isEqualTo(lastModelRunPrepDate);
    }

    private DateTime arrangeAndAct(DiseaseGroup diseaseGroup, Boolean weekHasElapsed,
                                   Boolean newOccurrenceCountOverThreshold) {
        // Arrange
        DateTime lastModelRunPrepDate = setLastModelRunPrepDate(diseaseGroup, weekHasElapsed);
        setModelRunMinNewOccurrences(diseaseGroup, newOccurrenceCountOverThreshold);
        ModelRunManager target = createModelRunGateKeeper();

        // Act
        target.prepareModelRun(diseaseGroup.getId());

        return lastModelRunPrepDate;
    }

    private DateTime setLastModelRunPrepDate(DiseaseGroup diseaseGroup, Boolean weekHasElapsed) {
        DateTime lastModelRunPrepDate;
        if (weekHasElapsed == null) {
            lastModelRunPrepDate = null;
        } else {
            int days = weekHasElapsed ? 7 : 1;
            lastModelRunPrepDate = DateTime.now().minusDays(days);
        }
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        return lastModelRunPrepDate;
    }

    private void setModelRunMinNewOccurrences(DiseaseGroup diseaseGroup, Boolean newOccurrenceCountOverThreshold) {
        Integer n;
        if (newOccurrenceCountOverThreshold == null) {
            n = null;
        } else {
            int thresholdAdjustment = newOccurrenceCountOverThreshold ? -1 : +1;
            n = (int) diseaseService.getNewOccurrencesCountByDiseaseGroup(diseaseGroup.getId()) + thresholdAdjustment;
        }
        diseaseGroup.setModelRunMinNewOccurrences(n);
    }

    private ModelRunManager createModelRunGateKeeper() {
        return new ModelRunManager(
                new ModelRunGatekeeper(diseaseService),
                new LastModelRunPrepDateManager(diseaseService),
                new DiseaseExtentGenerator(diseaseService),
                new WeightingsCalculator(diseaseService, expertService),
                mock(ModelRunRequester.class));
    }
}
