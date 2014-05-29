package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.offset;
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

        Expert expert = createExpert("expert", expertsWeighting);

        DiseaseService mockDiseaseService = mockUpDiseaseServiceWithOneReview(expert, occurrence,
                DiseaseOccurrenceReviewResponse.YES, diseaseGroupId, mock(DiseaseGroup.class));
        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mockExpertService);

        // Act
        target.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);

        // Assert
        assertThat(occurrence.getExpertWeighting()).isEqualTo(expectedWeighting);
    }

    @Test
    public void updateExpertsWeightingsWithEmptyReviewsOfOccurrenceReturnsExpectedResult() {
        // Arrange - Only one expert has reviewed an occurrence so "reviewsOfOccurrence" in calculateDifference is empty
        Expert expert = createExpert("expert", 0.9);
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, new DiseaseOccurrence(), DiseaseOccurrenceReviewResponse.YES);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(Arrays.asList(review));
        WeightingsCalculator target = new WeightingsCalculator(mockDiseaseService, mock(ExpertService.class));

        // Act
        Map<Expert, Double> map = target.calculateNewExpertsWeightings();

        // Assert
        // The expert's response is "wrong" by the amount of the value that they reviewed (in this case they were "off"
        // by 1.0 from YES response). The expert's new weighting is then how "right" they were - which is 0.
        assertThat(map.get(expert)).isEqualTo(0.0);
    }

    private DiseaseService mockUpDiseaseServiceWithOneReview(Expert expert, DiseaseOccurrence occurrence,
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
        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService, mockExpertService);

        // Act
        target.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);

        // Assert
        for (DiseaseOccurrence occurrence : diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)) {
            assertThat(occurrence.getFinalWeighting()).isNotNull();
        }
    }

    @Test
    public void calculateNewExpertsWeightingsReturnsExpectedMap() {
        // Arrange - Experts 1 and 2 submit YES reviews for an occurrence. Their new weightings will be 1.0
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(87);
        DiseaseOccurrence occ = occurrences.get(0);
        Expert ex1 = createExpert("expert1", 1.0);
        Expert ex2 = createExpert("expert2", 0.5);

        DiseaseOccurrenceReview review1 = new DiseaseOccurrenceReview(ex1, occ, DiseaseOccurrenceReviewResponse.YES);
        DiseaseOccurrenceReview review2 = new DiseaseOccurrenceReview(ex2, occ, DiseaseOccurrenceReviewResponse.YES);

        ExpertService mockExpertService = mock(ExpertService.class);
        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(Arrays.asList(review1, review2));

        WeightingsCalculator weightingsCalculator = new WeightingsCalculator(mockDiseaseService, mockExpertService);

        // Act
        Map<Expert, Double> map = weightingsCalculator.calculateNewExpertsWeightings();

        // Assert - NB. The map returns only the experts whose weightings have changed.
        assertThat(map.keySet().contains(ex2)).isTrue();
        assertThat(map.get(ex2)).isEqualTo(1.0);
    }

    @Test
    public void calculateNewExpertsWeightingsReturnsExpectedValuesAcrossMultipleOccurrences() {
        // Arrange
        DiseaseService mockDiseaseService = mockUpDiseaseServiceWithManyReviews();
        ExpertService mockExpertService = mock(ExpertService.class);
        WeightingsCalculator weightingsCalculator = new WeightingsCalculator(mockDiseaseService, mockExpertService);

        // Act
        Map<Expert, Double> map = weightingsCalculator.calculateNewExpertsWeightings();

        // Assert - These values were calculated in Weights spreadsheet, according to the formula defined there.
        assertThat(map.keySet()).hasSize(3);
        List<Double> values = new ArrayList<>();
        values.addAll(map.values());
        assertThat(values.get(0)).isEqualTo(0.5016, offset(0.05));
        assertThat(values.get(1)).isEqualTo(0.9966, offset(0.05));
        assertThat(values.get(2)).isEqualTo(0.4983, offset(0.05));
    }

    private DiseaseService mockUpDiseaseServiceWithManyReviews() {

        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(87);
        DiseaseOccurrence occ1 = occurrences.get(0);
        DiseaseOccurrence occ2 = occurrences.get(1);
        DiseaseOccurrence occ3 = occurrences.get(2);

        Expert ex1 = createExpert("ex1", 0.0);
        Expert ex2 = createExpert("ex2", 0.0);
        Expert ex3 = createExpert("ex3", 0.0);

        List<DiseaseOccurrenceReview> reviews = Arrays.asList(
                new DiseaseOccurrenceReview(ex1, occ1, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex2, occ1, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex3, occ1, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex1, occ2, DiseaseOccurrenceReviewResponse.YES),
                new DiseaseOccurrenceReview(ex2, occ2, DiseaseOccurrenceReviewResponse.UNSURE),
                new DiseaseOccurrenceReview(ex3, occ2, DiseaseOccurrenceReviewResponse.NO),
                new DiseaseOccurrenceReview(ex1, occ3, DiseaseOccurrenceReviewResponse.NO),
                new DiseaseOccurrenceReview(ex2, occ3, DiseaseOccurrenceReviewResponse.NO),
                new DiseaseOccurrenceReview(ex3, occ3, DiseaseOccurrenceReviewResponse.NO));

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        when(mockDiseaseService.getAllDiseaseOccurrenceReviews()).thenReturn(reviews);
        return mockDiseaseService;
    }

    private Expert createExpert(String name, Double expertsWeighting) {
        Expert expert = new Expert();
        expert.setName(name);
        expert.setWeighting(expertsWeighting);
        return expert;
    }

}
