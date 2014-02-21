package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoNamesLocationPrecision;

import java.util.List;

/**
 * Interface for the GeoNamesLocationPrecisionDao entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface GeoNamesLocationPrecisionDao {
    /**
     * Gets all mappings between GeoNames feature codes and location precisions.
     * @return A list of mappings.
     */
    List<GeoNamesLocationPrecision> getAll();
}
