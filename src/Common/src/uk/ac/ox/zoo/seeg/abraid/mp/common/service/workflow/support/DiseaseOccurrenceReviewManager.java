package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
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
            "Removed %d disease occurrence(s) from validation; %d occurrence(s) now remaining";
    private final int maxDaysOnValidator;

    private DiseaseService diseaseService;

    public DiseaseOccurrenceReviewManager(DiseaseService diseaseService, int maxDaysOnValidator) {
        this.diseaseService = diseaseService;
        this.maxDaysOnValidator = maxDaysOnValidator;
    }

    /**
     * Update the disease occurrence's status according to the length of time it has been on the DataValidator for
     * review by the experts. If the occurrence has been on the DataValidator for long enough, so is being removed, and
     * has not actually received any reviews, set the status to DISCARDED_UNREVIEWED so that it is never sent to the
     * model or included in the disease extent. If we are during disease group set up, the occurrence should be removed
     * from DataValidator immediately, irrespective of time spent in review.
     * @param diseaseGroupId The id of the disease group this model run preparation is for.
     * @param modelRunPrepDate The official start time of the model run preparation tasks.
     */
    public void updateDiseaseOccurrenceStatus(int diseaseGroupId, DateTime modelRunPrepDate) {
        int numRemovedFromValidator = 0;

        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId);
        Set<DiseaseOccurrence> reviewedOccurrences = getAllReviewedOccurrences(diseaseGroupId);

        for (DiseaseOccurrence occurrence : occurrences) {
            DiseaseOccurrenceStatus newStatus = occurrence.getStatus();
            if (duringDiseaseSetUp(occurrence.getDiseaseGroup())) {
                newStatus = DiseaseOccurrenceStatus.READY;
            } else if (occurrenceHasBeenInReviewForMoreThanMaximumNumberOfDays(occurrence, modelRunPrepDate)) {
                if (reviewedOccurrences.contains(occurrence)) {
                    newStatus = DiseaseOccurrenceStatus.READY;
                } else {
                    newStatus = DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED;
                }
            }

            if (!newStatus.equals(occurrence.getStatus())) {
                occurrence.setStatus(newStatus);
                diseaseService.saveDiseaseOccurrence(occurrence);
                numRemovedFromValidator++;
            }
        }

        logResults(occurrences.size(), numRemovedFromValidator);
    }

    private boolean duringDiseaseSetUp(DiseaseGroup diseaseGroup) {
        return diseaseGroup.getAutomaticModelRunsStartDate() == null;
    }

    private Set<DiseaseOccurrence> getAllReviewedOccurrences(int diseaseGroupId) {
        List<DiseaseOccurrenceReview> reviews =
                diseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(diseaseGroupId);
        return new HashSet<>(extract(reviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    /**
     * Whichever date is latest out of the occurrence's created date and its disease group's automatic model runs start
     * date signifies when it was first available to be reviewed by experts on the DataValidator. If this is more than
     * the maximum number of days ago, comparing dates only, we deem it to be sufficiently reviewed and set the
     * status accordingly, so that it will no longer be shown on the DataValidator.
     * @param occurrence The disease occurrence.
     * @param modelRunPrepDateTime The date (incl. time) of the current model run
     * @return The new value of the isValidated property.
     */
    private boolean occurrenceHasBeenInReviewForMoreThanMaximumNumberOfDays(DiseaseOccurrence occurrence,
                                                              DateTime modelRunPrepDateTime) {
        LocalDate comparisonDate = getComparisonDate(occurrence);
        LocalDate modelRunPrepDate = modelRunPrepDateTime.toLocalDate();
        return !comparisonDate.isAfter(modelRunPrepDate);
    }

    private LocalDate getComparisonDate(DiseaseOccurrence o) {
        LocalDate createdDate = o.getCreatedDate().toLocalDate();
        LocalDate automaticModelRunsStartDate = o.getDiseaseGroup().getAutomaticModelRunsStartDate().toLocalDate();
        return getLatest(createdDate, automaticModelRunsStartDate).plusDays(maxDaysOnValidator);
    }

    private LocalDate getLatest(LocalDate createdDate, LocalDate automaticModelRunsStartDate) {
        return (automaticModelRunsStartDate.isAfter(createdDate)) ? automaticModelRunsStartDate : createdDate;
    }

    private void logResults(int numOriginallyInValidation, int numRemovedFromValidator) {
        int numRemaining = numOriginallyInValidation - numRemovedFromValidator;
        LOGGER.info(String.format(LOG_MESSAGE, numRemovedFromValidator, numRemaining));
    }
}
