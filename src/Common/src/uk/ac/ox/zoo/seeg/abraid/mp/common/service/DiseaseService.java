package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

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
     * Gets the validator disease group by its name.
     * @param validatorDiseaseGroupName The name of the validator disease group.
     * @return The validator disease group.
     */
    ValidatorDiseaseGroup getValidatorDiseaseGroupByName(String validatorDiseaseGroupName);

    /**
     * Gets all the validator disease groups.
     * @return A list of all validator disease groups.
     */
    List<ValidatorDiseaseGroup> getAllValidatorDiseaseGroups();

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

    /**
     * Determines whether the specified disease occurrence already exists in the database. This is true if an
     * occurrence exists with the same disease group, location, alert and occurrence start date.
     * @param occurrence The disease occurrence.
     * @return True if the occurrence already exists in the database, otherwise false.
     */
    boolean doesDiseaseOccurrenceExist(DiseaseOccurrence occurrence);

    /**
     * Determines whether the specified occurrence's disease id belongs to the corresponding validator disease group.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @return True if the occurrence refers to a disease in the validator disease group, otherwise false.
     */
    boolean doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(Integer diseaseOccurrenceId,
                                                                           Integer validatorDiseaseGroupId);
}
