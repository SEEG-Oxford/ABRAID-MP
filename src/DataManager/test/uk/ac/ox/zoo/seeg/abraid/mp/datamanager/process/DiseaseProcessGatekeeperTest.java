package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        diseaseProcessGatekeeper = new DiseaseProcessGatekeeper(diseaseService);

        when(diseaseService.subtractMaxDaysOnValidator(any(DateTime.class))).thenAnswer(new Answer<LocalDate>() {
            @Override
            public LocalDate answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ((DateTime) invocationOnMock.getArguments()[0]).toLocalDate().minusDays(7);
            }
        });

        when(diseaseService.subtractDaysBetweenModelRuns(any(DateTime.class))).thenAnswer(new Answer<LocalDate>() {
            @Override
            public LocalDate answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ((DateTime) invocationOnMock.getArguments()[0]).toLocalDate().minusDays(7);
            }
        });

        // The default disease group for dengue has automatic model runs set to true
        diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(DISEASE_GROUP_ID);
        when(diseaseGroup.getName()).thenReturn("Dengue");
        when(diseaseGroup.getAutomaticModelRunsStartDate()).thenReturn(DateTime.now());
        when(diseaseGroup.getMinDistanceFromDiseaseExtentForTriggering()).thenReturn(0.0);
        when(diseaseGroup.getMaxEnvironmentalSuitabilityForTriggering()).thenReturn(0.0);
        when(diseaseService.getDiseaseGroupById(DISEASE_GROUP_ID)).thenReturn(diseaseGroup);
        DateTimeUtils.setCurrentMillisFixed(1429543707000L);
    }

    @Test
    public void modelShouldRunWhenLastModelRunPrepDateIsNull() throws Exception {
        expectModelShouldRun(null, false, true);
        expectModelShouldRun(null, false, false);
        expectModelShouldRun(null, false, null);
        expectModelShouldRun(null, true, true);
        expectModelShouldRun(null, true, false);
        expectModelShouldRun(null, true, null);
        expectModelShouldRun(null, null, true);
        expectModelShouldRun(null, null, false);
        expectModelShouldRun(null, null, null);
    }

    @Test
    public void modelShouldRunWhenEnoughDaysHaveElapsedSinceLastModelRunPrepDate() throws Exception {
        expectModelShouldRun(true, false, true);
        expectModelShouldRun(true, false, false);
        expectModelShouldRun(true, false, null);
        expectModelShouldRun(true, true, true);
        expectModelShouldRun(true, true, false);
        expectModelShouldRun(true, true, null);
        expectModelShouldRun(true, null, true);
        expectModelShouldRun(true, null, false);
        expectModelShouldRun(true, null, null);
    }


    @Test
    public void modelShouldRunWhenNewLocationsIsOverThreshold() throws Exception {
        expectModelShouldRun(true, false, true);
        expectModelShouldRun(false, false, true);
        expectModelShouldRun(null, false, true);
        expectModelShouldRun(true, true, true);
        expectModelShouldRun(false, true, true);
        expectModelShouldRun(null, true, true);
        expectModelShouldRun(true, null, true);
        expectModelShouldRun(false, null, true);
        expectModelShouldRun(null, null, true);
    }

    @Test
    public void modelShouldRunWhenExtentHasChanged() throws Exception {
        expectModelShouldRun(false, true, true);
        expectModelShouldRun(true, true, false);
        expectModelShouldRun(false, true, false);
        expectModelShouldRun(null, true, false);
        expectModelShouldRun(true, true, true);
        expectModelShouldRun(false, true, true);
        expectModelShouldRun(null, true, true);
        expectModelShouldRun(true, true, null);
        expectModelShouldRun(false, true, null);
        expectModelShouldRun(null, true, null);
    }

    @Test
    public void modelShouldNotRunWhenNoConditionsMet() throws Exception {
        expectModelShouldNotToRun(false, false, false);
        expectModelShouldNotToRun(false, null, false);
        expectModelShouldNotToRun(false, false, null);
        expectModelShouldNotToRun(false, null, null);
    }

    @Test
    public void modelShouldNotRunWhenMinDistanceFromDiseaseExtentNotDefined() throws Exception {
        when(diseaseGroup.getMinDistanceFromDiseaseExtentForTriggering()).thenReturn(null);
        expectModelShouldNotToRun(false, false, true);
    }

    @Test
    public void modelShouldNotRunWhenMaxEnvironmentalSuitabilityNotDefined() throws Exception {
        when(diseaseGroup.getMaxEnvironmentalSuitabilityForTriggering()).thenReturn(null);
        expectModelShouldNotToRun(false, false, true);
    }

    @Test
    public void extentShouldRunWhenLastExtentGenerationDateIsNull() throws Exception {
        expectExtentShouldRun(null, false, true);
        expectExtentShouldRun(null, false, false);
        expectExtentShouldRun(null, false, null);
        expectExtentShouldRun(null, true, true);
        expectExtentShouldRun(null, true, false);
        expectExtentShouldRun(null, true, null);
        expectExtentShouldRun(null, null, true);
        expectExtentShouldRun(null, null, false);
        expectExtentShouldRun(null, null, null);
    }

    @Test
    public void extentShouldRunWhenEnoughDaysHaveElapsedSinceLastExtentGenerationDate() throws Exception {
        expectExtentShouldRun(true, false, true);
        expectExtentShouldRun(true, false, false);
        expectExtentShouldRun(true, false, null);
        expectExtentShouldRun(true, true, true);
        expectExtentShouldRun(true, true, false);
        expectExtentShouldRun(true, true, null);
        expectExtentShouldRun(true, null, true);
        expectExtentShouldRun(true, null, false);
        expectExtentShouldRun(true, null, null);
    }


    @Test
    public void extentShouldRunWhenNewLocationsIsOverThreshold() throws Exception {
        expectExtentShouldRun(true, false, true);
        expectExtentShouldRun(false, false, true);
        expectExtentShouldRun(null, false, true);
        expectExtentShouldRun(true, true, true);
        expectExtentShouldRun(false, true, true);
        expectExtentShouldRun(null, true, true);
        expectExtentShouldRun(true, null, true);
        expectExtentShouldRun(false, null, true);
        expectExtentShouldRun(null, null, true);
    }

    @Test
    public void extentShouldNotRunWhenNoConditionsMet() throws Exception {
        expectExtentShouldNotToRun(false, false, false);
        expectExtentShouldNotToRun(false, null, false);
        expectExtentShouldNotToRun(false, false, null);
        expectExtentShouldNotToRun(false, null, null);
        // Extent class changes is not a condition for extent generation
        expectExtentShouldNotToRun(false, true, false);
        expectExtentShouldNotToRun(false, true, null);
    }

    @Test
    public void extentShouldNotRunWhenMinDistanceFromDiseaseExtentNotDefined() throws Exception {
        when(diseaseGroup.getMinDistanceFromDiseaseExtentForTriggering()).thenReturn(null);
        expectExtentShouldNotToRun(false, false, true);
    }

    @Test
    public void extentShouldNotRunWhenMaxEnvironmentalSuitabilityNotDefined() throws Exception {
        when(diseaseGroup.getMaxEnvironmentalSuitabilityForTriggering()).thenReturn(null);
        expectExtentShouldNotToRun(false, false, true);
    }

    private void expectModelShouldRun(Boolean weekHasElapsed, Boolean hasExtentChanged, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        boolean result = arrangeAndAct(weekHasElapsed, hasExtentChanged, newLocationCountOverThreshold);

        // Assert
        assertThat(result).isTrue();
    }

    private void expectModelShouldNotToRun(Boolean weekHasElapsed, Boolean hasExtentChanged, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        boolean result = arrangeAndAct(weekHasElapsed, hasExtentChanged, newLocationCountOverThreshold);

        // Assert
        assertThat(result).isFalse();
    }

    private void expectExtentShouldRun(Boolean weekHasElapsed, Boolean hasExtentChanged, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        boolean result = arrangeAndActExtent(weekHasElapsed, hasExtentChanged, newLocationCountOverThreshold);

        // Assert
        assertThat(result).isTrue();
    }

    private void expectExtentShouldNotToRun(Boolean weekHasElapsed, Boolean hasExtentChanged, Boolean newLocationCountOverThreshold) {
        // Arrange and Act
        boolean result = arrangeAndActExtent(weekHasElapsed, hasExtentChanged, newLocationCountOverThreshold);

        // Assert
        assertThat(result).isFalse();
    }

    private boolean arrangeAndAct(Boolean weekHasElapsed, Boolean hasExtentChanged, Boolean newLocationCountOverThreshold) {
        // Arrange
        long newLocationsCount = 10;
        setLastModelRunPrepDate(weekHasElapsed);
        setLastClassChangeDate(weekHasElapsed, hasExtentChanged);
        when(diseaseService.getDistinctLocationsCountForTriggeringModelRun(diseaseGroup, diseaseGroup.getLastModelRunPrepDate()))
                .thenReturn(newLocationsCount);
        setMinNewLocations(newLocationsCount, newLocationCountOverThreshold);

        // Act
        return diseaseProcessGatekeeper.modelShouldRun(DISEASE_GROUP_ID);
    }

    private boolean arrangeAndActExtent(Boolean weekHasElapsed, Boolean hasExtentChanged, Boolean newLocationCountOverThreshold) {
        // Arrange
        long newLocationsCount = 10;
        setLastExtentGenerationDate(weekHasElapsed);
        setLastClassChangeDate(weekHasElapsed, hasExtentChanged);
        when(diseaseService.getDistinctLocationsCountForTriggeringModelRun(diseaseGroup, diseaseGroup.getLastModelRunPrepDate()))
                .thenReturn(newLocationsCount);
        setMinNewLocations(newLocationsCount, newLocationCountOverThreshold);

        // Act
        return diseaseProcessGatekeeper.extentShouldRun(DISEASE_GROUP_ID);
    }

    private void setLastClassChangeDate(Boolean weekHasElapsed, Boolean hasChanged) {
        DateTime changeDate = null;
        if (hasChanged != null && weekHasElapsed != null) {
            int runDays = weekHasElapsed ? 7 : 1;
            int changeDays = hasChanged ? runDays - 1 : runDays + 1;
            changeDate = DateTime.now().minusDays(changeDays);
        }
        when(diseaseService.getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(DISEASE_GROUP_ID)).thenReturn(changeDate);
    }

    private void setLastModelRunPrepDate(Boolean weekHasElapsed) {
        DateTime lastModelRunPrepDate = null;
        if (weekHasElapsed != null) {
            int days = weekHasElapsed ? 7 : 1;
            lastModelRunPrepDate = DateTime.now().minusDays(days);
        }
        when(diseaseGroup.getLastModelRunPrepDate()).thenReturn(lastModelRunPrepDate);
    }

    private void setLastExtentGenerationDate(Boolean weekHasElapsed) {
        DateTime lastExtentGenerationDate = null;
        if (weekHasElapsed != null) {
            int days = weekHasElapsed ? 7 : 1;
            lastExtentGenerationDate = DateTime.now().minusDays(days);
        }
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(lastExtentGenerationDate);
    }

    private void setMinNewLocations(long newLocationsCount, Boolean newLocationCountOverThreshold) {
        Integer minNewLocations = null;
        if (newLocationCountOverThreshold != null) {
            long thresholdAdjustment = newLocationCountOverThreshold ? -1 : +1;
            minNewLocations = (int) (newLocationsCount + thresholdAdjustment);
        }
        when(diseaseGroup.getMinNewLocationsTrigger()).thenReturn(minNewLocations);
    }
}
