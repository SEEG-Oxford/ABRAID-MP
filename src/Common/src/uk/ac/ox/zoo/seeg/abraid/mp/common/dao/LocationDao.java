package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;

import java.util.List;

/**
 * Interface for the Location entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LocationDao {
    /**
     * Gets a location by ID.
     * @param id The ID
     * @return The location, or null if it does not exist.
     */
    Location getById(Integer id);

    /**
     * Gets all locations.
     * @return All locations.
     */
    List<Location> getAll();

    /**
     * Saves the specified location.
     * @param location The location to save.
     */
    void save(Location location);
}
