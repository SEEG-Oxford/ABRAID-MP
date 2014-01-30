package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOutbreak;

/**
 * Interface for the DiseaseOutbreak entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOutbreakDao {
    /**
     * Gets a disease outbreak by ID.
     * @param id The disease outbreak ID.
     * @return The disease outbreak, or null if it does not exist.
     */
    DiseaseOutbreak getById(Integer id);

    /**
     * Saves the specified disease outbreak.
     * @param diseaseOutbreak The disease outbreak to save.
     */
    void save(DiseaseOutbreak diseaseOutbreak);
}
