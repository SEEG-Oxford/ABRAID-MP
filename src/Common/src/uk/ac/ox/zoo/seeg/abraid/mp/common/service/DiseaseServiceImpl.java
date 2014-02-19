package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Service class for diseases, including disease occurrences.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class DiseaseServiceImpl implements DiseaseService {
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseGroupDao diseaseGroupDao;
    private HealthMapDiseaseDao healthMapDiseaseDao;

    public DiseaseServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao, DiseaseGroupDao diseaseGroupDao,
                              HealthMapDiseaseDao healthMapDiseaseDao) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseGroupDao = diseaseGroupDao;
        this.healthMapDiseaseDao = healthMapDiseaseDao;
    }

    /**
     * Saves a disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    @Override
    @Transactional
    public void saveDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence) {
        diseaseOccurrenceDao.save(diseaseOccurrence);
    }

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    @Override
    @Transactional
    public void saveHealthMapDisease(HealthMapDisease disease) {
        healthMapDiseaseDao.save(disease);
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
     * Gets all disease groups.
     * @return All disease groups.
     */
    @Override
    public List<DiseaseGroup> getAllDiseaseGroups() {
        return diseaseGroupDao.getAll();
    }
}
