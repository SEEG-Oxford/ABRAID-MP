package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void calculateExpertsWeightings() {
        // Act
        modelRunWorkflowService.calculateExpertsWeightings();

        // Assert
        verify(weightingsCalculator, times(1)).calculateNewExpertsWeightings();
    }

    @Test
    public void prepareForAndRequestModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
        DateTime lastModelRunPrepDate = DateTime.now().minusWeeks(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        List<DiseaseOccurrence> occurrences = new ArrayList<>();

        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(weightingsCalculator.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId)).
                thenReturn(occurrences);

        // Act
        modelRunWorkflowService.prepareForAndRequestModelRun(diseaseGroupId);

        // Assert
        verify(weightingsCalculator, times(1)).updateDiseaseOccurrenceExpertWeightings(
                eq(lastModelRunPrepDate), eq(diseaseGroupId));
        verify(reviewManager, times(1)).updateDiseaseOccurrenceIsValidatedValues(
                eq(diseaseGroupId), eq(DateTime.now()));
        verify(diseaseExtentGenerator, times(1)).generateDiseaseExtent(
                eq(diseaseGroupId), any(DiseaseExtentParameters.class));
        verify(modelRunRequester, times(1)).requestModelRun(eq(diseaseGroupId), same(occurrences));
        verify(diseaseService, times(1)).saveDiseaseGroup(same(diseaseGroup));
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
}
