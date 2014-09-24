package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the ModelRunWorkflowServiceImpl class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunWorkflowServiceTest {
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;
    private ModelRunOccurrencesSelector modelRunOccurrencesSelector;
    private DiseaseOccurrenceReviewManager reviewManager;
    private DiseaseService diseaseService;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private ModelRunWorkflowServiceImpl modelRunWorkflowService;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;

    @Before
    public void setUp() {
        weightingsCalculator = mock(WeightingsCalculator.class);
        modelRunRequester = mock(ModelRunRequester.class);
        modelRunOccurrencesSelector = mock(ModelRunOccurrencesSelector.class);
        reviewManager = mock(DiseaseOccurrenceReviewManager.class);
        diseaseService = mock(DiseaseService.class);
        LocationService locationService = mock(LocationService.class);
        diseaseExtentGenerator = mock(DiseaseExtentGenerator.class);
        automaticModelRunsEnabler = mock(AutomaticModelRunsEnabler.class);
        modelRunWorkflowService = spy(new ModelRunWorkflowServiceImpl(weightingsCalculator, modelRunRequester,
                reviewManager, diseaseService, locationService, diseaseExtentGenerator, automaticModelRunsEnabler));
    }

    @Test
    public void prepareForAndRequestManuallyTriggeredModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        Map<Integer, Double> newWeightings = new HashMap<>();
        DateTime batchEndDate = DateTime.now().plusDays(1);
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);
        when(weightingsCalculator.calculateNewExpertsWeightings()).thenReturn(newWeightings);

        // Act
        modelRunWorkflowService.prepareForAndRequestManuallyTriggeredModelRun(diseaseGroupId, batchEndDate);

        // Assert
        verify(weightingsCalculator).updateDiseaseOccurrenceExpertWeightings(eq(lastModelRunPrepDate),
                eq(diseaseGroupId));
        verify(reviewManager).updateDiseaseOccurrenceIsValidatedValues(eq(diseaseGroupId), eq(DateTime.now()),
                eq(true));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), isNull(DateTime.class), eq(false));
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), eq(batchEndDate));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
        verify(weightingsCalculator).saveExpertsWeightings(same(newWeightings));
    }

    @Test
    public void prepareForAndRequestAutomaticModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);

        // Act
        modelRunWorkflowService.prepareForAndRequestAutomaticModelRun(diseaseGroupId);

        // Assert
        verify(weightingsCalculator).updateDiseaseOccurrenceExpertWeightings(eq(lastModelRunPrepDate),
                eq(diseaseGroupId));
        verify(reviewManager).updateDiseaseOccurrenceIsValidatedValues(eq(diseaseGroupId), eq(DateTime.now()),
                eq(false));
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate),
                eq(false));
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
    }

    @Test
    public void prepareForAndRequestModelRunUsingGoldStandardOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        Map<Integer, Double> newWeightings = new HashMap<>();
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, true);
        when(weightingsCalculator.calculateNewExpertsWeightings()).thenReturn(newWeightings);

        // Act
        modelRunWorkflowService.prepareForAndRequestModelRunUsingGoldStandardOccurrences(diseaseGroupId);

        // Assert
        verify(weightingsCalculator, never()).updateDiseaseOccurrenceExpertWeightings(any(DateTime.class), anyInt());
        verify(reviewManager, never()).updateDiseaseOccurrenceIsValidatedValues(anyInt(), any(DateTime.class),
                anyBoolean());
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), isNull(DateTime.class), eq(true));
        verify(modelRunRequester).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class));
        verify(diseaseService).saveDiseaseGroup(same(diseaseGroup));
        verify(weightingsCalculator).saveExpertsWeightings(same(newWeightings));
    }

    @Test
    public void enableAutomaticModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(automaticModelRunsEnabler).enable(eq(diseaseGroupId));
    }

    @Test
    public void calculateExpertsWeightings() {
        // Arrange
        Map<Integer, Double> expectedNewWeightings = new HashMap<>();
        when(weightingsCalculator.calculateNewExpertsWeightings()).thenReturn(expectedNewWeightings);

        // Act
        Map<Integer, Double> actualNewWeightings = modelRunWorkflowService.calculateExpertsWeightings();

        // Assert
        assertThat(actualNewWeightings).isEqualTo(expectedNewWeightings);
    }

    @Test
    public void saveExpertsWeightings() {
        // Arrange
        Map<Integer, Double> map = new HashMap<>();

        // Act
        modelRunWorkflowService.saveExpertsWeightings(map);

        // Assert
        verify(weightingsCalculator).saveExpertsWeightings(same(map));
    }

    @Test
    public void generateDiseaseExtentWithAutomaticModelRunsDisabled() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), eq((DateTime) null), eq(false));
        verify(modelRunWorkflowService, never()).selectOccurrencesForModelRun(anyInt(), anyBoolean());
    }

    @Test
    public void generateDiseaseExtentWithAutomaticModelRunsEnabled() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId, false);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate),
                eq(false));
    }

    @Test
    public void generateDiseaseExtentUsingGoldStandardOccurrences() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);

        // Act
        modelRunWorkflowService.generateDiseaseExtentUsingGoldStandardOccurrences(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator).generateDiseaseExtent(eq(diseaseGroup), eq((DateTime) null), eq(true));
    }

    private List<DiseaseOccurrence> createListWithDate(DateTime minimumOccurrenceDate) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setOccurrenceDate(minimumOccurrenceDate);
        return Arrays.asList(occurrence);
    }
}
