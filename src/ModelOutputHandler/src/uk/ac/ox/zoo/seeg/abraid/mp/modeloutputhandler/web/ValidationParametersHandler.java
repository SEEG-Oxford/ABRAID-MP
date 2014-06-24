package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseOccurrenceValidationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.List;

/**
 * Handles validation parameters: if this is the first successful model run for this disease group, we need to add
 * validation parameters (e.g. environmental suitability, distance from disease extent) to each of the existing
 * disease occurrences that were fed into the model.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ValidationParametersHandler {
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
        int diseaseGroupId = modelRun.getDiseaseGroupId();

        if (didModelRunComplete(modelRun)) {
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

            // If the validation process has not yet started for this disease group, start it by adding validation
            // parameters to all relevant disease occurrences
            if (!hasValidationProcessStarted(diseaseGroup)) {
                addAndSaveValidationParameters(diseaseGroupId);
                updateValidationProcessStartDate(diseaseGroup);
            }
        }
    }

    private boolean didModelRunComplete(ModelRun modelRun) {
        return modelRun.getStatus() == ModelRunStatus.COMPLETED;
    }

    private boolean hasValidationProcessStarted(DiseaseGroup diseaseGroup) {
        return diseaseGroup.getValidationProcessStartDate() != null;
    }

    private void addAndSaveValidationParameters(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);

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
