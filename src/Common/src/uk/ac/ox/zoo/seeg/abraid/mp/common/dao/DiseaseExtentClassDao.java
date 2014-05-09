package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

import java.util.List;

/**
 * Interface for the DiseaseExtentClass entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseExtentClassDao {
    /**
     * Gets all disease extent classes.
     * @return All disease extent classes.
     */
    List<DiseaseExtentClass> getAll();

    /**
     * Gets a disease extent class by name.
     * @param name The name.
     * @return The disease extent class with the specified name, or null if not found.
     */
    DiseaseExtentClass getByName(String name);
}
