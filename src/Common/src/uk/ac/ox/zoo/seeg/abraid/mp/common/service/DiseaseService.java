package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Map;

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
     * Gets the disease group by its id.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease group.
     */
    DiseaseGroup getDiseaseGroupById(Integer diseaseGroupId);

    /**
     * Gets all disease groups.
     * @return All disease groups.
     */
    List<DiseaseGroup> getAllDiseaseGroups();

    /**
     * Gets all the validator disease groups.
     * @return A list of all validator disease groups.
     */
    List<ValidatorDiseaseGroup> getAllValidatorDiseaseGroups();

    /**
     * For each validator disease group, get a list of its disease groups.
     * @return The map, from the name of the validator disease group, to the disease groups belonging to it.
     */
    Map<String, List<DiseaseGroup>> getValidatorDiseaseGroupMap();

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


    /**
     * For each global admin unit, get the disease extent class for the specified disease group.
     * @param globalAdminUnits The global administrative units.
     * @param diseaseGroupId The id of the disease group.
     * @return The map, from global admin unit, to its disease extent class, for the specified disease group.
     */
    Map<GlobalAdminUnit, DiseaseExtentClass> getAdminUnitDiseaseExtentClassMap(List<GlobalAdminUnit> globalAdminUnits,
                                                                               Integer diseaseGroupId);

}
