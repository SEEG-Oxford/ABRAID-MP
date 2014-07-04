package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.*;

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
    public void prepareForAndRequestModelRun(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime modelRunPrepDate = DateTime.now();
        updateWeightingsAndIsValidated(diseaseGroup, modelRunPrepDate);
        generateDiseaseExtent(diseaseGroupId);
        modelRunRequester.requestModelRun(diseaseGroupId);
        saveModelRunPrepDate(diseaseGroup, modelRunPrepDate);
    }

    @Override
    public void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings) {
        weightingsCalculator.saveExpertsWeightings(newExpertsWeightings);
    }

    private void updateWeightingsAndIsValidated(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        int diseaseGroupId = diseaseGroup.getId();
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);
        reviewManager.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate);
        weightingsCalculator.setDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);
    }

    private void generateDiseaseExtent(int diseaseGroupId) {
        ///CHECKSTYLE:OFF MagicNumberCheck - Values for Dengue hard-coded for now
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId,
                new DiseaseExtentParameters(null, 5, 0.6, 5, 1, 2, 1, 2));
        ///CHECKSTYLE:ON
    }

    private void saveModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime modelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(modelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }
}
