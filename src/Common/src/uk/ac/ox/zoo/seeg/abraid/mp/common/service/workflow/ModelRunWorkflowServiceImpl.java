package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

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
    private BatchDatesValidator batchDatesValidator;

    public ModelRunWorkflowServiceImpl(WeightingsCalculator weightingsCalculator,
                                       ModelRunRequester modelRunRequester,
                                       DiseaseOccurrenceReviewManager reviewManager,
                                       DiseaseService diseaseService,
                                       ModelRunOccurrencesSelector modelRunOccurrencesSelector,
                                       DiseaseExtentGenerator diseaseExtentGenerator,
                                       AutomaticModelRunsEnabler automaticModelRunsEnabler,
                                       MachineWeightingPredictor machineWeightingPredictor,
                                       BatchDatesValidator batchDatesValidator) {
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.reviewManager = reviewManager;
        this.diseaseService = diseaseService;
        this.modelRunOccurrencesSelector = modelRunOccurrencesSelector;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.automaticModelRunsEnabler = automaticModelRunsEnabler;
        this.machineWeightingPredictor = machineWeightingPredictor;
        this.batchDatesValidator = batchDatesValidator;
    }

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when automatically triggering one or more model runs.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestAutomaticModelRun(int diseaseGroupId)
            throws ModelRunWorkflowException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

        processOccurrencesOnDataValidator(diseaseGroup, true);
        generateDiseaseExtent(diseaseGroup, true, false);

        requestModelRunAndSaveDate(diseaseGroup, null, null, false);
    }

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when manually triggering a model run.
     * @param diseaseGroupId The disease group ID.
     * @param batchStartDate The start date for batching (if validator parameter batching should happen after the model
     * run is completed), otherwise null.
     * @param batchEndDate The end date for batching (if it should happen), otherwise null.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestManuallyTriggeredModelRun(
            int diseaseGroupId, DateTime batchStartDate, DateTime batchEndDate) throws ModelRunWorkflowException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

        // Ensure that the batch date range is from start of day to end of day, then validate the dates
        batchStartDate = getBatchStartDateWithMinimumTime(batchStartDate);
        batchEndDate = getBatchEndDateWithMaximumTime(batchEndDate);
        batchDatesValidator.validate(diseaseGroupId, batchStartDate, batchEndDate);

        updateExpertsWeightings();
        processOccurrencesOnDataValidator(diseaseGroup, false);
        generateDiseaseExtent(diseaseGroup, false, false);

        requestModelRunAndSaveDate(diseaseGroup, batchStartDate, batchEndDate, false);
    }

    /**
     * Prepares for and requests a model run using "gold standard" disease occurrences, for the specified disease group.
     * This method is designed for use during disease group set-up, when a known set of good-quality occurrences has
     * been uploaded to send to the model. Experts' weightings are updated here - across all disease groups - to ensure
     * their most up-to-date values are used in disease extent generation.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestModelRunUsingGoldStandardOccurrences(int diseaseGroupId)
            throws ModelRunWorkflowException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

        updateExpertsWeightings();
        generateDiseaseExtent(diseaseGroup, false, true);

        requestModelRunAndSaveDate(diseaseGroup, null, null, true);
    }

    @Override
    public void updateExpertsWeightings() {
        weightingsCalculator.updateExpertsWeightings();
    }

    /**
     * Process any occurrences currently on the validator.
     * First updating the expert weighting of all validator occurrences, then remove the appropriate occurrences from
     * the validator (setting their final weightings in the process).
     * @param diseaseGroup The disease group to be processed.
     * @param isAutomaticProcess If this is part of the automated daily process or for a manual model run.
     */
    @Override
    public void processOccurrencesOnDataValidator(DiseaseGroup diseaseGroup, boolean isAutomaticProcess) {
        // Update the expert weighting of all occurrences on the validator, then remove the appropriate occurrences
        // from the validator. Setting their validation & final weighting in the process.
        updateOccurrenceWeightingsAndStatus(diseaseGroup, isAutomaticProcess);
        // Train the predictor using the updated knowledge of occurrences
        trainPredictor(diseaseGroup);
    }

    /**
     * Generate the disease extent for the specified disease group.
     * @param diseaseGroup The disease group.
     * @param isAutomaticProcess If this is part of the automated daily process or for a manual model run.
     * @param useOnlyGoldStandard If only gold standard occurrences should be used for extent generation (manual only).
     */
    @Override
    public void generateDiseaseExtent(
            DiseaseGroup diseaseGroup, boolean isAutomaticProcess, boolean useOnlyGoldStandard) {
        DateTime minimumOccurrenceDate = null;
        if (isAutomaticProcess) {
            // Find the minimum occurrence date if automatic model runs are enabled (it is unused if they are disabled)
            List<DiseaseOccurrence> occurrencesForModelRun = modelRunOccurrencesSelector.selectOccurrencesForModelRun(
                    diseaseGroup.getId(), false);
            minimumOccurrenceDate = extractMinimumOccurrenceDate(occurrencesForModelRun);
        }
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, useOnlyGoldStandard);
    }

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    @Override
    public void enableAutomaticModelRuns(int diseaseGroupId) {
        automaticModelRunsEnabler.enable(diseaseGroupId);
    }

    private void updateOccurrenceWeightingsAndStatus(DiseaseGroup diseaseGroup, boolean isAutomaticProcess) {
        int diseaseGroupId = diseaseGroup.getId();
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(diseaseGroupId);
        reviewManager.updateDiseaseOccurrenceStatus(diseaseGroupId, isAutomaticProcess);
        weightingsCalculator.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(diseaseGroupId);
    }

    private void trainPredictor(DiseaseGroup diseaseGroup) {
        List<DiseaseOccurrence> occurrencesForTrainingPredictor =
                diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroup.getId());
        machineWeightingPredictor.train(diseaseGroup.getId(), occurrencesForTrainingPredictor);
    }

    private void requestModelRunAndSaveDate(DiseaseGroup diseaseGroup,
                                            DateTime batchStartDate, DateTime batchEndDate,
                                            boolean onlyUseGoldStandardOccurrences) {
        requestModelRun(diseaseGroup, batchStartDate, batchEndDate, onlyUseGoldStandardOccurrences);
        updateLastModelRunPrepDate(diseaseGroup, DateTime.now());
    }

    private void requestModelRun(DiseaseGroup diseaseGroup, DateTime batchStartDate, DateTime batchEndDate,
                                 boolean onlyUseGoldStandardOccurrences) {
        List<DiseaseOccurrence> occurrencesForModelRun = modelRunOccurrencesSelector.selectOccurrencesForModelRun(
                diseaseGroup.getId(), onlyUseGoldStandardOccurrences);
        modelRunRequester.requestModelRun(diseaseGroup.getId(), occurrencesForModelRun, batchStartDate, batchEndDate);
    }

    private void updateLastModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private DateTime extractMinimumOccurrenceDate(List<DiseaseOccurrence> occurrencesForModelRun) {
        if (occurrencesForModelRun == null || occurrencesForModelRun.isEmpty()) {
            return null;
        }

        // The minimum occurrence date for the disease extent is the same as the minimum occurrence date of all
        // the occurrences that can be sent to the model
        return min(extract(occurrencesForModelRun, on(DiseaseOccurrence.class).getOccurrenceDate()));
    }

    private DateTime getBatchStartDateWithMinimumTime(DateTime batchStartDate) {
        return (batchStartDate == null) ? null : batchStartDate.withTimeAtStartOfDay();
    }

    private DateTime getBatchEndDateWithMaximumTime(DateTime batchEndDate) {
        return (batchEndDate == null) ? null : batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1);
    }
}
