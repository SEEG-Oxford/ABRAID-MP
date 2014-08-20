package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
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
    private LocationService locationService;
    private DiseaseExtentGenerator diseaseExtentGenerator;

    public ModelRunWorkflowServiceImpl(WeightingsCalculator weightingsCalculator,
                                       ModelRunRequester modelRunRequester,
                                       DiseaseOccurrenceReviewManager reviewManager,
                                       DiseaseService diseaseService,
                                       LocationService locationService,
                                       DiseaseExtentGenerator diseaseExtentGenerator) {
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.reviewManager = reviewManager;
        this.diseaseService = diseaseService;
        this.locationService = locationService;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
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
        List<DiseaseOccurrence> occurrencesForModelRun = selectOccurrencesForModelRun(diseaseGroup.getId());
        DateTime minimumOccurrenceDate = extractMinimumOccurrenceDate(occurrencesForModelRun);
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);
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

    /**
     * Selects occurrences for a model run, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The occurrences to send to the model.
     */
    public List<DiseaseOccurrence> selectOccurrencesForModelRun(int diseaseGroupId) {
        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, locationService,
                diseaseGroupId);
        return selector.selectModelRunDiseaseOccurrences();
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

        // Although the set of occurrences for the model run has already been retrieved in generateDiseaseExtent,
        // they may have changed as a result of updating weightings and isValidated. So retrieve them again before
        // running the model.
        List<DiseaseOccurrence> occurrencesForModelRun = selectOccurrencesForModelRun(diseaseGroup.getId());
        modelRunRequester.requestModelRun(diseaseGroupId, occurrencesForModelRun, batchEndDate);
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
