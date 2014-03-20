package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.argThat;
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
    public void firstRunDefaultStartDateSet() {
        // Arrange
        DateTime defaultStartDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();
        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(defaultStartDate);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(defaultStartDate), approx(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireData();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), approx(endDate));
    }

    @Test
    public void firstRunDefaultStartDateDaysBeforeNowSet() {
        // Arrange
        int defaultStartDateDaysBeforeNow = 3;
        DateTime startDate = DateTime.now().minusDays(3);
        DateTime endDate = DateTime.now();

        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(null);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(defaultStartDateDaysBeforeNow);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(approx(startDate), approx(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireData();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), approx(endDate));
    }

    @Test
    public void firstRunDefaultStartDateAndDefaultStartDateDaysBeforeNowSet() {
        // Arrange
        DateTime defaultStartDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();
        Provenance provenance = new Provenance();
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(defaultStartDate);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(3);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(defaultStartDate), approx(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireData();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), approx(endDate));
    }

    @Test
    public void alreadyRun() {
        // Arrange
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
        when(webService.sendRequest(eq(startDate), approx(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireData();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), approx(endDate));
    }

    @Test
    public void endDateDaysAfterStartDateSet() {
        // Arrange
        DateTime startDate = new DateTime("2004-02-01T01:02:03+0000");

        int endDateDaysAfterStartDate = 3;
        DateTime endDate = DateTime.now().plusDays(endDateDaysAfterStartDate);

        Provenance provenance = new Provenance();
        provenance.setLastRetrievalEndDate(startDate);
        List<HealthMapLocation> locations = new ArrayList<>();

        when(webService.getDefaultStartDate()).thenReturn(null);
        when(webService.getDefaultStartDateDaysBeforeNow()).thenReturn(null);
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(endDateDaysAfterStartDate);
        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), approx(endDate))).thenReturn(locations);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireData();

        // Assert
        verify(dataConverter, times(1)).convert(same(locations), approx(endDate));
    }

    @Test
    public void webServiceRequestFailed() {
        // Arrange
        DateTime startDate = new DateTime("2004-02-01T01:02:03+0000");
        DateTime endDate = DateTime.now();

        Provenance provenance = new Provenance();
        provenance.setLastRetrievalEndDate(startDate);

        when(lookupData.getHealthMapProvenance()).thenReturn(provenance);
        when(webService.sendRequest(eq(startDate), approx(endDate))).thenThrow(new WebServiceClientException(""));
        when(webService.getEndDateDaysAfterStartDate()).thenReturn(null);

        // Act
        HealthMapDataAcquisition dataAcquisition = new HealthMapDataAcquisition(webService, dataConverter, lookupData);
        dataAcquisition.acquireData();

        // Assert
        //noinspection unchecked
        verify(dataConverter, never()).convert(anyList(), any(DateTime.class));
    }

    private DateTime approx(DateTime date) {
        return argThat(new ApproximatelyMatches(date));
    }

    // Matcher that compares two dates for approximate equality, for use with dates that rely on the current time
    public class ApproximatelyMatches extends ArgumentMatcher<DateTime> {
        private DateTime comparisonDate;
        private static final long TOLERANCE_MILLISECONDS = 1000;

        public ApproximatelyMatches(DateTime comparisonDate) {
            this.comparisonDate = comparisonDate;
        }

        public boolean matches(Object date) {
            if (comparisonDate == null && date == null) {
                return true;
            }

            if (comparisonDate == null || date == null) {
                return false;
            }

            long differenceInMillis = new Duration(comparisonDate, (DateTime)date).getMillis();
            return differenceInMillis < TOLERANCE_MILLISECONDS;
        }
    }
}
