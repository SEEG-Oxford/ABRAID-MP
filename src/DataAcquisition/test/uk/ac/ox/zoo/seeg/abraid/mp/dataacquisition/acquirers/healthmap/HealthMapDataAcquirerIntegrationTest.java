package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.ManualValidationEnforcer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the HealthMapDataAcquirer class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataAcquirerIntegrationTest {
    private HealthMapWebService webService = new HealthMapWebService(new WebServiceClient());
    private HealthMapDataConverter dataConverter = mock(HealthMapDataConverter.class);
    private HealthMapLookupData lookupData = mock(HealthMapLookupData.class);

    @Test
    public void acquiresDataFromOneFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/acquirers/healthmap/healthmap_json_empty.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, mock(ManualValidationEnforcer.class));
        dataAcquisition.acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter).convert(eq(locations), eq((DateTime) null));
    }

    @Test
    public void doesNotAcquireDataFromInvalidFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/acquirers/healthmap/healthmap_json_invalid.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, mock(ManualValidationEnforcer.class));
        catchException(dataAcquisition).acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, never()).convert(eq(locations), eq((DateTime) null));
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException().getCause()).isInstanceOf(JsonParserException.class);
    }

    @Test
    public void doesNotAcquireDataFromNonExistentFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap/does_not_exist.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, mock(ManualValidationEnforcer.class));
        catchException(dataAcquisition).acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, never()).convert(eq(locations), eq((DateTime) null));
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException().getCause()).isInstanceOf(IOException.class);
    }
}
