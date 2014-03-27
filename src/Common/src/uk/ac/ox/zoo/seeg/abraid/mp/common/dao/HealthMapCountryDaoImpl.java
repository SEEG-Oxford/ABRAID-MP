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
public class HealthMapCountryDaoImpl extends AbstractDao<HealthMapCountry, Long> implements HealthMapCountryDao {
    public HealthMapCountryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a HealthMap country by name.
     * @param name The name.
     * @return The country, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple countries with this name are found
     */
    public HealthMapCountry getByName(String name) {
        return uniqueResultNamedQuery("getHealthMapCountryByName", "name", name);
    }
}
