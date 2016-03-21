package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.HealthMapDiseaseDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.HealthMapSubDiseaseDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

import java.util.List;

/**
 * Service class for HealthMap configuration.
 * Copyright (c) 2015 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class HealthMapServiceImpl implements HealthMapService {
    private HealthMapDiseaseDao healthMapDiseaseDao;
    private HealthMapSubDiseaseDao healthMapSubDiseaseDao;

    public HealthMapServiceImpl(
            HealthMapDiseaseDao healthMapDiseaseDao, HealthMapSubDiseaseDao healthMapSubDiseaseDao) {
        this.healthMapDiseaseDao = healthMapDiseaseDao;
        this.healthMapSubDiseaseDao = healthMapSubDiseaseDao;
    }

    /**
     * Gets a HealthMap disease by ID.
     * @param id The ID.
     * @return The HealthMap disease, or null if not found.
     */
    @Override
    public HealthMapDisease getHealthMapDiseasesById(Integer id) {
        return healthMapDiseaseDao.getById(id);
    }

    /**
     * Gets a HealthMap subdisease by ID.
     * @param id The ID.
     * @return The HealthMap subdisease, or null if not found.
     */
    @Override
    public HealthMapSubDisease getHealthMapSubDiseasesById(Integer id) {
        return healthMapSubDiseaseDao.getById(id);
    }

    /**
     * Gets all HealthMap diseases.
     * @return All HealthMap diseases.
     */
    @Override
    public List<HealthMapDisease> getAllHealthMapDiseases() {
        return healthMapDiseaseDao.getAll();
    }

    /**
     * Gets all HealthMap sub-diseases.
     * @return All HealthMap sub-diseases.
     */
    @Override
    public List<HealthMapSubDisease> getAllHealthMapSubDiseases() {
        return healthMapSubDiseaseDao.getAll();
    }

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    @Override
    public void saveHealthMapDisease(HealthMapDisease disease) {
        healthMapDiseaseDao.save(disease);
    }

    /**
     * Saves a HealthMap subdisease.
     * @param disease The disease to save.
     */
    @Override
    public void saveHealthMapSubDisease(HealthMapSubDisease disease) {
        healthMapSubDiseaseDao.save(disease);
    }

}
