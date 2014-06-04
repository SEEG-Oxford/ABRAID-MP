package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ModelRunGatekeeper class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeperTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    private static final int DISEASE_GROUP_ID = 87;
    private static DateTime weekHasElapsed;
    private static DateTime weekHasNotElapsed;
    private static int enoughOccurrences;
    private static int notEnoughOccurrences;

    @Before
    public void setUp() {
        // Set fixed time
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
        weekHasElapsed = DateTime.now().minusDays(7);
        weekHasNotElapsed = DateTime.now().minusDays(1);

        // Set value of modelRunMinNewOccurrences to less than the number of occurrences that there are currently, to
        // ensure that the test for whether there are enough occurrences will pass.
        enoughOccurrences = (int) (diseaseService.getNewOccurrencesCountByDiseaseGroup(DISEASE_GROUP_ID) - 1);
        // Vice versa
        notEnoughOccurrences = (int) (diseaseService.getNewOccurrencesCountByDiseaseGroup(DISEASE_GROUP_ID) + 1);
    }

    @Test
    public void dueToRunReturnsTrueWhenAWeekHasElapsedWithEnoughOccurrences() {
        executeTest(weekHasElapsed, enoughOccurrences, true);
    }

    @Test
    public void dueToRunReturnsTrueWhenAWeekHasElapsedButNotEnoughOccurrences() {
        executeTest(weekHasElapsed, notEnoughOccurrences, true);
    }

    @Test
    public void dueToRunReturnsTrueWhenAWeekHasNotElapsedButEnoughOccurrences() {
        executeTest(weekHasNotElapsed, enoughOccurrences, true);
    }

    @Test
    public void dueToRunReturnsFalseWhenAWeekHasNotElapsedAndNotEnoughOccurrences() {
        executeTest(weekHasNotElapsed, notEnoughOccurrences, false);
    }

    @Test
    public void dueToRunReturnsTrueForNullLastModelRunPrepDateWithEnoughOccurrences() {
        executeTest(null, enoughOccurrences, true);
    }

    @Test
    public void dueToRunReturnsTrueForNullLastModelRunPrepDateWithNotEnoughOccurrences() {
        executeTest(null, notEnoughOccurrences, true);
    }

    @Test
    public void dueToRunReturnsFalseForNullModelRunMinNewOccurrencesWhenAWeekHasPassed() {
        executeTest(weekHasElapsed, null, false);
    }

    @Test
    public void dueToRunReturnsFalseForNullModelRunMinNewOccurrencesWhenAWeekHasNotPassed() {
        executeTest(weekHasNotElapsed, null, false);
    }

    @Test
    public void dueToRunReturnsFalseForNullModelRunMinNewOccurrencesWhenLastModelRunPrepDateIsNull() {
        executeTest(null, null, false);
    }

    private void executeTest(DateTime lastModelRunPrepDate, Integer modelRunMinNewOccurrences, boolean expectedResult) {
        // Arrange
        diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID).setModelRunMinNewOccurrences(modelRunMinNewOccurrences);
        ModelRunGatekeeper target = new ModelRunGatekeeper(diseaseService);

        // Act
        boolean result = target.dueToRun(lastModelRunPrepDate, DISEASE_GROUP_ID);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }
}