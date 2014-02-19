package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

/**
 * The Country entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class CountryDaoImpl extends AbstractDao<Country, String> implements CountryDao {
    public CountryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a country by name.
     * @param name The name.
     * @return The country, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple countries with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public Country getByName(String name) {
        return uniqueResultNamedQuery("getCountryByName", "name", name);
    }
}
