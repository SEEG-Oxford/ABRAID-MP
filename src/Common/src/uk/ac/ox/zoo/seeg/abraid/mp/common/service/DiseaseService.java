package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * Service interface for diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseService {

    /**
     * Gets a disease by name.
     * @param name The name.
     * @return The disease, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple diseases with this name are found (should not
     * occur as names are unique)
     */
    Disease getDiseaseByName(String name);

    /**
     * Saves the specified disease.
     * @param disease The disease to save.
     */
    void saveDisease(Disease disease);
}
