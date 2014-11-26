package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.List;

/**
 * Handles disease occurrences. Specifically, if a batch end date is specified in the model run, it sets the
 * "validation parameters" (e.g. environmental suitability, distance from disease extent) for the disease occurrences up
 * until the end date.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandlerHelper {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceHandlerHelper.class);
    private static final String STARTING_HANDLING_LOG_MESSAGE = "Model run %d: starting disease occurrence handling";
    private static final String INITIAL_BATCH_LOG_MESSAGE = "Model run %d: this is the initial batch, so setting " +
            "status to AWAITING_BATCHING and final weighting to null for %d occurrence(s) of disease group %d (%s)";
    private static final String VALIDATION_LOG_MESSAGE =
            "Model run %d: setting validation parameters for %d occurrence(s) of disease group %d (%s) " +
                    "(batch start date %s, batch end date %s)";
    private static final String VALIDATION_COMPLETED_LOG_MESSAGE =
            "Model run %d: setting validation parameters completed";
    private static final String LOG_DATE_FORMAT = "dd MMM yyyy";
    private static final String NO_HANDLING_LOG_MESSAGE = "Model run %d: no disease occurrence handling to do";

    private DiseaseService diseaseService;
    private ModelRunService modelRunService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    public DiseaseOccurrenceHandlerHelper(DiseaseService diseaseService, ModelRunService modelRunService,
                                          DiseaseOccurrenceValidationService diseaseOccurrenceValidationService) {
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
    }

    /**
     * Handles disease occurrences for disease group setup (i.e. automatic model runs not yet enabled).
     * @param modelRun The model run.
     */
    @Transactional(rollbackFor = Exception.class)
    public DateTime handle(ModelRun modelRun) {
        // Reload the model run, because this may be a new transaction and we may need to save the model run later on
        modelRun = modelRunService.getModelRunByName(modelRun.getName());
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(modelRun.getDiseaseGroupId());
        DateTime batchingInitialisationDate = null;

        // If disease group is in the setup phase, set validation parameters on a batch of disease occurrences
        if (isBatchingRequired(modelRun, diseaseGroup)) {
            LOGGER.info(String.format(STARTING_HANDLING_LOG_MESSAGE, modelRun.getId()));
            batchingInitialisationDate = initialiseBatchingIfNecessary(modelRun, diseaseGroup);
            handleBatch(modelRun, diseaseGroup);
        } else {
            LOGGER.info(String.format(NO_HANDLING_LOG_MESSAGE, modelRun.getId()));
        }

        return batchingInitialisationDate;
    }

    /**
     * Ensures that all relevant occurrences are initialised, in particular any occurrences that were acquired while
     * batching initialisation was taking place.
     * @param diseaseGroupId The disease group ID.
     * @param batchingInitialisationDate The date that batching initialisation started, or null if batching was
     * not initialised by an earlier call to the handle() method.
     */
    @Transactional(rollbackFor = Exception.class)
    public void continueBatchingInitialisation(Integer diseaseGroupId, DateTime batchingInitialisationDate) {
        if (batchingInitialisationDate != null) {
            List<DiseaseOccurrence> occurrences = getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId);
            for (DiseaseOccurrence diseaseOccurrence : occurrences) {
                if (!batchingInitialisationDate.isAfter(diseaseOccurrence.getCreatedDate())) {
                    initialiseOccurrenceForBatching(diseaseOccurrence);
                }
            }
        }
    }

    private boolean isBatchingRequired(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        return (modelRun.getStatus() == ModelRunStatus.COMPLETED) &&
                (modelRun.getBatchStartDate() != null) && (modelRun.getBatchEndDate() != null) &&
                !diseaseGroup.isAutomaticModelRunsEnabled();
    }

    private DateTime initialiseBatchingIfNecessary(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        // If no batch of disease occurrences has completed for this disease group, initialise the batching process by
        // changing the status of all READY occurrences to AWAITING_BATCHING and setting their final weightings to null
        DateTime batchingInitialisationDate = null;
        Integer diseaseGroupId = diseaseGroup.getId();

        if (!modelRunService.hasBatchingEverCompleted(diseaseGroupId)) {
            batchingInitialisationDate = DateTime.now();
            List<DiseaseOccurrence> occurrences = getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId);
            LOGGER.info(String.format(INITIAL_BATCH_LOG_MESSAGE, modelRun.getId(), occurrences.size(),
                    diseaseGroupId, diseaseGroup.getName()));

            for (DiseaseOccurrence occurrence : occurrences) {
                initialiseOccurrenceForBatching(occurrence);
            }
        }

        return batchingInitialisationDate;
    }

    private void handleBatch(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        // Ensure that the batch start date is at the very start of the day
        DateTime batchStartDateWithMinimumTime = getBatchStartDateWithMinimumTime(modelRun.getBatchStartDate());
        // Ensure that the batch end date is at the very end of the day
        DateTime batchEndDateWithMaximumTime = getBatchEndDateWithMaximumTime(modelRun.getBatchEndDate());

        // Get the occurrences that we want to batch, and then set their validation parameters
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForBatching(diseaseGroup.getId(),
                batchStartDateWithMinimumTime, batchEndDateWithMaximumTime);
        LOGGER.info(String.format(VALIDATION_LOG_MESSAGE, modelRun.getId(), occurrences.size(),
                diseaseGroup.getId(), diseaseGroup.getName(),
                batchStartDateWithMinimumTime.toString(LOG_DATE_FORMAT),
                batchEndDateWithMaximumTime.toString(LOG_DATE_FORMAT)));
        setValidationParametersForOccurrencesBatch(occurrences);
        setModelRunBatchingParameters(modelRun, occurrences.size());
        LOGGER.info(String.format(VALIDATION_COMPLETED_LOG_MESSAGE, modelRun.getId()));
    }

    private List<DiseaseOccurrence> getDiseaseOccurrencesForBatchingInitialisation(int diseaseGroupId) {
        return diseaseService.getDiseaseOccurrencesByDiseaseGroupIdAndStatuses(diseaseGroupId,
                DiseaseOccurrenceStatus.READY);
    }

    private DateTime getBatchStartDateWithMinimumTime(DateTime batchStartDate) {
        return batchStartDate.withTimeAtStartOfDay();
    }

    private DateTime getBatchEndDateWithMaximumTime(DateTime batchEndDate) {
        return batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1);
    }

    private void initialiseOccurrenceForBatching(DiseaseOccurrence occurrence) {
        occurrence.setStatus(DiseaseOccurrenceStatus.AWAITING_BATCHING);
        occurrence.setFinalWeighting(null);
        occurrence.setFinalWeightingExcludingSpatial(null);
        diseaseService.saveDiseaseOccurrence(occurrence);
    }

    private void setValidationParametersForOccurrencesBatch(List<DiseaseOccurrence> occurrences) {
        if (occurrences.size() > 0) {
            diseaseOccurrenceValidationService.addValidationParameters(occurrences);
            for (DiseaseOccurrence occurrence : occurrences) {
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    private void setModelRunBatchingParameters(ModelRun modelRun, int batchedOccurrenceCount) {
        modelRun.setBatchingCompletedDate(DateTime.now());
        modelRun.setBatchOccurrenceCount(batchedOccurrenceCount);
        modelRunService.saveModelRun(modelRun);
    }
}
