package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

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
}
