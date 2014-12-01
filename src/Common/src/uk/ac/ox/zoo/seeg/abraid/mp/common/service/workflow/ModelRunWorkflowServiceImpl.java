package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

import java.util.List;

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
    private LocationService locationService;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private AutomaticModelRunsEnabler automaticModelRunsEnabler;
    private MachineWeightingPredictor machineWeightingPredictor;
    private EmailService emailService;
    private BatchDatesValidator batchDatesValidator;

    public ModelRunWorkflowServiceImpl(WeightingsCalculator weightingsCalculator,
                                       ModelRunRequester modelRunRequester,
                                       DiseaseOccurrenceReviewManager reviewManager,
                                       DiseaseService diseaseService,
                                       LocationService locationService,
                                       DiseaseExtentGenerator diseaseExtentGenerator,
                                       AutomaticModelRunsEnabler automaticModelRunsEnabler,
                                       MachineWeightingPredictor machineWeightingPredictor,
                                       EmailService emailService,
                                       BatchDatesValidator batchDatesValidator) {
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.reviewManager = reviewManager;
        this.diseaseService = diseaseService;
        this.locationService = locationService;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.automaticModelRunsEnabler = automaticModelRunsEnabler;
        this.machineWeightingPredictor = machineWeightingPredictor;
        this.emailService = emailService;
        this.batchDatesValidator = batchDatesValidator;
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
        // Ensure that the batch date range is from start of day to end of day, then validate the dates
        batchStartDate = getBatchStartDateWithMinimumTime(batchStartDate);
        batchEndDate = getBatchEndDateWithMaximumTime(batchEndDate);
        batchDatesValidator.validate(diseaseGroupId, batchStartDate, batchEndDate);

        updateExpertsWeightings();
        prepareForAndRequestModelRun(diseaseGroupId, batchStartDate, batchEndDate);
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
        prepareForAndRequestModelRun(diseaseGroupId, null, null);
    }

    /**
     * Prepares for and requests a model run using "gold standard" disease occurrences, for the specified disease group.
     * This method is designed for use during disease group set-up, when a known set of good-quality occurrences has
     * been uploaded to send to the model.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    @Override
    public void prepareForAndRequestModelRunUsingGoldStandardOccurrences(int diseaseGroupId)
            throws ModelRunWorkflowException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime modelRunPrepDate = DateTime.now();

        updateExpertsWeightings();
        generateDiseaseExtentUsingGoldStandardOccurrences(diseaseGroup);
        requestModelRunAndSaveDate(diseaseGroup, modelRunPrepDate, null, null, true);
    }

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    @Override
    public void enableAutomaticModelRuns(int diseaseGroupId) {
        automaticModelRunsEnabler.enable(diseaseGroupId);
    }

    /**
     * Calculates and saves the new weighting for each active expert.
     */
    @Override
    public void updateExpertsWeightings() {
        weightingsCalculator.updateExpertsWeightings();
    }

    /**
     * Generate the disease extent for the specified disease group.
     * @param diseaseGroup The disease group.
     */
    @Override
    public void generateDiseaseExtent(DiseaseGroup diseaseGroup) {
        DateTime minimumOccurrenceDate = null;
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            // Find the minimum occurrence date if automatic model runs are enabled (it is unused if they are disabled)
            List<DiseaseOccurrence> occurrencesForModelRun = selectOccurrencesForModelRun(diseaseGroup.getId(), false);
            minimumOccurrenceDate = extractMinimumOccurrenceDate(occurrencesForModelRun);
        }
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, false);
    }

    /**
     * Generate the disease extent for the specified disease group, only using "gold standard" disease occurrences.
     * This method is designed for use during disease group set-up, when a known set of good-quality occurrences has
     * been uploaded to send to the model.
     * @param diseaseGroup The disease group.
     */
    @Override
    public void generateDiseaseExtentUsingGoldStandardOccurrences(DiseaseGroup diseaseGroup) {
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, null, true);
    }

    private void prepareForAndRequestModelRun(int diseaseGroupId, DateTime batchStartDate, DateTime batchEndDate)
            throws ModelRunWorkflowException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime modelRunPrepDate = DateTime.now();

        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            generateDiseaseExtent(diseaseGroup);
            updateWeightingsAndStatus(diseaseGroup, modelRunPrepDate);
        } else {
            updateWeightingsAndStatus(diseaseGroup, modelRunPrepDate);
            generateDiseaseExtent(diseaseGroup);
        }

        // Although the set of occurrences for the model run has already been retrieved in generateDiseaseExtent,
        // they may have changed as a result of updating weightings and status. So retrieve them again before running
        // the model.
        requestModelRunAndSaveDate(diseaseGroup, modelRunPrepDate, batchStartDate, batchEndDate, false);
    }

    private void updateWeightingsAndStatus(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        int diseaseGroupId = diseaseGroup.getId();
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(diseaseGroupId);
        reviewManager.updateDiseaseOccurrenceStatus(diseaseGroupId, modelRunPrepDate);
        weightingsCalculator.updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(diseaseGroupId);
    }

    private void requestModelRunAndSaveDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate,
                                            DateTime batchStartDate, DateTime batchEndDate,
                                            boolean onlyUseGoldStandardOccurrences) {
        trainPredictor(diseaseGroup);
        requestModelRun(diseaseGroup, batchStartDate, batchEndDate, onlyUseGoldStandardOccurrences);
        updateLastModelRunPrepDate(diseaseGroup, modelRunPrepDate);
    }

    private void trainPredictor(DiseaseGroup diseaseGroup) {
        List<DiseaseOccurrence> occurrencesForTrainingPredictor =
                diseaseService.getDiseaseOccurrencesForTrainingPredictor(diseaseGroup.getId());
        machineWeightingPredictor.train(diseaseGroup.getId(), occurrencesForTrainingPredictor);
    }

    private void requestModelRun(DiseaseGroup diseaseGroup, DateTime batchStartDate, DateTime batchEndDate,
                                 boolean onlyUseGoldStandardOccurrences) {
        List<DiseaseOccurrence> occurrencesForModelRun =
                selectOccurrencesForModelRun(diseaseGroup.getId(), onlyUseGoldStandardOccurrences);
        modelRunRequester.requestModelRun(diseaseGroup.getId(), occurrencesForModelRun, batchStartDate, batchEndDate);
    }

    private void updateLastModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    /**
     * Selects occurrences for a model run, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" disease occurrences should be selected,
     * otherwise false.
     * @return The occurrences to send to the model.
     */
    public List<DiseaseOccurrence> selectOccurrencesForModelRun(int diseaseGroupId,
                                                                boolean onlyUseGoldStandardOccurrences) {
        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, locationService,
                emailService, diseaseGroupId, onlyUseGoldStandardOccurrences);
        return selector.selectModelRunDiseaseOccurrences();
    }

    private DateTime extractMinimumOccurrenceDate(List<DiseaseOccurrence> occurrencesForModelRun) {
        DateTime minimumOccurrenceDate = null;
        if (occurrencesForModelRun != null && occurrencesForModelRun.size() > 0) {
            // The minimum occurrence date for the disease extent is the same as the minimum occurrence date of all
            // the occurrences that can be sent to the model
            minimumOccurrenceDate = occurrencesForModelRun.get(0).getOccurrenceDate();
        }
        return minimumOccurrenceDate;
    }

    private DateTime getBatchStartDateWithMinimumTime(DateTime batchStartDate) {
        return (batchStartDate == null) ? null : batchStartDate.withTimeAtStartOfDay();
    }

    private DateTime getBatchEndDateWithMaximumTime(DateTime batchEndDate) {
        return (batchEndDate == null) ? null : batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1);
    }
}
