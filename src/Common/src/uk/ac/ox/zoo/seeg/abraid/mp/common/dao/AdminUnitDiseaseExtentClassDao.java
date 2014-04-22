package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;

import java.util.List;

/**
 * Interface for the AdminUnitDiseaseExtentClass entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitDiseaseExtentClassDao {
    /**
     * Gets all AdminUnitDiseaseExtentClass objects for the specified DiseaseGroup.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the AdminUnitDiseaseExtentClasses.
     */
    List<AdminUnitDiseaseExtentClass> getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(Integer diseaseGroupId);
}
