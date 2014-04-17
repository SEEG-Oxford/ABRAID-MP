package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

/**
 * Interface for the AdminUnitDiseaseExtentClass entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitDiseaseExtentClassDao {
    /**
     * Get the disease extent class for the disease group, across the admin unit.
     * @param gaulCode The gaul code of the administrative unit.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease extent class.
     */
    DiseaseExtentClass getDiseaseExtentClass(Integer gaulCode, Integer diseaseGroupId);
}
