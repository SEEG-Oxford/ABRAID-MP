package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseProcessType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

/**
 * Conditionally performs each step in the daily disease process.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class DiseaseProcessManager {
    private DiseaseProcessGatekeeper diseaseProcessGatekeeper;
    private ModelRunWorkflowService modelRunWorkflowService;

    public DiseaseProcessManager(
            DiseaseProcessGatekeeper diseaseProcessGatekeeper, ModelRunWorkflowService modelRunWorkflowService) {
        this.diseaseProcessGatekeeper = diseaseProcessGatekeeper;
        this.modelRunWorkflowService = modelRunWorkflowService;

    }

    /**
     * Calculates and saves the new weighting for each active expert.
     */
    public void updateExpertsWeightings() {
        modelRunWorkflowService.updateExpertsWeightings();
    }

    /**
     * Process any occurrences currently on the validator, for a given disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    public void processOccurrencesOnDataValidator(int diseaseGroupId) {
        modelRunWorkflowService.processOccurrencesOnDataValidator(diseaseGroupId, DiseaseProcessType.AUTOMATIC);
    }

    /**
     * Updates the disease extents if required, for a given disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    public void updateDiseaseExtents(int diseaseGroupId) {
        if (diseaseProcessGatekeeper.extentShouldRun(diseaseGroupId)) {
            modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.AUTOMATIC);
        }
    }

    /**
     * Requests a model run if required, for a given disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    public void requestModelRun(int diseaseGroupId) {
        if (diseaseProcessGatekeeper.modelShouldRun(diseaseGroupId)) {
            modelRunWorkflowService.prepareForAndRequestModelRun(
                    diseaseGroupId, DiseaseProcessType.AUTOMATIC, null, null);
        }
    }
}
