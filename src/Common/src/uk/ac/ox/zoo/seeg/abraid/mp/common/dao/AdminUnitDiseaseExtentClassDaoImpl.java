package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.List;

/**
 * The AdminUnitDiseaseExtentClass entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDiseaseExtentClassDaoImpl extends AbstractDao<AdminUnitDiseaseExtentClass, Integer> implements
        AdminUnitDiseaseExtentClassDao {
    private static final String GLOBAL = "Global";
    private static final String TROPICAL = "Tropical";
    private static final String LATEST_OCCURRENCES_QUERY =
            "select latestValidatorOccurrences from AdminUnitDiseaseExtentClass a " +
            "where a.diseaseGroup.id=:diseaseGroupId " +
            "and a.adminUnit%s.gaulCode=:gaulCode";
    public AdminUnitDiseaseExtentClassDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets the latest disease extent class change date for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The latest change date.
     */
    @Override
    public DateTime getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(Integer diseaseGroupId) {
        Query query = getParameterisedNamedQuery("getLatestDiseaseExtentClassChangeDateByDiseaseGroupId",
                "diseaseGroupId", diseaseGroupId);
        return (DateTime) query.uniqueResult();
    }

    /**
     * Gets the list of most recent disease occurrences on the admin unit disease extent class (defined by disease group
     * id and admin unit gaul code).
     * @param diseaseGroupId The id of the disease group the admin unit disease extent class represents.
     * @param isGlobal True if the disease group is considered global, false if considered tropical.
     * @param gaulCode The gaul code the admin unit disease extent class represents.
     * @return The list of latest disease occurrences for the given admin unit disease extent class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DiseaseOccurrence> getLatestValidatorOccurrencesForAdminUnitDiseaseExtentClass(
            Integer diseaseGroupId, boolean isGlobal, Integer gaulCode) {
        String queryString = String.format(LATEST_OCCURRENCES_QUERY, isGlobal ? GLOBAL : TROPICAL);

        Query query = currentSession().createQuery(queryString);
        query.setParameter("diseaseGroupId", diseaseGroupId);
        query.setParameter("gaulCode", gaulCode);

        return query.list();
    }

    /**
     * Gets all global AdminUnitDiseaseExtentClass objects for the specified DiseaseGroup.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the AdminUnitDiseaseExtentClasses.
     */
    @Override
    public List<AdminUnitDiseaseExtentClass> getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(
            Integer diseaseGroupId) {
        return listNamedQuery("getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId",
            "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets all tropical AdminUnitDiseaseExtentClass objects for the specified DiseaseGroup.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the tropical AdminUnitDiseaseExtentClasses.
     */
    @Override
    public List<AdminUnitDiseaseExtentClass> getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(
            Integer diseaseGroupId) {
        return listNamedQuery("getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId",
            "diseaseGroupId", diseaseGroupId);
    }
}
