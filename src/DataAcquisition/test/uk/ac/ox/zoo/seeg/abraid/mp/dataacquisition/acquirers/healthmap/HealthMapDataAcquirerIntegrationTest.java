package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.JsonParserException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.ManualValidationEnforcer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private HealthMapWebService webService;
    private HealthMapDataConverter dataConverter;
    private HealthMapLookupData lookupData;
    private ManualValidationEnforcer manualValidationEnforcer;

    @Before
    public void resetMocks() {
        webService = new HealthMapWebService(mock(WebServiceClient.class));
        dataConverter = mock(HealthMapDataConverter.class);
        lookupData = mock(HealthMapLookupData.class);
        manualValidationEnforcer = mock(ManualValidationEnforcer.class);
    }

    @Test
    public void acquiresDataFromOneFile() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/acquirers/healthmap/healthmap_json_empty.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        catchException(dataAcquisition).acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, never()).convert(eq(locations), eq((DateTime) null));
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException().getCause()).isInstanceOf(IOException.class);
    }

    @Test
    public void acquiresDataFromWebServiceCallsManualValidationEnforcerAfterSuccess() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/acquirers/healthmap/healthmap_json_empty.txt";
        List<HealthMapLocation> locations = new ArrayList<>();
        Set<DiseaseOccurrence> occurrences = new HashSet<>();
        when(dataConverter.convert(eq(locations), eq((DateTime) null))).thenReturn(occurrences);
        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromFile(fileName);

        // Assert
        verify(manualValidationEnforcer).addRandomSubsetToManualValidation(same(occurrences));
    }

    @Test
    public void acquiresDataFromWebServiceDoesNotCallsManualValidationEnforcerAfterFailure() {
        // Arrange
        String fileName = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap/healthmap_json_empty.txt";
        List<HealthMapLocation> locations = new ArrayList<>();

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        catchException(dataAcquisition).acquireDataFromFile(fileName);

        // Assert
        verify(dataConverter, never()).convert(eq(locations), eq((DateTime) null)); // null occurrences

        // Assert
        verify(manualValidationEnforcer, never()).addRandomSubsetToManualValidation(anySetOf(DiseaseOccurrence.class));
    }
}
