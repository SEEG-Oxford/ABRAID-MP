package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

/**
 * The HealthMapSubDisease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class HealthMapSubDiseaseDaoImpl extends AbstractDao<HealthMapSubDisease, Integer>
        implements HealthMapSubDiseaseDao {
    public HealthMapSubDiseaseDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
