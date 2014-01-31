package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import java.util.List;

/**
 * Interface for the Country entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface CountryDao {
    /**
     * Gets all countries.
     * @return A list of all countries.
     */
    List<Country> getAll();

    /**
     * Gets a country by name.
     * @param name The name.
     * @return The country, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple countries with this name are found (should not
     * occur as names are unique)
     */
    Country getByName(String name);
}
