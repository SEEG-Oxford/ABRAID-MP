package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;

import java.util.List;

/**
 * Interface for the AdminUnitQC entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitQCDao {
    /**
     * Gets all administrative units.
     * @return A list of all administrative units.
     */
    List<AdminUnitQC> getAll();

    /**
     * Gets an administrative unit by GAUL code.
     * @param gaulCode The GAUL code.
     * @return The administrative unit with the specified GAUL code, or null if it does not exist.
     */
    AdminUnitQC getByGaulCode(Integer gaulCode);
}
