package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Interface for the DiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseGroupDao {
    /**
     * Gets all disease groups.
     * @return All disease groups.
     */
    List<DiseaseGroup> getAll();

    /**
     * Gets a disease group by ID.
     * @param id The ID.
     * @return The disease group with the specified ID, or null if not found.
     */
    DiseaseGroup getById(Integer id);

    /**
     * Gets a disease group by its name.
     * @param name The name of the disease.
     * @return The disease group with the specified name, or null if not found.
     */
    DiseaseGroup getByName(String name);

    /**
     * Gets a list of disease groups by expert ID.
     * @param expertId The expert ID.
     * @return A list of disease groups associated with the expert ID (via ValidatorDiseaseGroup).
     */
    List<DiseaseGroup> getByExpertId(int expertId);

    /**
     * Saves the specified diseaseGroup.
     * @param diseaseGroup The diseaseGroup to save.
     */
    void save(DiseaseGroup diseaseGroup);
}
