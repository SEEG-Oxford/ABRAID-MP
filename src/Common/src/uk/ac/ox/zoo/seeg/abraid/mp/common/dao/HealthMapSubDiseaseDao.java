package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

import java.util.List;

/**
 * Interface for the HealthMapSubDisease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface HealthMapSubDiseaseDao {
    /**
     * Gets a HealthMapSubDisease by ID.
     *
     * @param id The ID.
     * @return The HealthMapSubDisease with the specified ID, or null if not found.
     */
    HealthMapSubDisease getById(Integer id);

    /**
     * Gets all sub-diseases.
     * @return All sub-diseases.
     */
    List<HealthMapSubDisease> getAll();

    /**
     * Saves the specified HealthMap subdisease.
     * @param disease The disease to save.
     */
    void save(HealthMapSubDisease disease);
}
