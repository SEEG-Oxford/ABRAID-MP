package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.AbstractDataManagerSpringIntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the ModelRunGatekeeper class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeperTest extends AbstractDataManagerSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    private static final int DISEASE_GROUP_ID = 87;
    private static DateTime weekHasElapsed;
    private static DateTime weekHasNotElapsed;
    private static int enoughLocations;
    private static int notEnoughLocations;

    @Before
    public void setUp() {
        // Set fixed time
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
        weekHasElapsed = DateTime.now().minusDays(7);
        weekHasNotElapsed = DateTime.now().minusDays(1);

        // Set value of minNewLocations to lower than the number of locations that there are currently, to
        // ensure that the test for whether there are enough locations will pass.
        enoughLocations = Integer.MIN_VALUE;
        // Vice versa
        notEnoughLocations = Integer.MAX_VALUE;
    }

    // NB. Boolean value automatic_model_runs is a non-null field, set to true for Dengue (DISEASE_GROUP_ID = 87)
    // in test data, so the following tests are working under that assumption
    @Test
    public void dueToRunReturnsTrueWhenAWeekHasElapsedWithEnoughLocations() {
        executeTest(weekHasElapsed, enoughLocations, true);
    }

    @Test
    public void dueToRunReturnsTrueWhenAWeekHasElapsedButNotEnoughLocations() {
        executeTest(weekHasElapsed, notEnoughLocations, true);
    }

    @Test
    public void dueToRunReturnsTrueWhenAWeekHasNotElapsedButEnoughLocations() {
        executeTest(weekHasNotElapsed, enoughLocations, true);
    }

    @Test
    public void dueToRunReturnsFalseWhenAWeekHasNotElapsedAndNotEnoughLocations() {
        executeTest(weekHasNotElapsed, notEnoughLocations, false);
    }

    @Test
    public void dueToRunReturnsTrueForNullLastModelRunPrepDateWithEnoughLocations() {
        executeTest(null, enoughLocations, true);
    }

    @Test
    public void dueToRunReturnsTrueForNullLastModelRunPrepDateWithNotEnoughLocations() {
        executeTest(null, notEnoughLocations, true);
    }

    @Test
    public void dueToRunReturnsFalseForNullMinNewLocationsWhenAWeekHasPassed() {
        executeTest(weekHasElapsed, null, false);
    }

    @Test
    public void dueToRunReturnsFalseForNullMinNewLocationsWhenAWeekHasNotPassed() {
        executeTest(weekHasNotElapsed, null, false);
    }

    @Test
    public void dueToRunReturnsFalseForNullMinNewLocationsWhenLastModelRunPrepDateIsNull() {
        executeTest(null, null, false);
    }

    private void executeTest(DateTime lastModelRunPrepDate, Integer minNewLocations, boolean expectedResult) {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID);
        diseaseGroup.setMinNewLocationsTrigger(minNewLocations);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        ModelRunGatekeeper target = new ModelRunGatekeeper(diseaseService);

        // Act
        boolean result = target.modelShouldRun(DISEASE_GROUP_ID);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }
}
