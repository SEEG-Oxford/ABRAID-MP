package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
    public void updateDiseaseOccurrenceStatusRemainsInReviewWhenMoreThanMaxDaysHasNotElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, DiseaseOccurrenceStatus.IN_REVIEW, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusRemainsInReviewWhenMoreThanMaxDaysHasNotElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, DiseaseOccurrenceStatus.IN_REVIEW, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsReadyWhenMaxDaysHasElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(5);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, DiseaseOccurrenceStatus.READY, 5);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsDiscardedWhenMaxDaysHasElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(6);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 6);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsReadyWhenMoreThanMaxDaysHasElapsedSinceCreatedDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(9);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, DiseaseOccurrenceStatus.READY, 8);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsDiscardedWhenMoreThanMaxDaysHasElapsedSinceCreatedDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(21);
        DateTime automaticModelRunsStartDate = createdDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 15);
    }

    @Test
    public void updateDiseaseOccurrenceStatusRemainsInReviewWhenMoreThanMaxDaysHasNotElapsedSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(0);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, DiseaseOccurrenceStatus.IN_REVIEW, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusRemainsInReviewWhenMoreThanMaxDaysHasNotElapsedSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(0);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, DiseaseOccurrenceStatus.IN_REVIEW, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsReadyWhenMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(7);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, DiseaseOccurrenceStatus.READY, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsDiscardedWhenMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(7);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsReadyWhenMoreThanMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDate() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(8);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, true, DiseaseOccurrenceStatus.READY, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsDiscardedWhenMoreThanMaxDaysHasElapsedSinceCreatedDateSinceAutomaticModelRunsStartDateAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime automaticModelRunsStartDate = lastModelRunPrepDate.minusDays(8);
        DateTime createdDate = automaticModelRunsStartDate.minusDays(2);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, automaticModelRunsStartDate, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, true, false, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsReadyWhenAutomaticModelRunsNotEnabled() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, null, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, true, DiseaseOccurrenceStatus.READY, 7);
    }

    @Test
    public void updateDiseaseOccurrenceStatusSetsDiscardedWhenAutomaticModelRunsNotEnabledAndOccurrenceNotReviewed() {
        // Arrange
        DateTime lastModelRunPrepDate = DateTime.now();
        DateTime createdDate = lastModelRunPrepDate.minusDays(0);
        DiseaseOccurrence occurrence = arrangeDates(createdDate, null, lastModelRunPrepDate);
        // Act and Assert
        executeTest(occurrence, false, false, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 7);
    }

    private DiseaseOccurrence arrangeDates(DateTime createdDate, DateTime automaticModelRunsStartDate, DateTime lastModelRunPrepDate) {
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(DISEASE_GROUP_ID);
        when(diseaseGroup.getAutomaticModelRunsStartDate()).thenReturn(automaticModelRunsStartDate);
        when(diseaseGroup.getLastModelRunPrepDate()).thenReturn(lastModelRunPrepDate);

        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getStatus()).thenReturn(DiseaseOccurrenceStatus.IN_REVIEW);
        when(occurrence.getCreatedDate()).thenReturn(createdDate);
        when(occurrence.getDiseaseGroup()).thenReturn(diseaseGroup);
        return occurrence;
    }

    private void executeTest(DiseaseOccurrence occurrence, boolean isAutomaticProcess, boolean hasBeenReviewed,
                             DiseaseOccurrenceStatus expectedStatus, int maxDays) {
        // Arrange
        DiseaseService diseaseService = mockDiseaseService(occurrence, hasBeenReviewed, maxDays);
        DiseaseOccurrenceReviewManager target = new DiseaseOccurrenceReviewManager(diseaseService);

        // Act
        target.updateDiseaseOccurrenceStatus(DISEASE_GROUP_ID, isAutomaticProcess);

        // Assert
        int expectedTimes = (expectedStatus == DiseaseOccurrenceStatus.READY ||
                expectedStatus == DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED) ? 1 : 0;
        verify(occurrence, times(expectedTimes)).setStatus(expectedStatus);
        verify(diseaseService, times(expectedTimes)).saveDiseaseOccurrence(occurrence);
    }

    private DiseaseService mockDiseaseService(DiseaseOccurrence occurrence, boolean hasBeenReviewed, final int maxDays) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseOccurrencesInValidation(DISEASE_GROUP_ID)).thenReturn(Arrays.asList(occurrence));
        List<DiseaseOccurrenceReview> reviews = new ArrayList<>();
        if (hasBeenReviewed) {
            reviews.add(new DiseaseOccurrenceReview(mock(Expert.class), occurrence, DiseaseOccurrenceReviewResponse.UNSURE));
        }
        when(diseaseService.getAllDiseaseOccurrenceReviewsForOccurrencesInValidation(DISEASE_GROUP_ID)).thenReturn(reviews);
        when(diseaseService.subtractMaxDaysOnValidator(any(DateTime.class))).thenAnswer(new Answer<LocalDate>() {
            @Override
            public LocalDate answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ((DateTime) invocationOnMock.getArguments()[0]).toLocalDate().minusDays(maxDays);
            }
        });
        return diseaseService;
    }
}
