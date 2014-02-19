package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Service interface for diseases, including disease occurrences.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseService {
    /**
     * Gets all HealthMap diseases.
     * @return All HealthMap diseases.
     */
    List<HealthMapDisease> getAllHealthMapDiseases();

    /**
     * Gets all disease groups.
     * @return All disease groups.
     */
    List<DiseaseGroup> getAllDiseaseGroups();

    /**
     * Saves a disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    void saveDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence);

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    void saveHealthMapDisease(HealthMapDisease disease);
}
