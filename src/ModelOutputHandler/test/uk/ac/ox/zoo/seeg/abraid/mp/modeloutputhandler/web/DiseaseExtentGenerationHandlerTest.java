package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import static org.mockito.Mockito.*;


/**
 * Tests the DiseaseExtentGenerationHandler class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerationHandlerTest {

    private static final String TEST_MODEL_RUN_NAME = "deng_2014-05-16-13-28-57_482ae3ca-ab30-414d-acce-388baae7d83c";
    private static final int DISEASE_GROUP_ID = 87;

    @Test
    public void handleGeneratesDiseaseExtentIfAutomaticModelRunsEnabled() throws Exception {
        // Arrange
        DiseaseGroup diseaseGroup = new DiseaseGroup(DISEASE_GROUP_ID);
        diseaseGroup.setAutomaticModelRuns(true);

        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseService diseaseService = mockDiseaseService(DISEASE_GROUP_ID, diseaseGroup);

        DiseaseExtentGenerationHandler handler =
                new DiseaseExtentGenerationHandler(modelRunWorkflowService, diseaseService);
        ModelRun modelRun = new ModelRun(TEST_MODEL_RUN_NAME, DISEASE_GROUP_ID, DateTime.now());

        // Act
        handler.handle(modelRun);

        // Assert
        verify(modelRunWorkflowService, times(1)).generateDiseaseExtent(diseaseGroup);
    }

    private DiseaseService mockDiseaseService(int id, DiseaseGroup diseaseGroup) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(id)).thenReturn(diseaseGroup);
        return diseaseService;
    }

    @Test
    public void handleDoesNotGenerateDiseaseExtentIfAutomaticModelRunsNotEnabled() throws Exception {
        // Arrange
        DiseaseGroup diseaseGroup = new DiseaseGroup(DISEASE_GROUP_ID);
        diseaseGroup.setAutomaticModelRuns(false);

        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseService diseaseService = mockDiseaseService(DISEASE_GROUP_ID, diseaseGroup);

        DiseaseExtentGenerationHandler handler =
                new DiseaseExtentGenerationHandler(modelRunWorkflowService, diseaseService);
        ModelRun modelRun = new ModelRun(TEST_MODEL_RUN_NAME, DISEASE_GROUP_ID, DateTime.now());

        // Act
        handler.handle(modelRun);

        // Assert
        verify(modelRunWorkflowService, times(0)).generateDiseaseExtent(diseaseGroup);
    }
}
