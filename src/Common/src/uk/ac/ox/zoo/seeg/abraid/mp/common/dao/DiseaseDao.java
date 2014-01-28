package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseDao {
    Disease getDiseaseByName(String name);
    void saveDisease(Disease disease);
}
