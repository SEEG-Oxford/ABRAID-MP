package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

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
     * Gets a list of all diseases.
     * @return A list of all diseases.
     */
    public List<DiseaseGroup> getAllDiseases() {
        return diseaseDao.getAll();
    }

    /**
     * Gets a diseaseGroup by name.
     * @param name The name.
     * @return The diseaseGroup, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple diseases with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public DiseaseGroup getDiseaseByName(String name) {
        return diseaseDao.getByName(name);
    }

    /**
     * Saves the specified diseaseGroup.
     * @param diseaseGroup The diseaseGroup to save.
     */
    @Override
    @Transactional
    public void saveDisease(DiseaseGroup diseaseGroup) {
        diseaseDao.save(diseaseGroup);
    }
}
