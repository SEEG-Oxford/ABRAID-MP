package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;

import java.util.List;

/**
 * Interface for the HealthMapDisease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface HealthMapDiseaseDao {
    /**
     * Gets a HealthMap disease by ID.
     * @param id The ID.
     * @return The HealthMap disease, or null if not found.
     */
    HealthMapDisease getById(Integer id);

    /**
     * Gets all diseases.
     * @return All diseases.
     */
    List<HealthMapDisease> getAll();

    /**
     * Saves the specified HealthMap disease.
     * @param disease The disease to save.
     */
    void save(HealthMapDisease disease);
}
