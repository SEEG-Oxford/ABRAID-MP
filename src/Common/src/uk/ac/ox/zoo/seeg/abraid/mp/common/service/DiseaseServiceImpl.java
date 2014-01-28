package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseServiceImpl implements DiseaseService {
    private DiseaseDao diseaseDao;

    public void setDiseaseDao(DiseaseDao diseaseDao) {
        this.diseaseDao = diseaseDao;
    }

    @Override
    public Disease getDiseaseByName(String name) {
        return diseaseDao.getDiseaseByName(name);
    }

    @Override
    public void saveDisease(Disease disease) {
        diseaseDao.saveDisease(disease);
    }
}
