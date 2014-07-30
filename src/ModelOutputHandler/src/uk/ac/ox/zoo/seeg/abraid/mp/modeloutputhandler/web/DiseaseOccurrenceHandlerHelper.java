package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.List;

/**
 * Helper class for the DiseaseOccurrenceHandler.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceHandlerHelper {
    private static final Logger LOGGER = Logger.getLogger(DiseaseOccurrenceHandlerHelper.class);
    private static final String INITIAL_BATCH_LOG_MESSAGE = "Model run %d: this is the initial batch, so setting " +
            "final weighting to null for %d occurrence(s) of disease group %d (%s)";

    private ModelRunService modelRunService;
    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    public DiseaseOccurrenceHandlerHelper(ModelRunService modelRunService, DiseaseService diseaseService,
                                          DiseaseOccurrenceValidationService diseaseOccurrenceValidationService) {
        this.modelRunService = modelRunService;
        this.diseaseService = diseaseService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
    }

    /**
     * If no batch of disease occurrences has completed for this disease group, initialises the batching process by
     * setting the final weightings of all disease occurrences to null.
     * @param modelRun The model run.
     * @param diseaseGroup The model run's disease group.
     */
    @Transactional(rollbackFor = Exception.class)
    public void initialiseBatchingIfNecessary(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        if (!modelRunService.hasBatchingEverCompleted(diseaseGroup.getId())) {
            List<DiseaseOccurrence> diseaseOccurrences =
                    diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroup.getId());
            LOGGER.info(String.format(INITIAL_BATCH_LOG_MESSAGE, modelRun.getId(), diseaseOccurrences.size(),
                    diseaseGroup.getId(), diseaseGroup.getName()));

            for (DiseaseOccurrence occurrence : diseaseOccurrences) {
                occurrence.setFinalWeighting(null);
                occurrence.setFinalWeightingExcludingSpatial(null);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    /**
     * Sets validation parameters for the specified disease occurrence IDs. These IDs represent part of a batch for
     * validation.
     * @param diseaseOccurrenceIds The disease occurrence IDs.
     */
    @Transactional(rollbackFor = Exception.class)
    public void setValidationParametersForOccurrencesBatch(List<Integer> diseaseOccurrenceIds) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesById(diseaseOccurrenceIds);
        for (DiseaseOccurrence occurrence : occurrences) {
            diseaseOccurrenceValidationService.addValidationParameters(occurrence);
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }

    /**
     * Sets the batching parameters for the specified model run.
     * @param modelRun The model run.
     */
    @Transactional(rollbackFor = Exception.class)
    public void setBatchingParameters(ModelRun modelRun, int batchedOccurrenceCount) {
        // Reload the model run before setting parameters, because we are in a new transaction
        modelRun = modelRunService.getModelRunByName(modelRun.getName());
        modelRun.setBatchingCompletedDate(DateTime.now());
        modelRun.setBatchedOccurrenceCount(batchedOccurrenceCount);
        modelRunService.saveModelRun(modelRun);
    }
}
