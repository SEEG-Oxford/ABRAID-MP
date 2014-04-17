package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.TropicalAdminUnit;

import java.util.List;

/**
 * Interface for the TropicalAdminUnit entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface TropicalAdminUnitDao {
    /**
     * Gets all tropical administrative units.
     * @return A list of all tropical administrative units.
     */
    List<TropicalAdminUnit> getAll();
}
