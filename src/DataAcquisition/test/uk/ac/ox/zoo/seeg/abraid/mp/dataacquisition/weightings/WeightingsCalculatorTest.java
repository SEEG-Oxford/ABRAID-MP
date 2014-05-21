package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

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

public class WeightingsCalculatorTest {

    @Autowired
    DiseaseService diseaseService;

    @Before
    public void setFixedTime() {
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsExecutesWhenLastRetrievalDateIsNull() throws Exception {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(87);
        WeightingsCalculator target = new WeightingsCalculator(diseaseService);

        // Act
        target.updateDiseaseOccurrenceWeightings(anyInt());

        // Assert
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isNotNull();
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsDoesNotExecuteWhenAWeekHasNotPassed() throws Exception {
        executeTest(1, 0.0);
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsExecutesWhenAWeekHasPassed() throws Exception {
        executeTest(7, 3.0);
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsExecutesWhenMoreThanAWeekHasPassed() throws Exception {
        executeTest(8, 3.0);
    }

    // An expert (who has a weighting of 3.0) has reviewed YES (value of 1) to an occurrence.
    // If 1 day has passed, the occurrence's weighting should remain at its initial value of 0.0.
    // If 7 days have passed then it should have updated to take a value of 3.0 (expert's weighting x response value).
    private void executeTest(int daysSinceLastRetrievalDate, double expectedWeighting) throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        double initialWeighting = 0.0;
        double expertsWeighting = 3.0;


        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getLastModelRunPrepDate()).thenReturn(DateTime.now().minusDays(daysSinceLastRetrievalDate));

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setExpertWeighting(initialWeighting);

        Expert expert = new Expert();
        expert.setWeighting(expertsWeighting);
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, occurrence,
                                                                     DiseaseOccurrenceReviewResponse.YES);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getAllReviewsForDiseaseGroupOccurrencesWithNewReviewsSinceLastModelRunPrep(
                (DateTime) any(), anyInt())).thenReturn(new ArrayList<>(Arrays.asList(review)));

        WeightingsCalculator target = new WeightingsCalculator(diseaseService);

        // Act
        target.updateDiseaseOccurrenceWeightings(diseaseGroupId);

        // Assert
        assertThat(occurrence.getExpertWeighting()).isEqualTo(expectedWeighting);
    }
}
