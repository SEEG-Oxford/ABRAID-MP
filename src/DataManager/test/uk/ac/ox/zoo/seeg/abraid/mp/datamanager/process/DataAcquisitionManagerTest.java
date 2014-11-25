package uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service.DataAcquisitionService;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the DataAcquisitionManager class.
 * Copyright (c) 2014 University of Oxford
 */
public class DataAcquisitionManagerTest {
    private DataAcquisitionManager manager;
    private DataAcquisitionService dataAcquisitionService;

    @Before
    public void setUp() {
        dataAcquisitionService = mock(DataAcquisitionService.class);
        manager = new DataAcquisitionManager(dataAcquisitionService);
    }

    @Test
    public void runDataAcquisitionAcquiresFromWebServiceIfNullFileNamesList() {
        manager.runDataAcquisition(null);
        verify(dataAcquisitionService).acquireHealthMapDataFromWebService();
    }

    @Test
    public void runDataAcquisitionAcquiresFromWebServiceIfEmptyFileNamesList() {
        manager.runDataAcquisition(new String[] {});
        verify(dataAcquisitionService).acquireHealthMapDataFromWebService();
    }

    @Test
    public void runDataAcquisitionAcquiresFromEachSpecifiedFile() {
        String fileName1 = "test.json";
        String fileName2 = "test2.json";
        manager.runDataAcquisition(new String[] {fileName1, fileName2});
        verify(dataAcquisitionService).acquireHealthMapDataFromFile(eq(fileName1));
        verify(dataAcquisitionService).acquireHealthMapDataFromFile(eq(fileName2));
    }
}
