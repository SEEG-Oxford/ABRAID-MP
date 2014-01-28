package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseDaoImpl implements DiseaseDao {
    @Override
    public Disease getDiseaseByName(String name) {
        return null;
    }

    @Override
    public void saveDisease(Disease disease) {

    }
}
