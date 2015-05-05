package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Helper class used to determine whether occurrences should come off the DataValidator and update their status
 * accordingly.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewManager {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceReviewManager.class);
    private static final String LOG_MESSAGE =
            "Removed %d disease occurrence(s) from validation (of which %d discarded); %d occurrence(s) now remaining";

    private DiseaseService diseaseService;

    public DiseaseOccurrenceReviewManager(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Update the disease occurrence's status according to the length of time it has been on the DataValidator for
     * review by the experts. If the occurrence has been on the DataValidator for long enough, so is being removed, and
     * has not actually received any reviews, set the status to DISCARDED_UNREVIEWED so that it is never sent to the
     * model or included in the disease extent. If we are during disease group set up, the occurrence should be removed
     * from DataValidator immediately, irrespective of time spent in review.
     * @param diseaseGroupId The ID of the disease group this model run preparation is for.
     * @param isAutomaticProcess If this is part of the automated daily process or for a manual model run.
     */
    public void updateDiseaseOccurrenceStatus(int diseaseGroupId, boolean isAutomaticProcess) {
        int numRemovedFromValidator = 0;
        int numDiscarded = 0;

        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId);
        Set<DiseaseOccurrence> reviewedOccurrences = getDiseaseOccurrencesInValidationWithReviews(diseaseGroupId);

        for (DiseaseOccurrence occurrence : occurrences) {
            DiseaseOccurrenceStatus newStatus = occurrence.getStatus();
            if (!isAutomaticProcess || occurrenceHasBeenInReviewForMoreThanMaximumNumberOfDays(occurrence)) {
                if (reviewedOccurrences.contains(occurrence)) {
                    newStatus = DiseaseOccurrenceStatus.READY;
                } else {
                    newStatus = DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED;
                    numDiscarded++;
                }
            }

            if (!newStatus.equals(occurrence.getStatus())) {
                occurrence.setStatus(newStatus);
                diseaseService.saveDiseaseOccurrence(occurrence);
                numRemovedFromValidator++;
            }
        }

        logResults(occurrences.size(), numRemovedFromValidator, numDiscarded);
    }

    private Set<DiseaseOccurrence> getDiseaseOccurrencesInValidationWithReviews(int diseaseGroupId) {
        List<DiseaseOccurrenceReview> reviews =
                diseaseService.getAllDiseaseOccurrenceReviewsForOccurrencesInValidation(diseaseGroupId);
        return new HashSet<>(extract(reviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    /**
     * Whichever date is latest out of the occurrence's created date and its disease group's automatic model runs start
     * date signifies when it was first available to be reviewed by experts on the DataValidator. If this is more than
     * the maximum number of days ago, comparing dates only, we deem it to be sufficiently reviewed and set the
     * status accordingly, so that it will no longer be shown on the DataValidator.
     * @param occurrence The disease occurrence.
     * @return Whether the occurrence has been in review for long enough to have its status changed from IN_REVIEW.
     */
    private boolean occurrenceHasBeenInReviewForMoreThanMaximumNumberOfDays(DiseaseOccurrence occurrence) {
        LocalDate addedDate = getComparisonDate(occurrence);
        LocalDate removalCutoff = diseaseService.subtractMaxDaysOnValidator(DateTime.now());
        return !addedDate.isAfter(removalCutoff);
    }

    private LocalDate getComparisonDate(DiseaseOccurrence o) {
        LocalDate createdDate = o.getCreatedDate().toLocalDate();
        LocalDate automaticModelRunsStartDate = o.getDiseaseGroup().getAutomaticModelRunsStartDate().toLocalDate();
        return getLatest(createdDate, automaticModelRunsStartDate);
    }

    private LocalDate getLatest(LocalDate createdDate, LocalDate automaticModelRunsStartDate) {
        return (automaticModelRunsStartDate.isAfter(createdDate)) ? automaticModelRunsStartDate : createdDate;
    }

    private void logResults(int numOriginallyInValidation, int numRemovedFromValidator, int numDiscarded) {
        int numRemaining = numOriginallyInValidation - numRemovedFromValidator;
        LOGGER.info(String.format(LOG_MESSAGE, numRemovedFromValidator, numDiscarded, numRemaining));
    }
}
