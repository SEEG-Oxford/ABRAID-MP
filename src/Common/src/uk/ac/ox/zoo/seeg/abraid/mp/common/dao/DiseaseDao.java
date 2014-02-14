package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Interface for the DiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseDao {
    /**
     * Gets all diseases.
     * @return All diseases.
     */
    List<DiseaseGroup> getAll();

    /**
     * Gets a diseaseGroup by name.
     * @param name The name.
     * @return The diseaseGroup, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple disease groups with this name are found (should not
     * occur as names are unique)
     */
    DiseaseGroup getByName(String name);

    /**
     * Saves the specified diseaseGroup.
     * @param diseaseGroup The diseaseGroup to save.
     */
    void save(DiseaseGroup diseaseGroup);
}
