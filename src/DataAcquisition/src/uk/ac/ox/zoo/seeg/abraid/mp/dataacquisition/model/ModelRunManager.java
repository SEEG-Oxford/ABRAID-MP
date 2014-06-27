package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Prepares for and requests a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManager {
    private static final Logger LOGGER = Logger.getLogger(ModelRunManager.class);
    private static final String DISEASE_GROUP_ID_MESSAGE = "MODEL RUN PREPARATION FOR DISEASE GROUP %d";
    private static final String STARTING_MODEL_PREP = "Starting model run preparation";
    private static final String NOT_STARTING_MODEL_PREP = "Model run preparation will not be executed";

    private ModelRunGatekeeper modelRunGatekeeper;
    private ModelRunWorkflowService modelRunWorkflowService;
    private DiseaseService diseaseService;

    public ModelRunManager(ModelRunGatekeeper modelRunGatekeeper,
                           ModelRunWorkflowService modelRunWorkflowService, DiseaseService diseaseService) {
        this.modelRunGatekeeper = modelRunGatekeeper;
        this.modelRunWorkflowService = modelRunWorkflowService;
        this.diseaseService = diseaseService;
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
        LOGGER.info(String.format(DISEASE_GROUP_ID_MESSAGE, diseaseGroupId));
        DateTime lastModelRunPrepDate = getLastModelRunPrepDate(diseaseGroupId);
        if (modelRunGatekeeper.dueToRun(lastModelRunPrepDate, diseaseGroupId)) {
            LOGGER.info(STARTING_MODEL_PREP);
            modelRunWorkflowService.prepareForAndRequestModelRun(diseaseGroupId);
        } else {
            LOGGER.info(NOT_STARTING_MODEL_PREP);
        }
    }

    /**
     * Gets the new weighting for each active expert.
     * @return A map from expert ID to the new weighting value.
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Integer, Double> prepareExpertsWeightings() {
        return modelRunWorkflowService.calculateExpertsWeightings();
    }

    /**
     * Saves the new weighting for each expert.
     * @param newExpertsWeightings The map from expert to the new weighting value.
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings) {
        modelRunWorkflowService.saveExpertsWeightings(newExpertsWeightings);
    }

    /**
     * Gets the date on which the model was last run for the specified disease group.
     * @param diseaseGroupId The ID of the disease group for which the model run is being prepared.
     * @return The date.
     */
    public DateTime getLastModelRunPrepDate(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        return diseaseGroup.getLastModelRunPrepDate();
    }
}
