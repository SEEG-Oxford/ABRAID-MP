package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.List;

/**
 * Helper class used to determine whether occurrences should come off the DataValidator and update the value of the
 * isValidated flag accordingly.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerHelper {

    DiseaseService diseaseService;

    public ModelRunManagerHelper(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    public void updateDiseaseOccurrenceIsValidatedValues(int diseaseGroupId, DateTime modelRunPrepDate) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesInValidation(diseaseGroupId);
        for (DiseaseOccurrence occurrence : occurrences) {
            boolean validated = occurrenceHasBeenInReviewForMoreThanAWeek(occurrence, modelRunPrepDate);
            if (occurrence.isValidated() != validated) {
                occurrence.setValidated(validated);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    // The occurrence's created date signifies when it was first available to be reviewed by experts on the
    // DataValidator. If this is more than a week ago, we deem it to be sufficiently reviewed and
    // set the isValidated flag to true, so that it will no longer be shown on the DataValidator.
    private boolean occurrenceHasBeenInReviewForMoreThanAWeek(DiseaseOccurrence occ, DateTime modelRunPrepDateTime) {
        LocalDate createdDate = occ.getCreatedDate().toLocalDate();
        LocalDate comparisonDate = createdDate.plusWeeks(1);
        LocalDate modelRunPrepDate = modelRunPrepDateTime.toLocalDate();
        return (comparisonDate.isEqual(modelRunPrepDate) || comparisonDate.isBefore(modelRunPrepDate));
    }
}
