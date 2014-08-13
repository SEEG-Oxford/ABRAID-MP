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
     * @return A list of occurrences that will be sent to the model (if relevant). This is selected as input to the
     * disease extent generation process.
     */
    @Override
    public List<DiseaseOccurrence> generateDiseaseExtent(DiseaseGroup diseaseGroup) {
        List<DiseaseOccurrence> occurrencesForModelRun = selectOccurrencesForModelRun(diseaseGroup.getId());
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, occurrencesForModelRun);
        return occurrencesForModelRun;
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

        List<DiseaseOccurrence> occurrencesForModelRun;
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            occurrencesForModelRun = generateDiseaseExtent(diseaseGroup);
            updateWeightingsAndIsValidated(diseaseGroup, modelRunPrepDate, alwaysRemoveFromValidator);
        } else {
            updateWeightingsAndIsValidated(diseaseGroup, modelRunPrepDate, alwaysRemoveFromValidator);
            occurrencesForModelRun = generateDiseaseExtent(diseaseGroup);
        }

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
