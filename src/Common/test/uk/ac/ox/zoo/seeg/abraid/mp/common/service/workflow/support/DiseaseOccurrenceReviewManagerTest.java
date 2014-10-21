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
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenMoreThanMaxDaysHasNotElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenMoreThanMaxDaysHasNotElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, false, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanMaxDaysHasElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(5);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, 5);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenMoreThanMaxDaysHasElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(6);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null, 6);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanMoreThanMaxDaysHasElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(9);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, 8);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenMoreThanMoreThanMaxDaysHasElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(21);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null, 15);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenMoreThanMaxDaysHasNotElapsedSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(0);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesRemainsFalseWhenMoreThanMaxDaysHasNotElapsedSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(0);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, false, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(7);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenMoreThanMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(7);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenMoreThanMoreThanMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(8);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsNullWhenMoreThanMoreThanMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(8);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, null, 7);
    }

    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAutomaticModelRunsNotEnabled() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, null, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, 7);
    }


    @Test
    public void updateDiseaseOccurrenceIsValidatedValuesSetsTrueWhenAutomaticModelRunsNotEnabledAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, null, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, true, 7);
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

    private void executeTest(DiseaseOccurrence occurrence, boolean hasBeenReviewed, Boolean expectedIsValidated, int maxDays) {
        // Arrange
        DiseaseService diseaseService = mockDiseaseService(occurrence, hasBeenReviewed);
        DiseaseOccurrenceReviewManager target = new DiseaseOccurrenceReviewManager(diseaseService, maxDays);
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
