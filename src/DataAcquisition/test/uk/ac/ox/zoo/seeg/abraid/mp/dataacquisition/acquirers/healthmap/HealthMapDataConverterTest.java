package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DiseaseOccurrenceDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapDataConverter class.
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataConverterTest {
    private AlertService alertService;
    private HealthMapLocationConverter locationConverter;
    private HealthMapAlertConverter alertConverter;
    @SuppressWarnings("FieldCanBeLocal")
    private HealthMapLookupData healthMapLookupData;
    private HealthMapDataConverter healthMapDataConverter;
    private DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer;

    private Provenance healthMapProvenance;

    @Before
    public void setUp() {
        alertService = mock(AlertService.class);
        locationConverter = mock(HealthMapLocationConverter.class);
        alertConverter = mock(HealthMapAlertConverter.class);
        healthMapLookupData = mock(HealthMapLookupData.class);
        diseaseOccurrenceDataAcquirer = mock(DiseaseOccurrenceDataAcquirer.class);
        healthMapDataConverter = new HealthMapDataConverter(locationConverter, alertConverter,
                alertService, healthMapLookupData,
                diseaseOccurrenceDataAcquirer);

        healthMapProvenance = new Provenance();
        when(healthMapLookupData.getHealthMapProvenance()).thenReturn(healthMapProvenance);
    }

    @Test
    public void convertNoLocations() {
        // Arrange
        List<HealthMapLocation> locations = new ArrayList<>();
        DateTime retrievalEndDate = DateTime.now();

        // Act
        healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertTwoSuccessfulLocationsEachWithTwoSuccessfulAlerts() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapLocation2 is successfully converted into location2
        Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence(1);
        mockConvertAndAcquire(healthMapAlert1, location1, diseaseOccurrence1, true);

        // healthMapAlert2 is successfully converted into diseaseOccurrence2
        DiseaseOccurrence diseaseOccurrence2 = new DiseaseOccurrence(2);
        mockConvertAndAcquire(healthMapAlert2, location1, diseaseOccurrence2, true);

        // healthMapAlert3 is successfully converted into diseaseOccurrence3
        DiseaseOccurrence diseaseOccurrence3 = new DiseaseOccurrence(3);
        mockConvertAndAcquire(healthMapAlert3, location2, diseaseOccurrence3, true);

        // healthMapAlert4 is successfully converted into diseaseOccurrence4
        DiseaseOccurrence diseaseOccurrence4 = new DiseaseOccurrence(4);
        mockConvertAndAcquire(healthMapAlert4, location2, diseaseOccurrence4, true);

        // Act
        Set<DiseaseOccurrence> actualOccurrences = healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertSavedOccurrences(actualOccurrences, diseaseOccurrence1, diseaseOccurrence2, diseaseOccurrence3,
                diseaseOccurrence4);
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertTwoSuccessfulLocationsEachWithOneSuccessfulOneFailedAlerts() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapLocation2 is successfully converted into location2
        Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence(1);
        mockConvertAndAcquire(healthMapAlert1, location1, diseaseOccurrence1, true);

        // healthMapAlert2 is successfully converted into diseaseOccurrence2
        DiseaseOccurrence diseaseOccurrence2 = new DiseaseOccurrence(2);
        mockConvertAndAcquire(healthMapAlert2, location1, diseaseOccurrence2, false);

        // healthMapAlert3 is successfully converted into diseaseOccurrence3
        DiseaseOccurrence diseaseOccurrence3 = new DiseaseOccurrence(3);
        mockConvertAndAcquire(healthMapAlert3, location2, diseaseOccurrence3, false);

        // healthMapAlert4 is successfully converted into diseaseOccurrence4
        DiseaseOccurrence diseaseOccurrence4 = new DiseaseOccurrence(4);
        mockConvertAndAcquire(healthMapAlert4, location2, diseaseOccurrence4, true);

        // Act
        Set<DiseaseOccurrence> actualOccurrences = healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertSavedOccurrences(actualOccurrences, diseaseOccurrence1, diseaseOccurrence4);
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertFirstLocationFails() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is not successfully converted into location1
        when(locationConverter.convert(healthMapLocation1)).thenReturn(null);

        // healthMapLocation2 is successfully converted into location2
        Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);

        // healthMapAlert3 is successfully converted into diseaseOccurrence3
        DiseaseOccurrence diseaseOccurrence3 = new DiseaseOccurrence(3);
        mockConvertAndAcquire(healthMapAlert3, location2, diseaseOccurrence3, true);

        // healthMapAlert4 is successfully converted into diseaseOccurrence4
        DiseaseOccurrence diseaseOccurrence4 = new DiseaseOccurrence(4);
        mockConvertAndAcquire(healthMapAlert4, location2, diseaseOccurrence4, true);

        // Act
        Set<DiseaseOccurrence> actualOccurrences = healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertSavedOccurrences(actualOccurrences, diseaseOccurrence3, diseaseOccurrence4);
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertFirstLocationSucceedsSecondLocationFails() {
        // Arrange
        // Create 2 locations each with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapLocation2 is not successfully converted into location2
        when(locationConverter.convert(healthMapLocation2)).thenReturn(null);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence(1);
        mockConvertAndAcquire(healthMapAlert1, location1, diseaseOccurrence1, true);

        // healthMapAlert2 is successfully converted into diseaseOccurrence2
        DiseaseOccurrence diseaseOccurrence2 = new DiseaseOccurrence(2);
        mockConvertAndAcquire(healthMapAlert2, location1, diseaseOccurrence2, true);

        // Act
        Set<DiseaseOccurrence> actualOccurrences = healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertSavedOccurrences(actualOccurrences, diseaseOccurrence1, diseaseOccurrence2);
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertNoAlerts() {
        // Arrange
        // Create 2 locations each with no alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapLocation2 is successfully converted into location2
        Location location2 = new Location();
        when(locationConverter.convert(healthMapLocation2)).thenReturn(location2);

        // Act
        Set<DiseaseOccurrence> actualOccurrences = healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertThat(actualOccurrences).isEmpty();
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    @Test
    public void convertFirstAlertFailsSecondAlertSucceeds() {
        // Arrange
        // Create a location with 2 alerts
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1);

        DateTime retrievalEndDate = DateTime.now();

        // healthMapLocation1 is successfully converted into location1
        Location location1 = new Location();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);

        // healthMapAlert1 is successfully converted into diseaseOccurrence1
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        mockConvertAndAcquire(healthMapAlert1, location1, diseaseOccurrence1, true);

        // healthMapAlert2 is not successfully converted into diseaseOccurrence2
        when(alertConverter.convert(healthMapAlert2, location1)).thenReturn(null);

        // Act
        Set<DiseaseOccurrence> actualOccurrences = healthMapDataConverter.convert(locations, retrievalEndDate);

        // Assert
        assertSavedOccurrences(actualOccurrences, diseaseOccurrence1);
        verifyWriteLastRetrievalEndDate(retrievalEndDate);
    }

    private void mockConvertAndAcquire(HealthMapAlert healthMapAlert, Location location,
                                       DiseaseOccurrence diseaseOccurrence, boolean isOccurrenceSaved) {
        when(alertConverter.convert(same(healthMapAlert), same(location))).thenReturn(diseaseOccurrence);
        when(diseaseOccurrenceDataAcquirer.acquire(same(diseaseOccurrence))).thenReturn(isOccurrenceSaved);
    }

    // Ensure that the saved occurrences are as expected.
    // NB - this only works as expected if the disease occurrences have different content (e.g. the IDs are different)
    private void assertSavedOccurrences(Set<DiseaseOccurrence> actualOccurrences,
                                        DiseaseOccurrence... expectedOccurrences) {
        assertThat(actualOccurrences.size()).isEqualTo(expectedOccurrences.length);
        assertThat(actualOccurrences).containsAll(Arrays.asList(expectedOccurrences));
    }

    private void verifyWriteLastRetrievalEndDate(DateTime retrievalEndDate) {
        assertThat(healthMapProvenance.getLastRetrievalEndDate()).isEqualTo(retrievalEndDate);
        verify(alertService, times(1)).saveProvenance(same(healthMapProvenance));
    }
}
