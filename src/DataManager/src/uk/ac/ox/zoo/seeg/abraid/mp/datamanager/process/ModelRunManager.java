package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import java.util.List;

/**
 * Prepares for and requests a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManager {
    private ModelRunGatekeeper modelRunGatekeeper;
    private ModelRunWorkflowService modelRunWorkflowService;
    private DiseaseService diseaseService;

    public ModelRunManager(ModelRunGatekeeper modelRunGatekeeper, ModelRunWorkflowService modelRunWorkflowService,
                           DiseaseService diseaseService) {
        this.modelRunGatekeeper = modelRunGatekeeper;
        this.modelRunWorkflowService = modelRunWorkflowService;
        this.diseaseService = diseaseService;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Integer> getDiseaseGroupIdsForAutomaticModelRuns() {
        return diseaseService.getDiseaseGroupIdsForAutomaticModelRuns();
    }

    /**
     * Prepares the model run by updating the disease extent, recalculating weightings and making the request.
     * @param diseaseGroupId The id of the disease group for which the model will be run.
     */
    @Transactional(rollbackFor = Exception.class)
    public void prepareForAndRequestModelRun(int diseaseGroupId) {
        if (modelRunGatekeeper.modelShouldRun(diseaseGroupId)) {
            modelRunWorkflowService.prepareForAndRequestAutomaticModelRun(diseaseGroupId);
        }
    }

    /**
     * Calculates and saves the new weighting for each active expert.
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateExpertsWeightings() {
        modelRunWorkflowService.updateExpertsWeightings();
    }
}
