package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName;

/**
 * Interface for the GeoName entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface GeoNameDao {
    /**
     * Gets a GeoName by ID.
     * @param geoNameId The alert ID.
     * @return The GeoName, or null if not found.
     */
    GeoName getById(Integer geoNameId);
}
