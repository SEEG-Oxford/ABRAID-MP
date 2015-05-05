package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseProcessType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseProcessManagerTest class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseProcessManagerTest {

    @Test
    public void updateExpertsWeightingsCallsWorkflowService() throws Exception {
        // Arrange
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseProcessGatekeeper diseaseProcessGatekeeper = mock(DiseaseProcessGatekeeper.class);
        DiseaseProcessManager target = new DiseaseProcessManager(diseaseProcessGatekeeper, modelRunWorkflowService);

        // Act
        target.updateExpertsWeightings();

        // Assert
        verify(modelRunWorkflowService).updateExpertsWeightings();
    }

    @Test
    public void processOccurrencesOnDataValidatorCallsWorkflowService() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseProcessGatekeeper diseaseProcessGatekeeper = mock(DiseaseProcessGatekeeper.class);
        DiseaseProcessManager target = new DiseaseProcessManager(diseaseProcessGatekeeper, modelRunWorkflowService);

        // Act
        target.processOccurrencesOnDataValidator(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService).processOccurrencesOnDataValidator(diseaseGroupId, DiseaseProcessType.AUTOMATIC);
    }

    @Test
    public void updateDiseaseExtentsCallsWorkflowServiceIfRequired() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseProcessGatekeeper diseaseProcessGatekeeper = mock(DiseaseProcessGatekeeper.class);
        DiseaseProcessManager target = new DiseaseProcessManager(diseaseProcessGatekeeper, modelRunWorkflowService);

        when(diseaseProcessGatekeeper.extentShouldRun(diseaseGroupId)).thenReturn(true);
        // Act
        target.updateDiseaseExtents(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService).generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.AUTOMATIC);
    }

    @Test
    public void updateDiseaseExtentsSkipsCallToWorkflowServiceIfNotRequired() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseProcessGatekeeper diseaseProcessGatekeeper = mock(DiseaseProcessGatekeeper.class);
        DiseaseProcessManager target = new DiseaseProcessManager(diseaseProcessGatekeeper, modelRunWorkflowService);

        when(diseaseProcessGatekeeper.extentShouldRun(diseaseGroupId)).thenReturn(false);
        // Act
        target.updateDiseaseExtents(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService, never()).generateDiseaseExtent(anyInt(), any(DiseaseProcessType.class));
    }

    @Test
    public void requestModelRunSkipsCallToWorkflowServiceIfNotRequired() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseProcessGatekeeper diseaseProcessGatekeeper = mock(DiseaseProcessGatekeeper.class);
        DiseaseProcessManager target = new DiseaseProcessManager(diseaseProcessGatekeeper, modelRunWorkflowService);

        when(diseaseProcessGatekeeper.modelShouldRun(diseaseGroupId)).thenReturn(true);
        // Act
        target.requestModelRun(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService).prepareForAndRequestModelRun(diseaseGroupId, DiseaseProcessType.AUTOMATIC, null, null);
    }

    @Test
    public void requestModelRunCallsWorkflowServiceIfRequired() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        ModelRunWorkflowService modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        DiseaseProcessGatekeeper diseaseProcessGatekeeper = mock(DiseaseProcessGatekeeper.class);
        DiseaseProcessManager target = new DiseaseProcessManager(diseaseProcessGatekeeper, modelRunWorkflowService);

        when(diseaseProcessGatekeeper.modelShouldRun(diseaseGroupId)).thenReturn(false);
        // Act
        target.requestModelRun(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService, never()).prepareForAndRequestModelRun(anyInt(), any(DiseaseProcessType.class), any(DateTime.class), any(DateTime.class));
    }
}
