package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceWeight;

import java.util.List;

/**
 * Interface for the ProvenanceWeight entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ProvenanceWeightDao {
    /**
     * Gets a provenance weight by ID.
     * @param id The ID
     * @return The provenance weight, or null if it does not exist.
     */
    ProvenanceWeight getById(Integer id);

    /**
     * Gets all provenance weights.
     * @return All of the provenance weights.
     */
    List<ProvenanceWeight> getAll();

    /**
     * Saves the specified provenance weight.
     * @param provenanceWeight The provenance weight to save.
     */
    void save(ProvenanceWeight provenanceWeight);
}
