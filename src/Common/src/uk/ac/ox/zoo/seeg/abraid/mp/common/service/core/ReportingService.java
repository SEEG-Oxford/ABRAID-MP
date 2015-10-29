package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;

import java.util.List;

/**
 * Service interface for aggregate report data.
 * Copyright (c) 2015 University of Oxford
 */
public interface ReportingService {
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
