package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

/**
 * Handles the disease extent generation tasks that happens following a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerationHandler {
    private static final Logger LOGGER = Logger.getLogger(DiseaseExtentGenerationHandler.class);
    private static final String STARTING_LOG_MESSAGE =
            "Model run %d: generating disease extent for disease group %d (%s)";
    private static final String NO_GENERATION_LOG_MESSAGE = "Model run %d: no disease extent generation to do";
    private static final String COMPLETED_LOG_MESSAGE = "Model run %d: generating disease extent completed";

    private ModelRunWorkflowService modelRunWorkflowService;
    private DiseaseService diseaseService;

    public DiseaseExtentGenerationHandler(ModelRunWorkflowService modelRunWorkflowService,
                                          DiseaseService diseaseService) {
        this.modelRunWorkflowService = modelRunWorkflowService;
        this.diseaseService = diseaseService;
    }

    /**
     * Generates the disease extent following an automatically triggered model run.
     * @param modelRun The current model run (just completed).
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(ModelRun modelRun) {
        int diseaseGroupId = modelRun.getDiseaseGroupId();
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            LOGGER.info(String.format(STARTING_LOG_MESSAGE, modelRun.getId(), diseaseGroupId,
                    diseaseGroup.getName()));
            modelRunWorkflowService.generateDiseaseExtent(diseaseGroup);
            LOGGER.info(String.format(COMPLETED_LOG_MESSAGE, modelRun.getId()));
        } else {
            LOGGER.info(String.format(NO_GENERATION_LOG_MESSAGE, modelRun.getId()));
        }
    }
}
