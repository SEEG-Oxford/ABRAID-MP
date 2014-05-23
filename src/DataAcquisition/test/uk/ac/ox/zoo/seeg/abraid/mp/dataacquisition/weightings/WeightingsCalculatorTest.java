package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

import java.util.ArrayList;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the WeightingsCalculator class.
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculatorTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Before
    public void setFixedTime() {
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    // An expert (who has a weighting of 0.9) has reviewed YES (value of 1) to an occurrence, so the occurrence's
    // weighting should update to take a value of 0.95 ie (expert's weighting x response value) shifted from range
    // [-1, 1] to desired range [0,1].
    @Test
    public void updateDiseaseOccurrenceExpertWeightingsShouldGiveExpectedResult() throws Exception {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now().minusDays(7);
        int diseaseGroupId = 87;
        double initialWeighting = 0.0;
        double expertsWeighting = 0.9;
        double expectedWeighting = 0.95;

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setExpertWeighting(initialWeighting);

        Expert expert = new Expert();
        expert.setWeighting(expertsWeighting);

        DiseaseService mockDiseaseService = mockUpDiseaseService(expert, occurrence,
                DiseaseOccurrenceReviewResponse.YES, diseaseGroupId, mock(DiseaseGroup.class));
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);

        // Assert
        assertThat(occurrence.getExpertWeighting()).isEqualTo(expectedWeighting);
    }

    private DiseaseService mockUpDiseaseService(Expert expert, DiseaseOccurrence occurrence,
                                                DiseaseOccurrenceReviewResponse response, int diseaseGroupId,
                                                DiseaseGroup diseaseGroup) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, occurrence, response);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(mockDiseaseService.getDiseaseOccurrenceReviewsForModelRunPrep(
                (DateTime) any(), anyInt())).thenReturn(new ArrayList<>(Arrays.asList(review)));
        return mockDiseaseService;
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsSetsFinalWeightingForEveryOccurrenceForModelRunRequest() {
        // Arrange
        int diseaseGroupId = 87; // Dengue
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(null);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        for (DiseaseOccurrence occurrence : diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)) {
            assertThat(occurrence.getFinalWeighting()).isNotNull();
        }
    }
}
