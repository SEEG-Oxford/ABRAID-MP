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
            "    concat(" +
            "        year(o.createdDate)," +
            "        '-', " +
            "        case when month(o.createdDate)<10 then '0' else '' end, " +
            "        month(o.createdDate)" +
            "    ), " +
            "    qualifier.name, " +
            "    sum(case when location.precision='COUNTRY' then 1 else 0 end), " +
            "    sum(case when location.precision='ADMIN1' then 1 else 0 end), " +
            "    sum(case when location.precision='ADMIN2' then 1 else 0 end), " +
            "    sum(case when location.precision='PRECISE' then 1 else 0 end)," +
            "    count(distinct case when location.precision='COUNTRY' then location.id else null end), " +
            "    count(distinct case when location.precision='ADMIN1' then location.id else null end), " +
            "    count(distinct case when location.precision='ADMIN2' then location.id else null end), " +
            "    count(distinct case when location.precision='PRECISE' then location.id else null end)" +
            ") " +
            "from DiseaseOccurrence as o " +
            "    inner join o.location location" +
            "    inner join o.alert alert " +
            "    inner join alert.feed feed " +
            "    inner join feed.provenance provenance " +
            "    inner join o.diseaseGroup diseaseGroup " +
            "    inner join location.country country " +
            "where " +
            "    provenance.name='HealthMap' and " +
            "    location.hasPassedQc is TRUE and " +
            "    diseaseGroup.isPriorityDisease is TRUE " +
            "group by " +
            "    year(o.createdDate)," +
            "    month(o.createdDate), " +
            "    qualifier.id";

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
                BASE_HEALTH_MAP_REPORT_QUERY.replaceAll("qualifier", "diseaseGroup")
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
                BASE_HEALTH_MAP_REPORT_QUERY.replaceAll("qualifier", "country")
        ).list();
    }
}
