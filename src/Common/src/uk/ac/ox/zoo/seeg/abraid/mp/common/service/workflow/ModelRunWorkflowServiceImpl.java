package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.List;
import java.util.Map;

/**
 * Service class to support the workflow surrounding a model run request.
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ModelRunWorkflowServiceImpl implements ModelRunWorkflowService {
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;
    private DiseaseOccurrenceReviewManager reviewManager;
    private DiseaseService diseaseService;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;

    public ModelRunWorkflowServiceImpl(WeightingsCalculator weightingsCalculator,
                                       ModelRunRequester modelRunRequester,
                                       DiseaseOccurrenceReviewManager reviewManager,
                                       DiseaseService diseaseService,
                                       DiseaseExtentGenerator diseaseExtentGenerator,
                                       DiseaseOccurrenceValidationService diseaseOccurrenceValidationService) {
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.reviewManager = reviewManager;
        this.diseaseService = diseaseService;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
    }

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when manually triggering a model run.
     * @param diseaseGroupId The disease group ID.
     * @param batchEndDate If validator parameter batching should happen after the model run is completed,
     * this is the end date for batching.
     * @throws ModelRunRequesterException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestManuallyTriggeredModelRun(int diseaseGroupId, DateTime batchEndDate)
            throws ModelRunRequesterException {
        Map<Integer, Double> newExpertWeightings = calculateExpertsWeightings();
        prepareForAndRequestModelRun(diseaseGroupId, batchEndDate, true);
        saveExpertsWeightings(newExpertWeightings);
    }

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when automatically triggering one or more model runs.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunRequesterException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestAutomaticModelRun(int diseaseGroupId)
            throws ModelRunRequesterException {
        prepareForAndRequestModelRun(diseaseGroupId, null, false);
    }


    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    @Override
    public void enableAutomaticModelRuns(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime now = DateTime.now();
        saveAutomaticModelRunsStartDate(diseaseGroup, now);
        setAdminUnitDiseaseExtentClassChangedDate(diseaseGroupId, now);
        addValidationParameters(diseaseGroupId);
    }

    private void saveAutomaticModelRunsStartDate(DiseaseGroup diseaseGroup, DateTime now) {
        diseaseGroup.setAutomaticModelRunsStartDate(now);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private void setAdminUnitDiseaseExtentClassChangedDate(int diseaseGroupId, DateTime now) {
        List<AdminUnitDiseaseExtentClass> extentClasses =
            diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);
        for (AdminUnitDiseaseExtentClass extentClass : extentClasses) {
            extentClass.setClassChangedDate(now);
            diseaseService.saveAdminUnitDiseaseExtentClass(extentClass);
        }
    }

    private void addValidationParameters(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences =
            diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId, false);
        for (DiseaseOccurrence occurrence : occurrences) {
            diseaseOccurrenceValidationService.addValidationParameters(occurrence);
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }

    /**
     * Gets the new weighting for each active expert.
     * @return A map from expert ID to the new weighting value.
     */
    @Override
    public Map<Integer, Double> calculateExpertsWeightings() {
        return weightingsCalculator.calculateNewExpertsWeightings();
    }

    /**
     * Saves the new weighting for each expert.
     * @param newExpertsWeightings The map from expert to the new weighting value.
     */
    @Override
    public void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings) {
        weightingsCalculator.saveExpertsWeightings(newExpertsWeightings);
    }

    /**
     * Generate the disease extent for the specified disease group.
     * @param diseaseGroup The disease group.
     */
    @Override
    public void generateDiseaseExtent(DiseaseGroup diseaseGroup) {
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup);
    }

    private void prepareForAndRequestModelRun(int diseaseGroupId, DateTime batchEndDate,
                                              boolean alwaysRemoveFromValidator)
            throws ModelRunRequesterException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime modelRunPrepDate = DateTime.now();
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            generateDiseaseExtent(diseaseGroup);
            updateWeightingsAndIsValidated(diseaseGroup, modelRunPrepDate, alwaysRemoveFromValidator);
        } else {
            updateWeightingsAndIsValidated(diseaseGroup, modelRunPrepDate, alwaysRemoveFromValidator);
            generateDiseaseExtent(diseaseGroup);
        }
        modelRunRequester.requestModelRun(diseaseGroupId, batchEndDate);
        saveModelRunPrepDate(diseaseGroup, modelRunPrepDate);
    }

    private void updateWeightingsAndIsValidated(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate,
                                                boolean alwaysRemoveFromValidator) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        int diseaseGroupId = diseaseGroup.getId();
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);
        reviewManager.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate,
                alwaysRemoveFromValidator);
        weightingsCalculator.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);
    }

    private void saveModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }
}
