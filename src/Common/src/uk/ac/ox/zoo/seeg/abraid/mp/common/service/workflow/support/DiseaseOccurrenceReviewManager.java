package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.List;

/**
 * Helper class used to determine whether occurrences should come off the DataValidator and update the value of the
 * isValidated flag accordingly.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewManager {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceReviewManager.class);
    private static final String LOG_MESSAGE =
            "Removed %d disease occurrence(s) from validation; %d occurrence(s) now remaining";

    private DiseaseService diseaseService;

    public DiseaseOccurrenceReviewManager(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * Update the value of the isValidated flag according to the length of time it has been on the DataValidator for
     * review by the experts.
     * @param diseaseGroupId The id of the disease group this model run preparation is for.
     * @param modelRunPrepDate The official start time of the model run preparation tasks.
     * @param alwaysRemoveFromValidator True if occurrences in validation should always be removed from the
     * DataValidator (instead of ensuring they have been on for a certain period of time), otherwise false.
     */
    public void updateDiseaseOccurrenceIsValidatedValues(int diseaseGroupId, DateTime modelRunPrepDate,
                                                         boolean alwaysRemoveFromValidator) {
        int numRemovedFromValidator = 0;

        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId);
        for (DiseaseOccurrence occurrence : occurrences) {
            if (alwaysRemoveFromValidator || occurrenceHasBeenInReviewForMoreThanAWeek(occurrence, modelRunPrepDate)) {
                occurrence.setValidated(true);
                diseaseService.saveDiseaseOccurrence(occurrence);
                numRemovedFromValidator++;
            }
        }

        logResults(occurrences.size(), numRemovedFromValidator);
    }

    /**
     * The occurrence's created date signifies when it was first available to be reviewed by experts on the
     * DataValidator. If this is more than one week ago, comparing dates only, we deem it to be sufficiently reviewed
     * and set the isValidated flag to true, so that it will no longer be shown on the DataValidator.
     * @param occ The disease occurrence.
     * @param modelRunPrepDateTime The date (incl. time) of the current model run
     * @return The new value of the isValidated property.
     */
    private boolean occurrenceHasBeenInReviewForMoreThanAWeek(DiseaseOccurrence occ, DateTime modelRunPrepDateTime) {
        LocalDate createdDate = occ.getCreatedDate().toLocalDate();
        LocalDate comparisonDate = createdDate.plusWeeks(1);
        LocalDate modelRunPrepDate = modelRunPrepDateTime.toLocalDate();
        return !comparisonDate.isAfter(modelRunPrepDate);
    }

    private void logResults(int numOriginallyInValidation, int numRemovedFromValidator) {
        int numRemaining = numOriginallyInValidation - numRemovedFromValidator;
        LOGGER.info(String.format(LOG_MESSAGE, numRemovedFromValidator, numRemaining));
    }
}
