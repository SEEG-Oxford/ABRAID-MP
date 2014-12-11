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
     * Gets all sub-diseases.
     * @return All sub-diseases.
     */
    List<HealthMapSubDisease> getAll();

    /**
     * Gets a HealthMap sub-disease by name.
     * @param name The name.
     * @return The HealthMap sub-disease, or null if not found.
     */
    HealthMapSubDisease getByName(String name);
}
