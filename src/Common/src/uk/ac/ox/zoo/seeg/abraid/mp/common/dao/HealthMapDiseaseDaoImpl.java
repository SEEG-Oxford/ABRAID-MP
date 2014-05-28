package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;

/**
 * The HealthMapDisease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class HealthMapDiseaseDaoImpl extends AbstractDao<HealthMapDisease, Integer> implements HealthMapDiseaseDao {
    public HealthMapDiseaseDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
