package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.HealthMapReportEntryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;

import java.util.List;

/**
 * Service for for aggregate report data.
 * Copyright (c) 2015 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ReportingServiceImpl implements ReportingService {
    private HealthMapReportEntryDao healthMapReportEntryDao;

    public ReportingServiceImpl(HealthMapReportEntryDao healthMapReportEntryDao) {
        this.healthMapReportEntryDao = healthMapReportEntryDao;
    }

    @Override
    public List<HealthMapReportEntry> getHealthMapDiseaseReportEntries() {
        return healthMapReportEntryDao.getHealthMapDiseaseReportEntries();
    }

    @Override
    public List<HealthMapReportEntry> getHealthMapCountryReportEntries() {
        return healthMapReportEntryDao.getHealthMapCountryReportEntries();
    }
}
