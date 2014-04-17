package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;

import java.util.List;

/**
 * Interface for the LandSeaBorder entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LandSeaBorderDao {
    /**
     * Gets all land-sea borders.
     * @return All land-sea borders.
     */
    List<LandSeaBorder> getAll();
}
