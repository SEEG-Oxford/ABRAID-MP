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
     * Gets the names of all countries to be shown in the HealthMap country report (sorted).
     * @return The country names.
     */
    List<String> getCountryNamesForHealthMapReport();

    /**
     * Gets a country by name.
     * @param name The name.
     * @return The country, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple countries with this name are found (should not
     * occur as names are unique)
     */
    Country getByName(String name);

    /**
     * Gets an country unit GAUL code.
     * @param gaulCode The GAUL code.
     * @return The country with the specified GAUL code, or null if it does not exist.
     */
    Country getByGaulCode(Integer gaulCode);

    /**
     * Gets the list of African countries that should be considered when calculating
     * the minimum data spread required for a model run.
     * @return The list of GAUL codes for the African countries used in minimum data spread calculation.
     */
    List<Integer> getCountriesForMinDataSpreadCalculation();
}
