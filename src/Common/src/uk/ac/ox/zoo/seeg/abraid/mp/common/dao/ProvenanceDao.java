package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;

import java.util.List;

/**
 * Interface for the Provenance entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ProvenanceDao {
    /**
     * Gets all provenances.
     * @return All provenances.
     */
    List<Provenance> getAll();

    /**
     * Gets a provenance by name.
     * @param name The provenance name
     * @return The provenance name, or null if it does not exist.
     */
    Provenance getByName(String name);

    /**
     * Saves the specified provenance.
     * @param provenance The provenance to save.
     */
    void save(Provenance provenance);
}
