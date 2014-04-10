package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * The DiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseGroupDaoImpl extends AbstractDao<DiseaseGroup, Integer> implements DiseaseGroupDao {
    public DiseaseGroupDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a disease group by its name.
     * @param name The name of the disease.
     * @return The disease group with the specified name, or null if not found.
     */
    @Override
    public DiseaseGroup getByName(String name) {
        return uniqueResultNamedQuery("getDiseaseGroupByName", "name", name);
    }
}
