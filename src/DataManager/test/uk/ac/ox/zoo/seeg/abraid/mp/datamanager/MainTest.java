package uk.ac.ox.zoo.seeg.abraid.mp.datamanager;

import org.junit.Test;
import org.mockito.InOrder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process.DataAcquisitionManager;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process.DiseaseProcessManager;

import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Unit tests the Main class.
 *
 * Copyright (c) 2015 University of Oxford
 */
public class MainTest {
    @Test
    public void runMainCallsCorrectSequenceOfSteps() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        DataAcquisitionManager dataAcquisitionManager = mock(DataAcquisitionManager.class);
        DiseaseProcessManager processManager = mock(DiseaseProcessManager.class);
        String[] args = new String[] {"a", "b", "c"};
        when(diseaseService.getDiseaseGroupIdsForAutomaticModelRuns()).thenReturn(Arrays.asList(3, 6, 21));
        InOrder inOrder = inOrder(dataAcquisitionManager, processManager);

        Main target = new Main(diseaseService, dataAcquisitionManager, processManager, "version");

        // Act
        target.runMain(args);

        // Assert
        // 1
        inOrder.verify(processManager).updateExpertsWeightings();
        // 2
        inOrder.verify(processManager).processOccurrencesOnDataValidator(3);
        inOrder.verify(processManager).processOccurrencesOnDataValidator(6);
        inOrder.verify(processManager).processOccurrencesOnDataValidator(21);
        // 3
        inOrder.verify(dataAcquisitionManager).runDataAcquisition(args);
        // 4
        inOrder.verify(processManager).updateDiseaseExtents(3);
        inOrder.verify(processManager).updateDiseaseExtents(6);
        inOrder.verify(processManager).updateDiseaseExtents(21);
        // 5
        inOrder.verify(processManager).requestModelRun(3);
        inOrder.verify(processManager).requestModelRun(6);
        inOrder.verify(processManager).requestModelRun(21);
    }

    @Test
    public void runMainContinuesThroughFailedStepsButSkipsModelRunForFailedExtent() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        DataAcquisitionManager dataAcquisitionManager = mock(DataAcquisitionManager.class);
        DiseaseProcessManager processManager = mock(DiseaseProcessManager.class);
        String[] args = new String[] {"a", "b", "c"};
        when(diseaseService.getDiseaseGroupIdsForAutomaticModelRuns()).thenReturn(Arrays.asList(3, 6, 21));
        doThrow(new RuntimeException()).when(processManager).updateExpertsWeightings();
        doThrow(new RuntimeException()).when(dataAcquisitionManager).runDataAcquisition(args);
        doThrow(new RuntimeException()).when(processManager).processOccurrencesOnDataValidator(3);
        doThrow(new RuntimeException()).when(processManager).updateDiseaseExtents(6);
        doThrow(new RuntimeException()).when(processManager).requestModelRun(3);
        InOrder inOrder = inOrder(dataAcquisitionManager, processManager);

        Main target = new Main(diseaseService, dataAcquisitionManager, processManager, "version");

        // Act
        target.runMain(args);

        // Assert
        // 1
        inOrder.verify(processManager).updateExpertsWeightings();
        // 2
        inOrder.verify(processManager).processOccurrencesOnDataValidator(3);
        inOrder.verify(processManager).processOccurrencesOnDataValidator(6);
        inOrder.verify(processManager).processOccurrencesOnDataValidator(21);
        // 3
        inOrder.verify(dataAcquisitionManager).runDataAcquisition(args);
        // 4
        inOrder.verify(processManager).updateDiseaseExtents(3);
        inOrder.verify(processManager).updateDiseaseExtents(6);
        inOrder.verify(processManager).updateDiseaseExtents(21);
        // 5
        inOrder.verify(processManager).requestModelRun(3);
        verify(processManager, never()).requestModelRun(6); // Skipped due to exception during updateDiseaseExtents(6)
        inOrder.verify(processManager).requestModelRun(21);
    }
}
