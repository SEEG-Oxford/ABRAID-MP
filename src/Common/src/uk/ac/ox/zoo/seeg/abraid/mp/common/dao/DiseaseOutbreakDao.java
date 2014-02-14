package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Interface for the DiseaseOccurrence entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOutbreakDao {
    /**
     * Gets a disease occurrence by ID.
     * @param id The disease occurrence ID.
     * @return The disease occurrence, or null if it does not exist.
     */
    DiseaseOccurrence getById(Integer id);

    /**
     * Saves the specified disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    void save(DiseaseOccurrence diseaseOccurrence);
}
