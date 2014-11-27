package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

/**
 * Validator for the dates of a batch of disease occurrences.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class BatchDatesValidator {
    private static final String TOO_FEW_OCCURRENCES_MESSAGE = "This batch contains %d non-country occurrence(s), " +
            "which is below the Minimum Data Volume (%d) and therefore will be too few for the model run after this " +
            "one. Please increase the batch size, or reduce the value of Minimum Data Volume.";

    private ModelRunService modelRunService;
    private DiseaseService diseaseService;

    public BatchDatesValidator(ModelRunService modelRunService, DiseaseService diseaseService) {
        this.modelRunService = modelRunService;
        this.diseaseService = diseaseService;
    }

    /**
     * Validate the batch dates.
     * @param diseaseGroupId The disease group ID.
     * @param batchStartDate The batch start date.
     * @param batchEndDate The batch end date.
     */
    public void validate(int diseaseGroupId, DateTime batchStartDate, DateTime batchEndDate) {
        if ((batchStartDate != null) && (batchEndDate != null) &&
                !modelRunService.hasBatchingEverCompleted(diseaseGroupId)) {
            // If this is the first batch, count the number of points in the batch date range that are eligible
            // for a future model run containing only those points. If this is less than Minimum Data Volume, return an
            // error.
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
            int minDataVolume = diseaseGroup.getMinDataVolume();

            long occurrenceCount = diseaseService.getNumberOfDiseaseOccurrencesEligibleForModelRun(diseaseGroupId,
                    batchStartDate, batchEndDate);

            if (occurrenceCount < minDataVolume) {
                throw new ModelRunWorkflowException(String.format(TOO_FEW_OCCURRENCES_MESSAGE,
                        occurrenceCount, minDataVolume));
            }
        }
    }
}
