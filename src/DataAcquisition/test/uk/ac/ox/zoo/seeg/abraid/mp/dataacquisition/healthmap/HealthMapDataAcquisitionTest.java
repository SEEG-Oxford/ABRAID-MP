package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapDataAcquisition class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataAcquisitionTest {
    private HealthMapWebService webService = mock(HealthMapWebService.class);
    private HealthMapDataConverter dataConverter = mock(HealthMapDataConverter.class);
    private HealthMapLookupData lookupData = mock(HealthMapLookupData.class);

    @Test
    public void acquiresDataFromWebServiceOnFirstRunWithDefaultStartDateSet() {
        // Arrange
        fixCurrentDateTime();
        DateTime defaultStartDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();
        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(defaultStartDate);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(defaultStartDate), eq(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), eq(endDate));
    }

    @Test
    public void acquiresDataFromWebServiceOnFirstRunWithDefaultStartDateDaysBeforeNowSet() {
        // Arrange
        fixCurrentDateTime();
        int defaultStartDateDaysBeforeNow = 3;
        DateTime startDate = DateTime.now().minusDays(3);
        DateTime endDate = DateTime.now();

        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(null);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(defaultStartDateDaysBeforeNow);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), eq(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), eq(endDate));
    }

    @Test
    public void acquiresDataFromWebServiceOnFirstRunWithDefaultStartDateAndDefaultStartDateDaysBeforeNowSet() {
        // Arrange
        fixCurrentDateTime();
        DateTime defaultStartDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();
        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(defaultStartDate);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(3);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(defaultStartDate), eq(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), eq(endDate));
    }

    @Test
    public void webServiceDoesNotDuplicateDataIfAlreadyRun() {
        // Arrange
        fixCurrentDateTime();
        DateTime startDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime defaultStartDate = new DateTime("2006-02-01T01:02:03+0000");
        int defaultStartDateDaysBeforeNow = 3;
        DateTime endDate = DateTime.now();
        Provenance provenance = new Provenance();
        provenance.setLastRetrievalEndDate(startDate);
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(defaultStartDate);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(defaultStartDateDaysBeforeNow);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), eq(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), eq(endDate));
    }

    @Test
    public void acquiresDataFromWebServiceWithEndDateDaysAfterStartDateSet() {
        // Arrange
        DateTime startDate = new DateTime("2004-02-01T01:02:03+0000");
        int endDateDaysAfterStartDate = 3;
        DateTime endDate = startDate.plusDays(endDateDaysAfterStartDate);

        Provenance provenance = new Provenance();
        provenance.setLastRetrievalEndDate(startDate);
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(null);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(null);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(endDateDaysAfterStartDate);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), eq(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), eq(endDate));
    }

    @Test
    public void acquiresDataFromWebServiceWithEndDateDaysAfterStartDateSetButBeyondNow() {
        // Arrange
        fixCurrentDateTime();
        DateTime startDate = new DateTime("2100-02-01T01:02:03+0000");
        int endDateDaysAfterStartDate = 3;
        DateTime endDate = DateTime.now();

        Provenance provenance = new Provenance();
        provenance.setLastRetrievalEndDate(startDate);
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(null);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(null);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(endDateDaysAfterStartDate);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), eq(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), eq(endDate));
    }

    @Test
    public void doesNotAcquireDataIfWebServiceRequestFails() {
        // Arrange
        fixCurrentDateTime();
        DateTime startDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();

        Provenance provenance = new Provenance();
        provenance.setLastRetrievalEndDate(startDate);

        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), eq(endDate))).thenThrow(new WebServiceClientException(""));
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        //noinspection unchecked
        verify(dataConverter, never()).convert(anyList(), any(DateTime.class));
    }

    private void fixCurrentDateTime() {
        // This ensures that DateTime.now() always returns a particular date/time, so that equality comparisons work
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }
}
