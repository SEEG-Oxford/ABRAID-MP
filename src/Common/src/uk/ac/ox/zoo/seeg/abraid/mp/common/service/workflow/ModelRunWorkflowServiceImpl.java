package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
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

    public ModelRunWorkflowServiceImpl(WeightingsCalculator weightingsCalculator,
                                       ModelRunRequester modelRunRequester,
                                       DiseaseOccurrenceReviewManager reviewManager,
                                       DiseaseService diseaseService,
                                       DiseaseExtentGenerator diseaseExtentGenerator) {
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.reviewManager = reviewManager;
        this.diseaseService = diseaseService;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
    }

    @Override
    public Map<Integer, Double> calculateExpertsWeightings() {
        return weightingsCalculator.calculateNewExpertsWeightings();
    }

    @Override
    public void prepareForModelRun(int diseaseGroupId) {
        prepareForAndRequestModelRunIfDesired(diseaseGroupId, false);
    }

    @Override
    public void prepareForAndRequestModelRun(int diseaseGroupId) {
        prepareForAndRequestModelRunIfDesired(diseaseGroupId, true);
    }

    @Override
    public void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings) {
        weightingsCalculator.saveExpertsWeightings(newExpertsWeightings);
    }

    private void prepareForAndRequestModelRunIfDesired(int diseaseGroupId, boolean requestModelRun) {
        DiseaseGroup diseaseGroup = getDiseaseGroup(diseaseGroupId);
        DateTime modelRunPrepDate = getModelRunPrepDate();
        List<DiseaseOccurrence> occurrences = updateWeightingsAndIsValidated(diseaseGroup, modelRunPrepDate);
        generateDiseaseExtent(diseaseGroupId);
        requestModelRun(diseaseGroupId, occurrences, requestModelRun);
        saveModelRunPrepDate(diseaseGroup, modelRunPrepDate);
    }

    private DiseaseGroup getDiseaseGroup(int diseaseGroupId) {
        return diseaseService.getDiseaseGroupById(diseaseGroupId);
    }

    private DateTime getModelRunPrepDate() {
        return DateTime.now();
    }

    private List<DiseaseOccurrence> updateWeightingsAndIsValidated(DiseaseGroup diseaseGroup,
                                                                   DateTime modelRunPrepDate) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        int diseaseGroupId = diseaseGroup.getId();
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);
        reviewManager.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate);
        return weightingsCalculator.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);
    }

    private void generateDiseaseExtent(int diseaseGroupId) {
        ///CHECKSTYLE:OFF MagicNumberCheck - Values for Dengue hard-coded for now
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId,
                new DiseaseExtentParameters(null, 5, 0.6, 5, 1, 2, 1, 2));
        ///CHECKSTYLE:ON
    }

    private void requestModelRun(int diseaseGroupId, List<DiseaseOccurrence> occurrences, boolean requestModelRun) {
        if (requestModelRun) {
            modelRunRequester.requestModelRun(diseaseGroupId, occurrences);
        }
    }

    private void saveModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }
}
