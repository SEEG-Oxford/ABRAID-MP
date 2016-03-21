package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;

import java.util.List;

/**
 * Interface for the Data Access Object to access the aggregate data needed for HealthMap data rate reports.
 * Copyright (c) 2015 University of Oxford
 */
public interface HealthMapReportEntryDao {
    /**
     * Get the data for the disease qualified HealthMap data rate reports.
     * @return The data for a disease qualified HealthMap data rate report.
     */
    List<HealthMapReportEntry> getHealthMapDiseaseReportEntries();

    /**
     * Get the data for the country qualified HealthMap data rate reports.
     * @return The data for a country qualified HealthMap data rate report.
     */
    List<HealthMapReportEntry> getHealthMapCountryReportEntries();
}
