package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseProcessType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent.DiseaseExtentGenerator;

import java.util.List;

import static ch.lambdaj.Lambda.*;

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
    private ModelRunOccurrencesSelector modelRunOccurrencesSelector;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;
    private MachineWeightingPredictor machineWeightingPredictor;

    public ModelRunWorkflowServiceImpl(WeightingsCalculator weightingsCalculator,
                                       ModelRunRequester modelRunRequester,
                                       DiseaseOccurrenceReviewManager reviewManager,
                                       DiseaseService diseaseService,
                                       ModelRunOccurrencesSelector modelRunOccurrencesSelector,
                                       DiseaseExtentGenerator diseaseExtentGenerator,
                                       AutomaticModelRunsEnabler automaticModelRunsEnabler,
                                       MachineWeightingPredictor machineWeightingPredictor) {
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.reviewManager = reviewManager;
        this.diseaseService = diseaseService;
        this.modelRunOccurrencesSelector = modelRunOccurrencesSelector;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.automaticModelRunsEnabler = automaticModelRunsEnabler;
        this.machineWeightingPredictor = machineWeightingPredictor;
    }

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when automatically triggering one or more model runs.
     * @param diseaseGroupId The disease group ID.
     * @param processType The type of process that is being performed (auto/manual/gold).
     * @param batchStartDate The start date for batching (if validator parameter batching should happen after the model
     * run is completed), otherwise null. (Only required for DiseaseProcessType.MANUAL)
     * @param batchEndDate The end date for batching (if it should happen), otherwise null.
     *                     (Only required for DiseaseProcessType.MANUAL)
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestModelRun(int diseaseGroupId, DiseaseProcessType processType,
            DateTime batchStartDate, DateTime batchEndDate) throws ModelRunWorkflowException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime now = DateTime.now();

        // Do model run prep if this is a manual run. For automatic runs, any necessary prep will have already been done
        if (!processType.isAutomatic()) {
            updateExpertsWeightings();
            processOccurrencesOnDataValidator(diseaseGroup, processType);
            generateDiseaseExtent(diseaseGroup, processType);
        }

        requestModelRun(diseaseGroup, processType, batchStartDate, batchEndDate);
        updateLastModelRunPrepDate(diseaseGroup, now);
    }

    @Override
    public void updateExpertsWeightings() {
        weightingsCalculator.updateExpertsWeightings();
    }

    /**
     * Process any occurrences currently on the validator.
     * First updating the expert weighting of all validator occurrences, then remove the appropriate occurrences from
     * the validator (setting their final weightings in the process).
     * @param diseaseGroupId The disease group ID.
     * @param processType The type of process that is being performed (auto/manual/gold).
     */
    @Override
    public void processOccurrencesOnDataValidator(int diseaseGroupId, DiseaseProcessType processType) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        processOccurrencesOnDataValidator(diseaseGroup, processType);
    }

    private void processOccurrencesOnDataValidator(DiseaseGroup diseaseGroup, DiseaseProcessType processType) {
        // Update the expert weighting of all occurrences on the validator
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(diseaseGroup.getId());
        // Remove the appropriate occurrences from the validator
        reviewManager.updateDiseaseOccurrenceStatus(diseaseGroup.getId(), processType.isAutomatic());
        // Set their validation & final weighting in the process
        weightingsCalculator.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(diseaseGroup.getId());

        // Train the predictor using the updated knowledge of occurrences
        trainPredictor(diseaseGroup);
    }

    /**
     * Generate the disease extent for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param processType The type of process that is being performed (auto/manual/gold).
     */
    @Override
    public void generateDiseaseExtent(
            int diseaseGroupId, DiseaseProcessType processType) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        generateDiseaseExtent(diseaseGroup, processType);
    }

    private void generateDiseaseExtent(DiseaseGroup diseaseGroup, DiseaseProcessType processType) {
        DateTime minimumOccurrenceDate = getDateOfEarliestOccurrenceForExtentGeneration(diseaseGroup, processType);
        diseaseExtentGenerator.
        generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, processType);
    }

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    @Override
    public void enableAutomaticModelRuns(int diseaseGroupId) {
        updateExpertsWeightings();
        processOccurrencesOnDataValidator(diseaseGroupId, DiseaseProcessType.MANUAL);
        automaticModelRunsEnabler.enable(diseaseGroupId);
    }

    private void trainPredictor(DiseaseGroup diseaseGroup) {
        List<DiseaseOccurrence> occurrencesForTrainingPredictor =
                diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroup.getId());
        machineWeightingPredictor.train(diseaseGroup.getId(), occurrencesForTrainingPredictor);
    }

    private List<DiseaseOccurrence> getModelRunOccurrences(DiseaseGroup diseaseGroup, DiseaseProcessType processType) {
        return modelRunOccurrencesSelector.
                selectOccurrencesForModelRun(diseaseGroup.getId(), processType.isGoldStandard());
    }

    private void requestModelRun(DiseaseGroup diseaseGroup, DiseaseProcessType processType,
                                 DateTime batchStartDate, DateTime batchEndDate) {
        List<DiseaseOccurrence> modelRunOccurrences = getModelRunOccurrences(diseaseGroup, processType);
        modelRunRequester.requestModelRun(diseaseGroup.getId(), modelRunOccurrences, batchStartDate, batchEndDate);

    }

    private void updateLastModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private DateTime getDateOfEarliestOccurrenceForExtentGeneration(
            DiseaseGroup diseaseGroup, DiseaseProcessType processType) {
        if (processType.isAutomatic()) {
            // The minimum occurrence date for the disease extent is the same as the minimum occurrence date of all
            // the occurrences that can be sent to the model
            List<DiseaseOccurrence> modelRunOccurrences = getModelRunOccurrences(diseaseGroup, processType);

            if (modelRunOccurrences != null && !modelRunOccurrences.isEmpty()) {
                return min(extract(modelRunOccurrences, on(DiseaseOccurrence.class).getOccurrenceDate()));
            }
        }

        return null;
    }
}
