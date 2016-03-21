package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

import java.util.List;

/**
 * Service interface for HealthMap configuration.
 * Copyright (c) 2015 University of Oxford
 */
public interface HealthMapService {
    /**
     * Gets a HealthMap disease by ID.
     * @param id The ID.
     * @return The HealthMap disease, or null if not found.
     */
    HealthMapDisease getHealthMapDiseasesById(Integer id);

    /**
     * Gets a HealthMap subdisease by ID.
     * @param id The ID.
     * @return The HealthMap subdisease, or null if not found.
     */
    HealthMapSubDisease getHealthMapSubDiseasesById(Integer id);

    /**
     * Gets all HealthMap diseases.
     * @return All HealthMap diseases.
     */
    List<HealthMapDisease> getAllHealthMapDiseases();

    /**
     * Gets all HealthMap sub-diseases.
     * @return All HealthMap sub-diseases.
     */
    List<HealthMapSubDisease> getAllHealthMapSubDiseases();

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    void saveHealthMapDisease(HealthMapDisease disease);

    /**
     * Saves a HealthMap subdisease.
     * @param disease The disease to save.
     */
    void saveHealthMapSubDisease(HealthMapSubDisease disease);
}
