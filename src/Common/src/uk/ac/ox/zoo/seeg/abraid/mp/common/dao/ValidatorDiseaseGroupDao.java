package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.List;

/**
 * Interface for the ValidatorDiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ValidatorDiseaseGroupDao {
    /**
     * Gets all validator disease groups.
     * @return All validator disease groups.
     */
    List<ValidatorDiseaseGroup> getAll();

    /**
     * Gets a validator disease group by ID.
     * @param id The ID.
     * @return The validator disease group with the specified ID, or null if not found.
     */
    ValidatorDiseaseGroup getById(Integer id);

    /**
     * Gets a validator disease group by name.
     * @param name The name.
     * @return The validator disease group with the specified name, or null if not found.
     */
    ValidatorDiseaseGroup getByName(String name);

    /**
     * Saves the specified validatorDiseaseGroup.
     * @param validatorDiseaseGroup The validatorDiseaseGroup to save.
     */
    void save(ValidatorDiseaseGroup validatorDiseaseGroup);
}
