package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry;

import java.util.List;

/**
 * Data Access Object to access the aggregate data needed for HealthMap data rate reports.
 * Copyright (c) 2015 University of Oxford
 */
@Repository
public class HealthMapReportEntryDaoImpl implements HealthMapReportEntryDao {
    private static final String BASE_HEALTH_MAP_REPORT_QUERY =
            "select new uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapReportEntry( " +
            "    concat(year(o.createdDate),'-',month(o.createdDate)), " +
            "    qualifier, " +
            "    count(distinct case when o.location.precision='COUNTRY' then o.id else null end), " +
            "    count(distinct case when o.location.precision='ADMIN1' then o.id else null end), " +
            "    count(distinct case when o.location.precision='ADMIN2' then o.id else null end), " +
            "    count(distinct case when o.location.precision='PRECISE' then o.id else null end)," +
            "    count(distinct case when o.location.precision='COUNTRY' then o.location.id else null end), " +
            "    count(distinct case when o.location.precision='ADMIN1' then o.location.id else null end), " +
            "    count(distinct case when o.location.precision='ADMIN2' then o.location.id else null end), " +
            "    count(distinct case when o.location.precision='PRECISE' then o.location.id else null end)" +
            ") from DiseaseOccurrence as o " +
            "where " +
            "    o.alert.feed.provenance.name='HealthMap' and " +
            "    o.location.hasPassedQc is TRUE " +
            "group by " +
            "    concat(year(o.createdDate),'-',month(o.createdDate)), " +
            "    qualifier";

    private final SessionFactory sessionFactory;

    public HealthMapReportEntryDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Get the data for the disease qualified HealthMap data rate reports.
     * @return The data for a disease qualified HealthMap data rate report.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<HealthMapReportEntry> getHealthMapDiseaseReportEntries() {
        return sessionFactory.getCurrentSession().createQuery(
                BASE_HEALTH_MAP_REPORT_QUERY.replaceAll("qualifier", "o.diseaseGroup.name")
        ).list();
    }

    /**
     * Get the data for the country qualified HealthMap data rate reports.
     * @return The data for a country qualified HealthMap data rate report.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<HealthMapReportEntry> getHealthMapCountryReportEntries() {
        return sessionFactory.getCurrentSession().createQuery(
                BASE_HEALTH_MAP_REPORT_QUERY.replaceAll("qualifier", "o.location.country.name")
        ).list();
    }
}
