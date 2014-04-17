package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitDiseaseExtentClassDaoImpl extends AbstractDao<AdminUnitDiseaseExtentClass, Integer> implements
        AdminUnitDiseaseExtentClassDao {
    public AdminUnitDiseaseExtentClassDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    /**
     * Get the disease extent class for the disease group, across the admin unit.
     * @param gaulCode The gaul code of the administrative unit.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease extent class.
     */
    @Override
    public DiseaseExtentClass getDiseaseExtentClass(Integer gaulCode, Integer diseaseGroupId) {
        Query query = getParameterisedNamedQuery("getDiseaseExtentClass", "gaulCode", gaulCode, "diseaseGroupId", diseaseGroupId);
        return (DiseaseExtentClass) query.uniqueResult();
    }
}
