package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.ManualValidationEnforcer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapDataAcquirer class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDataAcquirerTest {
    private HealthMapWebService webService;
    private HealthMapDataConverter dataConverter;
    private HealthMapLookupData lookupData;
    private ManualValidationEnforcer manualValidationEnforcer;

    @Before
    public void resetMocks() {
        webService = mock(HealthMapWebService.class);
        dataConverter = mock(HealthMapDataConverter.class);
        lookupData = mock(HealthMapLookupData.class);
        manualValidationEnforcer = mock(ManualValidationEnforcer.class);
    }

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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
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
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        catchException(dataAcquisition).acquireDataFromWebService();

        // Assert
        //noinspection unchecked
        verify(dataConverter, never()).convert(anyList(), any(DateTime.class));
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException().getCause()).isInstanceOf(WebServiceClientException.class);
    }

    @Test
    public void acquiresDataFromWebServiceCallsManualValidationEnforcerAfterSuccess() {
        // Arrange
        fixCurrentDateTime();
        DateTime defaultStartDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();
        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();
        Set<DiseaseOccurrence> occurrences = new HashSet<>();

        when(webService.getDefaultStartDate()).thenReturn(defaultStartDate);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(defaultStartDate), eq(endDate))).thenReturn(locations);
        when(dataConverter.convert(same(locations), eq(endDate))).thenReturn(occurrences);

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService();

        // Assert
        verify(dataConverter).convert(same(locations), eq(endDate));
        verify(manualValidationEnforcer).addRandomSubsetToManualValidation(same(occurrences));
    }

    @Test
    public void acquiresDataFromWebServiceDoesNotCallsManualValidationEnforcerAfterFailure() {
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
        when(dataConverter.convert(same(locations), eq(endDate))).thenReturn(null);

        // Act
        HealthMapDataAcquirer dataAcquisition = new HealthMapDataAcquirer(webService, dataConverter, lookupData, manualValidationEnforcer);
        dataAcquisition.acquireDataFromWebService(); // null occurrences

        // Assert
        verify(manualValidationEnforcer, never()).addRandomSubsetToManualValidation(anySetOf(DiseaseOccurrence.class));
    }

    private void fixCurrentDateTime() {
        // This ensures that DateTime.now() always returns a particular date/time, so that equality comparisons work
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }
}
