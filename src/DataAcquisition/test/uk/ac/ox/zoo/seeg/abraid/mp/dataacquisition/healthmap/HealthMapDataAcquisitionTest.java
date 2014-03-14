package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.*;

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
        Date defaultStartDate = getDate(2004, 2, 1, 1, 2, 3);
        Date endDate = Calendar.getInstance().getTime();
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);

        Date startDate = calendar.getTime();
        Date endDate = Calendar.getInstance().getTime();

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
        Date defaultStartDate = getDate(2004, 2, 1, 1, 2, 3);
        Date endDate = Calendar.getInstance().getTime();
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
        Date startDate = getDate(2004, 2, 1, 1, 2, 3);
        Date defaultStartDate = getDate(2006, 2, 1, 1, 2, 3);
        int defaultStartDateDaysBeforeNow = 3;
        Date endDate = Calendar.getInstance().getTime();
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
        Date startDate = getDate(2004, 2, 1, 1, 2, 3);

        int endDateDaysAfterStartDate = 3;
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(startDate);
        endCalendar.add(Calendar.DAY_OF_MONTH, endDateDaysAfterStartDate);
        Date endDate = endCalendar.getTime();

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
        Date startDate = getDate(2004, 2, 1, 1, 2, 3);
        Date endDate = Calendar.getInstance().getTime();

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
        verify(dataConverter, never()).convert(anyList(), any(Date.class));
    }

    private Date getDate(int year, int month, int date, int hourOfDay, int minute, int second) {
        // The date's timezone is UTC (i.e. +0000)
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
        //noinspection MagicConstant
        calendar.set(year, month - 1, date, hourOfDay, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date approx(Date date) {
        return argThat(new ApproximatelyMatches(date));
    }

    // Matcher that compares two dates for approximate equality, for use with dates that rely on the current time
    public class ApproximatelyMatches extends ArgumentMatcher<Date> {
        private Date comparisonDate;
        private static final long TOLERANCE_MILLISECONDS = 1000;

        public ApproximatelyMatches(Date comparisonDate) {
            this.comparisonDate = comparisonDate;
        }

        public boolean matches(Object date) {
            if (comparisonDate == null && date == null) {
                return true;
            }

            if (comparisonDate == null || date == null) {
                return false;
            }

            long differenceInMillis = Math.abs(comparisonDate.getTime() - ((Date)date).getTime());
            return differenceInMillis < TOLERANCE_MILLISECONDS;
        }
    }
}
