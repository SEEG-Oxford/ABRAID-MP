package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitSimplifiedGlobal;

import java.util.List;

/**
 * Interface for the AdminUnitSimplifiedGlobal entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitSimplifiedGlobalDao {
    /**
     * Gets all administrative units.
     * @return A list of all administrative units.
     */
    List<AdminUnitSimplifiedGlobal> getAll();
}
