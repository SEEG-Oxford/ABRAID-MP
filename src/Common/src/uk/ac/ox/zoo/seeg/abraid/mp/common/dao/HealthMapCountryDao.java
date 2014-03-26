package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;

import java.util.List;

/**
 * Interface for the HealthMapCountry entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface HealthMapCountryDao {
    /**
     * Gets all HealthMap countries.
     * @return All HealthMap countries.
     */
    List<HealthMapCountry> getAll();

    /**
     * Gets a HealthMap country by name.
     * @param name The name.
     * @return The country, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple countries with this name are found
     */
    HealthMapCountry getByName(String name);
}
