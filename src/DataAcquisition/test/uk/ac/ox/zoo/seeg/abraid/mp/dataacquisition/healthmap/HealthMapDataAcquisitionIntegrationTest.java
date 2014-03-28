package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Integration tests for the HealthMapDataAcquisition class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataAcquisitionIntegrationTest {
    private HealthMapWebService webService = new HealthMapWebService(new WebServiceClient());
    private HealthMapDataConverter dataConverter = mock(HealthMapDataConverter.class);
    private HealthMapLookupData lookupData = mock(HealthMapLookupData.class);

    @Test
    public void acquiresDataFromOneFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap/healthmap_json_empty.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, times(1)).convert(eq(locations), eq((DateTime) null));
    }

    @Test
    public void doesNotAcquireDataFromInvalidFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap/healthmap_json_invalid.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, never()).convert(eq(locations), eq((DateTime) null));
    }

    @Test
    public void doesNotAcquireDataFromNonExistentFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap/does_not_exist.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, never()).convert(eq(locations), eq((DateTime) null));
    }
}
