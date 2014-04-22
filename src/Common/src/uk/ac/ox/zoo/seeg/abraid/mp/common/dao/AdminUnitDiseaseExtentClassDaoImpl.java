package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;

import java.util.List;

/**
 * The AdminUnitDiseaseExtentClass entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDiseaseExtentClassDaoImpl extends AbstractDao<AdminUnitDiseaseExtentClass, Integer> implements
        AdminUnitDiseaseExtentClassDao {
    public AdminUnitDiseaseExtentClassDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
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
