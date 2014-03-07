package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapDataConverter class.
 * 
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataConverterTest {
    private AlertService alertService;
    private DiseaseService diseaseService;
    private HealthMapLocationConverter locationConverter;
    private HealthMapAlertConverter alertConverter;
    private HealthMapLookupData healthMapLookupData;
    private HealthMapDataConverter healthMapDataConverter;

    private Provenance healthMapProvenance;

    @Before
    public void setUp() {
        alertService = mock(AlertService.class);
        diseaseService = mock(DiseaseService.class);
        locationConverter = mock(HealthMapLocationConverter.class);
        alertConverter = mock(HealthMapAlertConverter.class);
        healthMapLookupData = mock(HealthMapLookupData.class);
        healthMapDataConverter = new HealthMapDataConverter(locationConverter, alertConverter,
                alertService, diseaseService, healthMapLookupData);

        healthMapProvenance = new Provenance();
        when(healthMapLookupData.getHealthMapProvenance()).thenReturn(healthMapProvenance);
    }

    @Test
    public void convertNoLocations() {
        // Arrange
        List<HealthMapLocation> locations = new ArrayList<>();
        Date retrievalDate = Calendar.getInstance().getTime();

        // Act
        healthMapDataConverter.convert(locations, retrievalDate);

        // Assert
        verifyWriteLastRetrievalDate(retrievalDate);
    }

    @Test
    public void convertTwoSuccessfulLocationsEachWithTwoSuccessfulAlerts() {
        // Arrange
        HealthMapLocation healthMapLocation1 = new HealthMapLocation();
        HealthMapLocation healthMapLocation2 = new HealthMapLocation();
        HealthMapAlert healthMapAlert1 = new HealthMapAlert();
        HealthMapAlert healthMapAlert2 = new HealthMapAlert();
        HealthMapAlert healthMapAlert3 = new HealthMapAlert();
        HealthMapAlert healthMapAlert4 = new HealthMapAlert();
        healthMapLocation1.setAlerts(Arrays.asList(healthMapAlert1, healthMapAlert2));
        healthMapLocation2.setAlerts(Arrays.asList(healthMapAlert3, healthMapAlert4));
        List<HealthMapLocation> locations = Arrays.asList(healthMapLocation1, healthMapLocation2);

        Date retrievalDate = Calendar.getInstance().getTime();

        final Location location1 = new Location();
        DiseaseOccurrence diseaseOccurrence1 = new DiseaseOccurrence();
        when(locationConverter.convert(healthMapLocation1)).thenReturn(location1);
        when(alertConverter.convert(healthMapAlert1, location1)).thenReturn(diseaseOccurrence1);
        mockAddPrecision(healthMapLocation1, location1);
        // TODO - finish off

        // Act
        healthMapDataConverter.convert(locations, retrievalDate);

        // Assert
    }

    @Test
    public void convertFirstLocationFails() {

    }

    @Test
    public void convertFirstLocationSucceedsSecondLocationFails() {

    }

    @Test
    public void convertNoAlerts() {

    }

    @Test
    public void convertFirstAlertFailsSecondAlertSucceeds() {

    }

    @Test
    public void convertLocationContinuationFails() {

    }

    private void verifyWriteLastRetrievalDate(Date retrievalDate) {
        assertThat(healthMapProvenance.getLastRetrievedDate()).isEqualTo(retrievalDate);
        verify(alertService, times(1)).saveProvenance(refEq(healthMapProvenance));
    }

    private void mockAddPrecision(HealthMapLocation healthMapLocation1, final Location location1) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                location1.setPrecision(LocationPrecision.COUNTRY);
                return null;
            }
        }).when(locationConverter).addPrecision(healthMapLocation1, location1);
    }
}
