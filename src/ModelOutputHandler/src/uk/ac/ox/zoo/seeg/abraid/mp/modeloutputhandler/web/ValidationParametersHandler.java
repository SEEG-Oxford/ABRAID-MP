package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.List;

/**
 * Handles validation parameters: if this is the first successful model run for this disease group, we need to add
 * validation parameters (e.g. environmental suitability, distance from disease extent) to each of the existing
 * disease occurrences that were fed into the model.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidationParametersHandler {
    private static final Logger LOGGER = Logger.getLogger(ValidationParametersHandler.class);
    private static final String HANDLING_LOG_MESSAGE =
            "Model run %d: adding validation parameters for %d occurrence(s) of disease group %d (%s)";
    private static final String HANDLING_COMPLETED_LOG_MESSAGE =
            "Model run %d: validation parameter handling completed";
    private static final String NO_HANDLING_LOG_MESSAGE = "Model run %d: no validation parameter handling to do";

    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    public ValidationParametersHandler(DiseaseService diseaseService,
                                       DiseaseOccurrenceValidationService diseaseOccurrenceValidationService) {
        this.diseaseService = diseaseService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
    }

    /**
     * Handles validation parameters.
     * @param modelRun The model run.
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleValidationParameters(ModelRun modelRun) {
        boolean handlingToDo = false;
        int diseaseGroupId = modelRun.getDiseaseGroupId();

        if (didModelRunComplete(modelRun)) {
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

            // If the validation process has not yet started for this disease group, start it by adding validation
            // parameters to all relevant disease occurrences
            if (!hasValidationProcessStarted(diseaseGroup)) {
                handlingToDo = true;
                addAndSaveValidationParameters(modelRun, diseaseGroup);
                updateValidationProcessStartDate(diseaseGroup);
            }
        }

        String message = handlingToDo ? HANDLING_COMPLETED_LOG_MESSAGE : NO_HANDLING_LOG_MESSAGE;
        LOGGER.info(String.format(message, modelRun.getId()));
    }

    private boolean didModelRunComplete(ModelRun modelRun) {
        return modelRun.getStatus() == ModelRunStatus.COMPLETED;
    }

    private boolean hasValidationProcessStarted(DiseaseGroup diseaseGroup) {
        return diseaseGroup.getValidationProcessStartDate() != null;
    }

    private void addAndSaveValidationParameters(ModelRun modelRun, DiseaseGroup diseaseGroup) {
        List<DiseaseOccurrence> occurrences =
                diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroup.getId());
        LOGGER.info(String.format(HANDLING_LOG_MESSAGE, modelRun.getId(), occurrences.size(), diseaseGroup.getId(),
                diseaseGroup.getName()));

        for (DiseaseOccurrence occurrence : occurrences) {
            if (diseaseOccurrenceValidationService.addValidationParameters(occurrence)) {
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    private void updateValidationProcessStartDate(DiseaseGroup diseaseGroup) {
        diseaseGroup.setValidationProcessStartDate(DateTime.now());
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }
}
