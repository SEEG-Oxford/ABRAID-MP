package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

import java.util.List;

/**
 * Interface for the HealthMapSubDisease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface HealthMapSubDiseaseDao {
    /**
     * Gets all sub-diseases.
     * @return All sub-diseases.
     */
    List<HealthMapSubDisease> getAll();

}
