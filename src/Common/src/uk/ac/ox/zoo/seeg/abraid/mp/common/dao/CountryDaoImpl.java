package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

import java.util.List;

/**
 * The Country entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class CountryDaoImpl extends AbstractDao<Country, Integer> implements CountryDao {
    public CountryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets the names of all countries to be shown in the HealthMap country report (sorted).
     * @return The country names.
     */
    @Override
    public List<String> getCountryNamesForHealthMapReport() {
        Query query = getParameterisedNamedQuery("getCountryNamesForHealthMapReport");
        return query.list();
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

    /**
     * Gets an country unit GAUL code.
     * @param gaulCode The GAUL code.
     * @return The country with the specified GAUL code, or null if it does not exist.
     */
    @Override
    public Country getByGaulCode(Integer gaulCode) {
        return getById(gaulCode);
    }

    /**
     * Gets the list of African countries that should be considered when calculating
     * the minimum data spread required for a model run.
     * @return The list of GAUL codes for the African countries used in minimum data spread calculation.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getCountriesForMinDataSpreadCalculation() {
        Query query = namedQuery("getCountriesForMinDataSpreadCalculation");
        return query.list();
    }
}
