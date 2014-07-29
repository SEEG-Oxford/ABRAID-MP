package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.List;

/**
 * Handles disease occurrences. Specifically, if a batch end date is specified in the model run, it sets the
 * "validation parameters" (e.g. environmental suitability, distance from disease extent) for the disease occurrences up
 * until the end date.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandler {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceHandler.class);
    private static final String VALIDATION_LOG_MESSAGE =
            "Model run %d: setting validation parameters for %d occurrence(s) of disease group %d (%s) " +
            "(batch end date %s)";
    private static final String VALIDATION_COMPLETED_LOG_MESSAGE =
            "Model run %d: setting validation parameters completed";
    private static final String NO_HANDLING_LOG_MESSAGE = "Model run %d: no disease occurrence handling to do";
    private static final Integer TRANSACTION_SIZE = 100;

    private DiseaseService diseaseService;
    private DiseaseOccurrenceHandlerHelper diseaseOccurrenceHandlerHelper;

    public DiseaseOccurrenceHandler(DiseaseService diseaseService,
                                    DiseaseOccurrenceHandlerHelper diseaseOccurrenceHandlerHelper) {
        this.diseaseService = diseaseService;
        this.diseaseOccurrenceHandlerHelper = diseaseOccurrenceHandlerHelper;
    }

    /**
     * Handles disease occurrences for disease group setup (i.e. automatic model runs not yet enabled).
     * @param modelRun The model run.
     */
    public void handle(ModelRun modelRun) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(modelRun.getDiseaseGroupId());

        // If disease group is in the setup phase, set validation parameters on a batch of disease occurrences
        if (didModelRunComplete(modelRun) && !areAutomaticModelRunsEnabled(diseaseGroup)) {
            diseaseOccurrenceHandlerHelper.initialiseBatchingIfNecessary(modelRun, diseaseGroup);
            handleBatch(modelRun, diseaseGroup);
        } else {
            LOGGER.info(String.format(NO_HANDLING_LOG_MESSAGE, modelRun.getId()));
        }
    }

    private void handleBatch(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        DateTime batchEndDate = modelRun.getBatchEndDate();
        if (batchEndDate != null) {
            // Ensure that the batch end date is at the very end of the day
            DateTime batchEndDateWithMaximumTime = getBatchEndDateWithMaximumTime(batchEndDate);

            // Get the occurrences that we want to batch, and then set their validation parameters
            List<Integer> occurrenceIDs = diseaseService.getDiseaseOccurrenceIDsForBatching(diseaseGroup.getId(),
                    batchEndDateWithMaximumTime);
            LOGGER.info(String.format(VALIDATION_LOG_MESSAGE, modelRun.getId(), occurrenceIDs.size(),
                    diseaseGroup.getId(), diseaseGroup.getName(), batchEndDateWithMaximumTime));
            setValidationParametersForOccurrencesBatch(occurrenceIDs);
            diseaseOccurrenceHandlerHelper.setBatchCompletedDate(modelRun);
            LOGGER.info(String.format(VALIDATION_COMPLETED_LOG_MESSAGE, modelRun.getId()));
        }
    }

    private boolean didModelRunComplete(ModelRun modelRun) {
        return modelRun.getStatus() == ModelRunStatus.COMPLETED;
    }

    private boolean areAutomaticModelRunsEnabled(DiseaseGroup diseaseGroup) {
        return diseaseGroup.isAutomaticModelRunsEnabled();
    }

    private DateTime getBatchEndDateWithMaximumTime(DateTime batchEndDate) {
        return batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1);
    }

    private void setValidationParametersForOccurrencesBatch(List<Integer> occurrenceIDs) {
        // Set the validation parameters a group at a time, where each group is one transaction. This is because
        // there may be 1000s of occurrences to handle, and setting validation parameters takes some time.
        int numOccurrences = occurrenceIDs.size();
        for (int startIndex = 0; startIndex >= numOccurrences; startIndex += TRANSACTION_SIZE) {
            int endIndex = getEndIndexForOccurrenceBatch(numOccurrences, startIndex);
            List<Integer> subListOccurrenceIDs = occurrenceIDs.subList(startIndex, endIndex);
            diseaseOccurrenceHandlerHelper.setValidationParametersForOccurrencesBatch(subListOccurrenceIDs);
        }
    }

    private int getEndIndexForOccurrenceBatch(int numOccurrences, int startIndex) {
        int endIndex = startIndex + TRANSACTION_SIZE;
        if (endIndex > numOccurrences) {
            endIndex = numOccurrences;
        }
        return endIndex;
    }
}
