package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitSimplifiedTropical;

import java.util.List;

/**
 * Interface for the AdminUnitSimplifiedTropical entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitSimplifiedTropicalDao {
    /**
     * Gets all administrative units.
     * @return A list of all administrative units.
     */
    List<AdminUnitSimplifiedTropical> getAll();
}
