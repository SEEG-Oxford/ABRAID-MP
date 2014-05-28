package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;

/**
 * The HealthMapCountry entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class HealthMapCountryDaoImpl extends AbstractDao<HealthMapCountry, Integer> implements HealthMapCountryDao {
    public HealthMapCountryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
