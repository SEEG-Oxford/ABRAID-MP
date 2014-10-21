package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseOccurrenceReviewManager class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewManagerTest {

    private static final int DISEASE_GROUP_ID = 1;

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenAWeekHasNotElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenAWeekHasNotElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, false);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAWeekHasElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(7);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenAWeekHasElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(7);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanAWeekHasElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(8);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenMoreThanAWeekHasElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(8);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenAWeekHasNotElapsedSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(0);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenAWeekHasNotElapsedSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(0);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, false);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAWeekHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(7);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenAWeekHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(7);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanAWeekHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(8);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenMoreThanAWeekHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(8);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAutomaticModelRunsNotEnabled() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, null, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true);
    }


    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAutomaticModelRunsNotEnabledAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, null, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, true);
    }

    private DiseaseOccurrence arrangeDates(DateTime createdDate, DateTime automaticModelRunsStartDate, DateTime lastModelRunPrepDate) {
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(DISEASE_GROUP_ID);
        when(diseaseGroup.getAutomaticModelRunsStartDate()).thenReturn(automaticModelRunsStartDate);
        when(diseaseGroup.getLastModelRunPrepDate()).thenReturn(lastModelRunPrepDate);

        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.isValidated()).thenReturn(false);
        when(occurrence.getCreatedDate()).thenReturn(createdDate);
        when(occurrence.getDiseaseGroup()).thenReturn(diseaseGroup);
        return occurrence;
    }

    private void executeTest(DiseaseOccurrence occurrence, boolean hasBeenReviewed, Boolean expectedIsValidated) {
        // Arrange
        DiseaseService diseaseService = mockDiseaseService(occurrence, hasBeenReviewed);
        DiseaseOccurrenceReviewManager target = new DiseaseOccurrenceReviewManager(diseaseService);
        DateTime lastModelRunPrepDate = occurrence.getDiseaseGroup().getLastModelRunPrepDate();

        // Act
        target.updateDiseaseOccurrenceIsValidatedValues(DISEASE_GROUP_ID, lastModelRunPrepDate);

        // Assert
        int expectedTimes = (expectedIsValidated == null || expectedIsValidated) ? 1 : 0;
        verify(occurrence, times(expectedTimes)).setValidated(expectedIsValidated);
        verify(diseaseService, times(expectedTimes)).saveDiseaseOccurrence(occurrence);
    }

    private DiseaseService mockDiseaseService(DiseaseOccurrence occurrence, boolean hasBeenReviewed) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrencesInValidation(DISEASE_GROUP_ID)).thenReturn(Arrays.asList(occurrence));
        List<DiseaseOccurrenceReview> reviews = new ArrayList<>();
        if (hasBeenReviewed) {
            reviews.add(new DiseaseOccurrenceReview(mock(Expert.class), occurrence, DiseaseOccurrenceReviewResponse.UNSURE));
        }
        when(diseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(DISEASE_GROUP_ID)).thenReturn(reviews);
        return diseaseService;
    }
}
