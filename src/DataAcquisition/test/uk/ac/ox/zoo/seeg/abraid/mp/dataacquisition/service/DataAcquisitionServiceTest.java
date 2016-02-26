package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.CsvDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.HealthMapDataAcquirer;

import static org.mockito.Mockito.*;

/**
 * Tests the DataAcquisitionService class.
 * Copyright (c) 2014 University of Oxford
 */
public class DataAcquisitionServiceTest {
    private DataAcquisitionService service;
    private HealthMapDataAcquirer healthMapDataAcquirer;
    private CsvDataAcquirer csvDataAcquirer;

    @Before
    public void setUp() {
        healthMapDataAcquirer = mock(HealthMapDataAcquirer.class);
        csvDataAcquirer = mock(CsvDataAcquirer.class);
        service = new DataAcquisitionServiceImpl(healthMapDataAcquirer, csvDataAcquirer);
    }

    @Test
    public void acquireHealthMapDataFromWebService() {
        service.acquireHealthMapDataFromWebService();
        verify(healthMapDataAcquirer).acquireDataFromWebService();
    }

    @Test
    public void acquireHealthMapDataFromFile() {
        String fileName = "test.json";
        service.acquireHealthMapDataFromFile(fileName);
        verify(healthMapDataAcquirer).acquireDataFromFile(eq(fileName));
    }

    @Test
    public void acquireCsvData() {
        byte[] csv = "1, 2, 3".getBytes();
        service.acquireCsvData(csv, false, true, null);
        verify(csvDataAcquirer).acquireDataFromCsv(eq(csv), eq(false), eq(true), (DiseaseGroup) isNull());
    }

    @Test
    public void acquireCsvBiasData() {
        byte[] csv = "1, 2, 3".getBytes();
        DiseaseGroup disease = mock(DiseaseGroup.class);
        service.acquireCsvData(csv, true, false, disease);
        verify(csvDataAcquirer).acquireDataFromCsv(eq(csv), eq(true), eq(false), eq(disease));
    }
}
