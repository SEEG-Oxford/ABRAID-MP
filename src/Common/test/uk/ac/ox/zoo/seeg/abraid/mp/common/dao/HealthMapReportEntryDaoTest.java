package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HealthMapReportEntryDao class.
  * Copyright (c) 2015 University of Oxford
 */
public class HealthMapReportEntryDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private HealthMapReportEntryDao dao;

    @Test
    public void getDiseaseReportEntries() throws Exception {
        // Arrange
        String month = DateTime.now().toString("YYYY-MM");

        // Act
        List<HealthMapReportEntry> entries = dao.getHealthMapDiseaseReportEntries();

        // Assert
        assertThat(entries).containsOnly(
                new HealthMapReportEntry(month, "Cholera", 0L, 3L, 0L, 0L, 0L, 2L, 0L, 0L),
                new HealthMapReportEntry(month, "Dengue", 18L, 4L, 5L, 18L, 11L, 4L, 5L, 18L),
                new HealthMapReportEntry(month, "Poliomyelitis", 2L, 3L, 1L, 3L, 2L, 1L, 1L, 3L)
        );
    }

    @Test
    public void getCountryReportEntries() throws Exception {
        // Arrange
        String month = DateTime.now().toString("YYYY-MM");

        // Act
        List<HealthMapReportEntry> entries = dao.getHealthMapCountryReportEntries();

        // Assert
        assertThat(entries).containsOnly(
                new HealthMapReportEntry(month, "Bolivia",                  0L, 3L, 0L, 0L, 0L, 2L, 0L, 0L),
                new HealthMapReportEntry(month, "Brazil",                   1L, 2L, 3L, 6L, 1L, 2L, 3L, 6L),
                new HealthMapReportEntry(month, "Colombia",                 0L, 0L, 0L, 1L, 0L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Cook Islands",             2L, 0L, 0L, 0L, 1L, 0L, 0L, 0L),
                new HealthMapReportEntry(month, "Fiji",                     2L, 0L, 0L, 2L, 1L, 0L, 0L, 2L),
                new HealthMapReportEntry(month, "India",                    0L, 0L, 0L, 1L, 0L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Indonesia",                1L, 0L, 0L, 1L, 1L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Malaysia",                 4L, 0L, 0L, 1L, 1L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Mexico",                   1L, 1L, 0L, 1L, 1L, 1L, 0L, 1L),
                new HealthMapReportEntry(month, "Pakistan",                 2L, 0L, 1L, 2L, 1L, 0L, 1L, 2L),
                new HealthMapReportEntry(month, "Paraguay",                 1L, 0L, 0L, 0L, 1L, 0L, 0L, 0L),
                new HealthMapReportEntry(month, "Peru",                     0L, 0L, 0L, 1L, 0L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Philippines",              2L, 1L, 2L, 2L, 1L, 1L, 2L, 2L),
                new HealthMapReportEntry(month, "Saudi Arabia",             1L, 0L, 0L, 1L, 1L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Singapore",                0L, 0L, 0L, 1L, 0L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Somalia",                  1L, 0L, 0L, 1L, 1L, 0L, 0L, 1L),
                new HealthMapReportEntry(month, "Thailand",                 2L, 0L, 0L, 0L, 1L, 0L, 0L, 0L),
                new HealthMapReportEntry(month, "United States of America", 0L, 3L, 0L, 0L, 0L, 1L, 0L, 0L)
        );
    }
}
