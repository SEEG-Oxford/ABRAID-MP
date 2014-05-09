package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

/**
 * Interface for the DiseaseExtentClass entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseExtentClassDao {
    /**
     * Gets a disease extent class by name.
     * @param name The name.
     * @return The disease extent class with the specified name, or null if not found.
     */
    DiseaseExtentClass getByName(String name);

    /**
     * Saves the specified diseaseExtentClass.
     * @param diseaseExtentClass The diseaseExtentClass to save.
     */
    void save(DiseaseExtentClass diseaseExtentClass);
}
