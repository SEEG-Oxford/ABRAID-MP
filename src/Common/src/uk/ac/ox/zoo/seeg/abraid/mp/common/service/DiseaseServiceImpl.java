package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * Service class for diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class DiseaseServiceImpl implements DiseaseService {
    private DiseaseDao diseaseDao;

    @Required
    public void setDiseaseDao(DiseaseDao diseaseDao) {
        this.diseaseDao = diseaseDao;
    }

    /**
     * Gets a disease by name.
     * @param name The name.
     * @return The disease, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple diseases with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public Disease getDiseaseByName(String name) {
        return diseaseDao.getByName(name);
    }

    /**
     * Saves the specified disease.
     * @param disease The disease to save.
     */
    @Override
    @Transactional
    public void saveDisease(Disease disease) {
        diseaseDao.save(disease);
    }
}
