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
 * Integration tests for the ModelRunGatekeeper class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGatekeeperIntegrationTest extends AbstractDataManagerSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private ModelRunGatekeeper modelRunGatekeeper;

    private static final int DISEASE_GROUP_ID = 87;
    private static DiseaseGroup diseaseGroup;
    private static DateTime weekHasElapsed;
    private static DateTime weekHasNotElapsed;
    private static boolean enoughLocations = true;
    private static boolean notEnoughLocations = false;
    private static boolean thresholdNotDefined = false;

    // NB. In all the following tests, it is assumed that automatic model runs are enabled. The method shouldModelRun
    // is only ever called on disease groups where this is the case, thanks to modelRunManager.getDiseaseGroupIdsForAutomaticModelRuns in Main

    @Before
    public void setUp() {
        diseaseGroup = diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID);

        // Set fixed time
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
        weekHasElapsed = DateTime.now().minusDays(7);
        weekHasNotElapsed = DateTime.now().minusDays(1);
    }

    // Null lastModelRunPrepDate means the model has never run, so should run now.
    @Test
    public void shouldRunReturnsTrueForNullLastModelRunPrepDateWithEnoughLocations() {
        executeTest(null, enoughLocations, true);
    }

    @Test
    public void shouldRunReturnsTrueForNullLastModelRunPrepDateWithNotEnoughLocations() {
        executeTest(null, notEnoughLocations, true);
    }

    @Test
    public void shouldRunReturnsTrueForNullLastModelRunPrepDateWhenMinNewLocationsTriggerNotDefined() {
        minNewLocationsTriggerNotDefined();
        executeTest(null, thresholdNotDefined, true);
    }

    @Test
    public void shouldRunReturnsTrueForNullLastModelRunPrepDateWhenMinEnvSuitabilityNotDefined() {
        minEnvSuitabilityNotDefined();
        executeTest(null, thresholdNotDefined, true);
    }

    @Test
    public void shouldRunReturnsTrueForNullLastModelRunPrepDateWhenMinDistanceFromExtentNotDefined() {
        minDistanceFromExtentNotDefined();
        executeTest(null, thresholdNotDefined, true);
    }

    @Test
    public void shouldRunReturnsTrueWhenAWeekHasElapsedWithEnoughLocations() {
        executeTest(weekHasElapsed, enoughLocations, true);
    }

    @Test
    public void shouldRunReturnsTrueWhenAWeekHasElapsedButNotEnoughLocations() {
        executeTest(weekHasElapsed, notEnoughLocations, true);
    }

    @Test
    public void shouldRunReturnsTrueWhenAWeekHasElapsedWhenMinNewLocationsTriggerNotDefined() {
        minNewLocationsTriggerNotDefined();
        executeTest(weekHasElapsed, thresholdNotDefined, true);
    }

    @Test
    public void shouldRunReturnsTrueWhenAWeekHasElapsedWhenMinEnvSuitabilityNotDefined() {
        minEnvSuitabilityNotDefined();
        executeTest(weekHasElapsed, thresholdNotDefined, true);
    }

    @Test
    public void shouldRunReturnsTrueWhenAWeekHasElapsedWhenMinDistanceFromExtentNotDefined() {
        minDistanceFromExtentNotDefined();
        executeTest(weekHasElapsed, thresholdNotDefined, true);
    }

    @Test
    public void shouldRunReturnsTrueWhenAWeekHasNotElapsedButEnoughLocations() {
        executeTest(weekHasNotElapsed, enoughLocations, true);
    }

    @Test
    public void shouldRunReturnsFalseWhenAWeekHasNotElapsedAndNotEnoughLocations() {
        executeTest(weekHasNotElapsed, notEnoughLocations, false);
    }

    @Test
    public void shouldRunReturnsFalseWhenAWeekHasNotElapsedAndMinNewLocationsTriggerNotDefined() {
        minNewLocationsTriggerNotDefined();
        executeTest(weekHasNotElapsed, thresholdNotDefined, false);
    }

    @Test
    public void shouldRunReturnsFalseWhenAWeekHasNotElapsedAndMinEnvSuitabilityNotDefined() {
        minEnvSuitabilityNotDefined();
        executeTest(weekHasNotElapsed, thresholdNotDefined, false);
    }

    @Test
    public void shouldRunReturnsFalseWhenAWeekHasNotElapsedAndMinDistanceFromExtentNotDefined() {
        minDistanceFromExtentNotDefined();
        executeTest(weekHasNotElapsed, thresholdNotDefined, false);
    }

    private void executeTest(DateTime lastModelRunPrepDate, boolean hasEnoughLocations, boolean expectedResult) {
        // Arrange
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        if (hasEnoughLocations) {
            setUpEnoughLocations(Integer.MIN_VALUE);
        }

        // Act
        boolean result = modelRunGatekeeper.modelShouldRun(DISEASE_GROUP_ID);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
    }

    private void setUpEnoughLocations(int minNewLocations) {
        // Set value of minNewLocations to lower than the number of locations that there are currently, to
        // ensure that the test for whether there are enough locations will pass.
        diseaseGroup.setMinNewLocationsTrigger(minNewLocations);
        diseaseGroup.setMinEnvironmentalSuitability(0.5);
        diseaseGroup.setMinDistanceFromDiseaseExtent(10.0);
    }

    private void minNewLocationsTriggerNotDefined() {
        diseaseGroup.setMinNewLocationsTrigger(null);
    }

    private void minEnvSuitabilityNotDefined() {
        diseaseGroup.setMinEnvironmentalSuitability(null);
    }

    private void minDistanceFromExtentNotDefined() {
        diseaseGroup.setMinDistanceFromDiseaseExtent(null);
    }
}
