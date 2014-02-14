package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Service interface for diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseService {
    /**
     * Gets a list of all diseases.
     * @return A list of all diseases.
     */
    List<DiseaseGroup> getAllDiseases();

    /**
     * Gets a diseaseGroup by name.
     * @param name The name.
     * @return The diseaseGroup, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple diseases with this name are found (should not
     * occur as names are unique)
     */
    DiseaseGroup getDiseaseByName(String name);

    /**
     * Saves the specified diseaseGroup.
     * @param diseaseGroup The diseaseGroup to save.
     */
    void saveDisease(DiseaseGroup diseaseGroup);
}
