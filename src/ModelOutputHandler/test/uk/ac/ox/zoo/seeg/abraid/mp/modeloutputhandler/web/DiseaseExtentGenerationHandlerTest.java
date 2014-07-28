package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import static org.mockito.Mockito.*;


/**
 * Tests the DiseaseExtentGenerationHandler class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerationHandlerTest {

    private static final String TEST_MODEL_RUN_NAME = "deng_2014-05-16-13-28-57_482ae3ca-ab30-414d-acce-388baae7d83c";
    private static final int TEST_MODEL_RUN_DISEASE_GROUP_ID = 87;

    @Test
    public void handleGeneratesDiseaseExtent() throws Exception {
        // Arrange
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseExtentGenerationHandler handler = new DiseaseExtentGenerationHandler(modelRunWorkflowService);
        ModelRun modelRun = new ModelRun(TEST_MODEL_RUN_NAME, TEST_MODEL_RUN_DISEASE_GROUP_ID, DateTime.now());

        // Act
        handler.handle(modelRun);

        // Assert
        verify(modelRunWorkflowService, times(1)).generateDiseaseExtent(TEST_MODEL_RUN_DISEASE_GROUP_ID);
    }
}
