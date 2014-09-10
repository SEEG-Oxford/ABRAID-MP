package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapDataAcquisition;

import static org.mockito.Mockito.*;

/**
 * Tests the DataAcquisitionService class.
 * Copyright (c) 2014 University of Oxford
 */
public class DataAcquisitionServiceTest {
    private DataAcquisitionService service;
    private HealthMapDataAcquisition healthMapDataAcquisition;

    @Before
    public void setUp() {
        healthMapDataAcquisition = mock(HealthMapDataAcquisition.class);
        service = new DataAcquisitionServiceImpl(healthMapDataAcquisition);
    }

    @Test
    public void acquireHealthMapDataFromWebService() {
        service.acquireHealthMapDataFromWebService();
        verify(healthMapDataAcquisition, times(1)).acquireDataFromWebService();
    }

    @Test
    public void acquireHealthMapDataFromFile() {
        String fileName = "test.json";
        service.acquireHealthMapDataFromFile(fileName);
        verify(healthMapDataAcquisition, times(1)).acquireDataFromFile(eq(fileName));
    }
}
