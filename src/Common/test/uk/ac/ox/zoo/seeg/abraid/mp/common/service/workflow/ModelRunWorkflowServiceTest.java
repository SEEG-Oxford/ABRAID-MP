package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.HashMap;
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
    private DiseaseOccurrenceReviewManager reviewManager;
    private DiseaseService diseaseService;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private ModelRunWorkflowService modelRunWorkflowService;

    @Before
    public void setUp() {
        weightingsCalculator = mock(WeightingsCalculator.class);
        modelRunRequester = mock(ModelRunRequester.class);
        reviewManager = mock(DiseaseOccurrenceReviewManager.class);
        diseaseService = mock(DiseaseService.class);
        diseaseExtentGenerator = mock(DiseaseExtentGenerator.class);
        modelRunWorkflowService = new ModelRunWorkflowServiceImpl(weightingsCalculator, modelRunRequester,
                reviewManager, diseaseService, diseaseExtentGenerator);
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

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(weightingsCalculator.calculateNewExpertsWeightings()).thenReturn(newWeightings);

        // Act
        modelRunWorkflowService.prepareForAndRequestManualModelRun(diseaseGroupId);

        // Assert
        verify(weightingsCalculator, times(1)).updateDiseaseOccurrenceExpertWeightings(
                eq(lastModelRunPrepDate), eq(diseaseGroupId));
        verify(reviewManager, times(1)).updateDiseaseOccurrenceIsValidatedValues(
                eq(diseaseGroupId), eq(DateTime.now()), eq(true));
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(eq(diseaseGroupId));
        verify(modelRunRequester, times(1)).requestModelRun(eq(diseaseGroupId));
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

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        modelRunWorkflowService.prepareForAndRequestAutomaticModelRun(diseaseGroupId);

        // Assert
        verify(weightingsCalculator, times(1)).updateDiseaseOccurrenceExpertWeightings(
                eq(lastModelRunPrepDate), eq(diseaseGroupId));
        verify(reviewManager, times(1)).updateDiseaseOccurrenceIsValidatedValues(
                eq(diseaseGroupId), eq(DateTime.now()), eq(false));
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(eq(diseaseGroupId));
        verify(modelRunRequester, times(1)).requestModelRun(eq(diseaseGroupId));
        verify(diseaseService, times(1)).saveDiseaseGroup(same(diseaseGroup));
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

        // Act
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId);

        // Assert - Parameters are hardcoded for Dengue in Service method presently
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(eq(diseaseGroupId));
    }
}
