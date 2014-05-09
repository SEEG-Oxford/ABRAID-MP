package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

/**
 * The DiseaseExtentClass entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseExtentClassDaoImpl extends AbstractDao<DiseaseExtentClass, Integer>
        implements DiseaseExtentClassDao {
    public DiseaseExtentClassDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a disease extent class by name.
     * @param name The name.
     * @return The disease extent class with the specified name, or null if not found.
     */
    @Override
    public DiseaseExtentClass getByName(String name) {
        return uniqueResultNamedQuery("getDiseaseExtentClassByName", "name", name);
    }
}
