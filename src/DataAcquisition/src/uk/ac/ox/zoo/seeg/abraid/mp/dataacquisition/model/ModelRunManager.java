package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentParameters;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings.WeightingsCalculator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Prepares the model run by updating the disease extent and recalculating weightings.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManager {
    private static Logger logger = Logger.getLogger(ModelRunManager.class);
    private static final String DISEASE_GROUP_ID_MESSAGE = "MODEL RUN PREPARATION FOR DISEASE GROUP %d";
    private static final String STARTING_MODEL_PREP = "Starting model run preparation";
    private static final String NOT_STARTING_MODEL_PREP = "Model run preparation will not be executed";
    private static final String AUTOMATIC_RUNS_NOT_ENABLED = "Skipping model run preparation and request for disease group %d - Automatic model runs are not enabled";

    private ModelRunGatekeeper modelRunGatekeeper;
    private LastModelRunPrepDateManager lastModelRunPrepDateManager;
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;
    private ModelRunManagerHelper helper;

    public ModelRunManager(ModelRunGatekeeper modelRunGatekeeper,
                           LastModelRunPrepDateManager lastModelRunPrepDateManager,
                           DiseaseExtentGenerator diseaseExtentGenerator,
                           WeightingsCalculator weightingsCalculator,
                           ModelRunRequester modelRunRequester,
                           ModelRunManagerHelper helper) {
        this.modelRunGatekeeper = modelRunGatekeeper;
        this.lastModelRunPrepDateManager = lastModelRunPrepDateManager;
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
        this.helper = helper;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Integer> getDiseaseGroupsWithOccurrences() {
        return Arrays.asList(87); ///CHECKSTYLE:SUPPRESS MagicNumberCheck - only Dengue hard-coded for now
    }

    /**
     * Prepares the model run by updating the disease extent, recalculating weightings and making the request.
     * @param diseaseGroupId The id of the disease group for which the model will be run.
     */
    @Transactional(rollbackFor = Exception.class)
    public void prepareForAndRequestModelRun(int diseaseGroupId) {
        if (modelRunGatekeeper.automaticModelRunsEnabled(diseaseGroupId)) {
            logger.info(String.format(DISEASE_GROUP_ID_MESSAGE, diseaseGroupId));
            DateTime lastModelRunPrepDate = lastModelRunPrepDateManager.getDate(diseaseGroupId);
            if (modelRunGatekeeper.dueToRun(lastModelRunPrepDate, diseaseGroupId)) {
                DateTime modelRunPrepDate = DateTime.now();
                logger.info(STARTING_MODEL_PREP);
                List<DiseaseOccurrence> occurrencesForModelRunRequest =
                        updateWeightingsAndIsValidated(lastModelRunPrepDate, modelRunPrepDate, diseaseGroupId);
                generateDiseaseExtent(diseaseGroupId);
                modelRunRequester.requestModelRun(diseaseGroupId, occurrencesForModelRunRequest);
                lastModelRunPrepDateManager.saveDate(modelRunPrepDate, diseaseGroupId);
            } else {
                logger.info(NOT_STARTING_MODEL_PREP);
            }
        } else {
            logger.info(String.format(AUTOMATIC_RUNS_NOT_ENABLED, diseaseGroupId));
        }
    }

    private List<DiseaseOccurrence> updateWeightingsAndIsValidated(DateTime lastModelRunPrepDate,
                                                                   DateTime modelRunPrepDate, int diseaseGroupId) {
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);
        helper.updateDiseaseOccurrenceIsValidatedValues(diseaseGroupId, modelRunPrepDate);
        return weightingsCalculator.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);
    }

    private void generateDiseaseExtent(int diseaseGroupId) {
        ///CHECKSTYLE:OFF MagicNumberCheck - Values for Dengue hard-coded for now
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId,
                new DiseaseExtentParameters(null, 5, 0.6, 5, 1, 2, 1, 2));
        ///CHECKSTYLE:ON
    }

    /**
     * Gets the new weighting for each active expert.
     * @return A map from expert ID to the new weighting value.
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Integer, Double> prepareExpertsWeightings() {
        return weightingsCalculator.calculateNewExpertsWeightings();
    }

    /**
     * Saves the new weighting for each expert.
     * @param newExpertsWeightings The map from expert to the new weighting value.
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings) {
        weightingsCalculator.saveExpertsWeightings(newExpertsWeightings);
    }
}
