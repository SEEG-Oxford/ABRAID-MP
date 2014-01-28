package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * Copyright (c) 2014 University of Oxford
 */
interface DiseaseService {
    Disease getDiseaseByName(String name);
    void saveDisease(Disease disease);
}
