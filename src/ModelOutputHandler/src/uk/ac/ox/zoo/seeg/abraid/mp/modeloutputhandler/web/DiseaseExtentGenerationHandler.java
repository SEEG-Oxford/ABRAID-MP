package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

/**
 * Handles the disease extent generation tasks that happens following a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerationHandler {

    private ModelRunWorkflowService modelRunWorkflowService;

    public DiseaseExtentGenerationHandler(ModelRunWorkflowService modelRunWorkflowService) {
        this.modelRunWorkflowService = modelRunWorkflowService;
    }

    /**
     * Generates the disease extent following a model run.
     * @param modelRun The current model run (just completed).
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(ModelRun modelRun) {
        int diseaseGroupId = modelRun.getDiseaseGroupId();
        modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId);
    }
}
