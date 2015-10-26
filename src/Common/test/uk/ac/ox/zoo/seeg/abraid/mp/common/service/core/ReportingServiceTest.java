package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.HealthMapReportEntryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the ReportingService class.
 * Copyright (c) 2015 University of Oxford
 */
public class ReportingServiceTest {
    @Test
    public void getHealthMapDiseaseReportEntries() throws Exception {
        // Arrange
        HealthMapReportEntryDao dao = mock(HealthMapReportEntryDao.class);
        List<HealthMapReportEntry> expected = Arrays.asList(mock(HealthMapReportEntry.class));
        when(dao.getHealthMapDiseaseReportEntries()).thenReturn(expected);
        ReportingServiceImpl target = new ReportingServiceImpl(dao);

        // Act
        List<HealthMapReportEntry> result = target.getHealthMapDiseaseReportEntries();

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getHealthMapCountryReportEntries() throws Exception {
        // Arrange
        HealthMapReportEntryDao dao = mock(HealthMapReportEntryDao.class);
        List<HealthMapReportEntry> expected = Arrays.asList(mock(HealthMapReportEntry.class));
        when(dao.getHealthMapCountryReportEntries()).thenReturn(expected);
        ReportingServiceImpl target = new ReportingServiceImpl(dao);

        // Act
        List<HealthMapReportEntry> result = target.getHealthMapCountryReportEntries();

        // Assert
        assertThat(result).isEqualTo(expected);
    }
}
