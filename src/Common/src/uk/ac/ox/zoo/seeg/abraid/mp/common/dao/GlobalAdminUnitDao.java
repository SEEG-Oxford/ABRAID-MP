package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GlobalAdminUnit;

import java.util.List;

/**
 * Interface for the GlobalAdminUnit's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface GlobalAdminUnitDao {
    /**
     * Gets all global administrative units.
     * @return A list of all the global administrative units.
     */
    List<GlobalAdminUnit> getAll();

    /**
     * Gets the global admin unit, specified by its GAUL code.
     * @param gaulCode The gaul code of the admin unit.
     * @return The global admin unit.
     */
    GlobalAdminUnit getByGaulCode(Integer gaulCode);
}
