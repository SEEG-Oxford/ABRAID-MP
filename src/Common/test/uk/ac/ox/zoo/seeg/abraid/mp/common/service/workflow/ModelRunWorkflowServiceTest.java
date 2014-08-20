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
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    @Before
    public void setUp() {
        weightingsCalculator = mock(WeightingsCalculator.class);
        modelRunRequester = mock(ModelRunRequester.class);
        modelRunOccurrencesSelector = mock(ModelRunOccurrencesSelector.class);
        reviewManager = mock(DiseaseOccurrenceReviewManager.class);
        diseaseService = mock(DiseaseService.class);
        LocationService locationService = mock(LocationService.class);
        diseaseExtentGenerator = mock(DiseaseExtentGenerator.class);
        diseaseOccurrenceValidationService = mock(DiseaseOccurrenceValidationService.class);
        modelRunWorkflowService = spy(new ModelRunWorkflowServiceImpl(weightingsCalculator, modelRunRequester,
                reviewManager, diseaseService, locationService, diseaseExtentGenerator, diseaseOccurrenceValidationService));
    }

    @Test
    public void prepareForAndRequestManualModelRun() {
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
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId);
        when(modelRunOccurrencesSelector.selectModelRunDiseaseOccurrences()).thenReturn(occurrences);
        when(weightingsCalculator.calculateNewExpertsWeightings()).thenReturn(newWeightings);

        // Act
        modelRunWorkflowService.prepareForAndRequestManuallyTriggeredModelRun(diseaseGroupId, batchEndDate);

        // Assert
        verify(weightingsCalculator, times(1)).updateDiseaseOccurrenceExpertWeightings(
                eq(lastModelRunPrepDate), eq(diseaseGroupId));
        verify(reviewManager, times(1)).updateDiseaseOccurrenceIsValidatedValues(
                eq(diseaseGroupId), eq(DateTime.now()), eq(true));
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate));
        verify(modelRunRequester, times(1)).requestModelRun(eq(diseaseGroupId), same(occurrences), eq(batchEndDate));
        verify(diseaseService, times(1)).saveDiseaseGroup(same(diseaseGroup));
        verify(weightingsCalculator, times(1)).saveExpertsWeightings(same(newWeightings));
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
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId);

        // Act
        modelRunWorkflowService.prepareForAndRequestAutomaticModelRun(diseaseGroupId);

        // Assert
        verify(weightingsCalculator, times(1)).updateDiseaseOccurrenceExpertWeightings(
                eq(lastModelRunPrepDate), eq(diseaseGroupId));
        verify(reviewManager, times(1)).updateDiseaseOccurrenceIsValidatedValues(
                eq(diseaseGroupId), eq(DateTime.now()), eq(false));
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate));
        verify(modelRunRequester, times(1)).requestModelRun(eq(diseaseGroupId), same(occurrences), isNull(DateTime.class));
        verify(diseaseService, times(1)).saveDiseaseGroup(same(diseaseGroup));
    }

    @Test
    public void enableAutomaticModelRunsSavesAutomaticModelRunsStartDateOnDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        // Act
        modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(diseaseService, times(1)).saveDiseaseGroup(diseaseGroup);
        assertThat(diseaseGroup.getAutomaticModelRunsStartDate()).isEqualTo(now);
    }

    @Test
    public void enableAutomaticModelRunsSavesClassChangedDateOnAdminUnitDiseaseExtentClasses() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        AdminUnitDiseaseExtentClass extentClass = new AdminUnitDiseaseExtentClass();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(Arrays.asList(extentClass));

        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        // Act
        modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(diseaseService, times(1)).saveAdminUnitDiseaseExtentClass(extentClass);
        assertThat(extentClass.getClassChangedDate()).isEqualTo(now);
    }

    @Test
    public void enableAutomaticModelRunsAddsValidationParametersToDiseaseOccurrence() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        when(diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId, false)).thenReturn(Arrays.asList(occurrence));

        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        // Act
        modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(diseaseOccurrenceValidationService, times(1)).addValidationParameters(Arrays.asList(occurrence));
        verify(diseaseService, times(1)).saveDiseaseOccurrence(occurrence);
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
        verify(weightingsCalculator, times(1)).saveExpertsWeightings(same(map));
    }

    @Test
    public void generateDiseaseExtent() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = createListWithDate(minimumOccurrenceDate);
        doReturn(occurrences).when(modelRunWorkflowService).selectOccurrencesForModelRun(diseaseGroupId);

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroup);

        // Assert
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(eq(diseaseGroup), same(minimumOccurrenceDate));
    }

    private List<DiseaseOccurrence> createListWithDate(DateTime minimumOccurrenceDate) {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setOccurrenceDate(minimumOccurrenceDate);
        return Arrays.asList(occurrence);
    }
}
