package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

import java.util.List;

/**
 * Interface for the Disease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseDao {
    /**
     * Gets all diseases.
     * @return All diseases.
     */
    List<Disease> getAll();

    /**
     * Gets a disease by name.
     * @param name The name.
     * @return The disease, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple diseases with this name are found (should not
     * occur as names are unique)
     */
    Disease getByName(String name);

    /**
     * Saves the specified disease.
     * @param disease The disease to save.
     */
    void save(Disease disease);
}
