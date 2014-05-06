package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitTropical;

import java.util.List;

/**
 * Interface for the AdminUnitTropical entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitTropicalDao {
    /**
     * Gets all tropical administrative units.
     * @return A list of all tropical administrative units.
     */
    List<AdminUnitTropical> getAll();

    /**
     * Gets the tropical admin unit, specified by its GAUL code.
     * @param gaulCode The gaul code of the admin unit.
     * @return The tropical admin unit.
     */
    AdminUnitTropical getByGaulCode(Integer gaulCode);
}
