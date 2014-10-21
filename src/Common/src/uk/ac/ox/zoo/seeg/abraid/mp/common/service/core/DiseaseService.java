package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
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
     * Gets the disease group by its id.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease group.
     */
    DiseaseGroup getDiseaseGroupById(Integer diseaseGroupId);

    /**
     * Gets the validator disease group by its id.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @return The validator disease group.
     */
    ValidatorDiseaseGroup getValidatorDiseaseGroupById(Integer validatorDiseaseGroupId);

    /**
     * For each validator disease group, get a list of its disease groups.
     * @return The map, from the name of the validator disease group, to the disease groups belonging to it.
     */
    Map<String, List<DiseaseGroup>> getValidatorDiseaseGroupMap();

    /**
     * Gets a list of admin units for global or tropical diseases, depending on whether the specified disease group
     * is a global or a tropical disease.
     * @param diseaseGroupId The ID of the disease group.
     * @return The disease extent.
     */
    List<? extends AdminUnitGlobalOrTropical> getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(
            Integer diseaseGroupId);

    /**
     * Gets disease occurrences with the specified IDs.
     * @param diseaseOccurrenceIds The disease occurrence IDs.
     * @return The disease occurrences with the specified IDs.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesById(List<Integer> diseaseOccurrenceIds);

    /**
     * Gets all disease occurrences for the specified disease group.
     * @param diseaseGroupId The disease group's ID.
     * @return all disease occurrences for the specified disease group.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesByDiseaseGroupId(int diseaseGroupId);

    /**
     * Gets disease occurrences for generating the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param minimumValidationWeighting All disease occurrences must have a validation weighting greater than this
     * value, and must have a final weighting. If null, the validation and final weightings are ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise false.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            boolean useGoldStandardOccurrences);

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is false.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId);

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is true
     * and finalWeighting is currently null.
     *
     * @param diseaseGroupId The ID of the disease group.
     * @param mustHaveEnvironmentalSuitability True if the occurrence's environmental suitability must be non-null.
     *                                         False if it doesn't matter either way.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
            Integer diseaseGroupId, boolean mustHaveEnvironmentalSuitability);

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise false.
     * @return Disease occurrences for a request to run the model.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId,
                                                                    boolean useGoldStandardOccurrences);

    /**
     * Gets the list of new disease occurrences for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @param startDate Occurrences must be newer than this date.
     * @param endDate Occurrences must be older than this date, to ensure they have had ample time in validation.
     * @return The list of relevant new occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForTriggeringModelRun(int diseaseGroupId,
                                                                       DateTime startDate, DateTime endDate);

    /**
     * Gets the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The disease extent.
     */
    List<AdminUnitDiseaseExtentClass> getDiseaseExtentByDiseaseGroupId(Integer diseaseGroupId);

    /**
     * Gets a disease extent class by name.
     * @param name The disease extent class name.
     * @return The corresponding disease extent class, or null if it does not exist.
     */
    DiseaseExtentClass getDiseaseExtentClass(String name);

    /**
     * Gets all disease extent classes.
     * @return All disease extent classes.
     */
    List<DiseaseExtentClass> getAllDiseaseExtentClasses();

    /**
     * Gets a list of all the disease occurrence reviews in the database.
     * @return The disease occurrence reviews.
     */
    List<DiseaseOccurrenceReview> getAllDiseaseOccurrenceReviews();

    /**
     * Gets a list of all the disease occurrence reviews in the database for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The disease occurrence reviews.
     */
    List<DiseaseOccurrenceReview> getAllDiseaseOccurrenceReviewsByDiseaseGroupId(Integer diseaseGroupId);

    /**
     * Gets all reviews (for all time) for the disease occurrences which have new reviews.
     * @param lastModelRunPrepDate The date on which the disease occurrence reviews were last retrieved.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of the reviews of disease occurrences whose weightings needs updating.
     */
    List<DiseaseOccurrenceReview> getDiseaseOccurrenceReviewsForModelRunPrep(DateTime lastModelRunPrepDate,
                                                                             Integer diseaseGroupId);

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
     * Saves a disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    void saveDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence);

    /**
     * Saves a disease group.
     * @param diseaseGroup The disease group to save.
     */
    void saveDiseaseGroup(DiseaseGroup diseaseGroup);

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    void saveHealthMapDisease(HealthMapDisease disease);

    /**
     * Saves a disease extent class that is associated with an admin unit (global or tropical).
     * @param adminUnitDiseaseExtentClass The object to save.
     */
    void saveAdminUnitDiseaseExtentClass(AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass);

    /**
     * Updates the aggregated disease extent that is stored in the disease_extent table, for the specified disease.
     * @param diseaseGroupId The disease group ID.
     * @param isGlobal True if the disease is global, false if tropical.
     */
    void updateAggregatedDiseaseExtent(int diseaseGroupId, boolean isGlobal);

    /**
     * Gets statistics about the occurrences of the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The statistics.
     */
    DiseaseOccurrenceStatistics getDiseaseOccurrenceStatistics(int diseaseGroupId);

    /**
     * Gets the IDs of disease groups that have automatic model runs enabled.
     * @return The IDs of relevant disease groups.
     */
    List<Integer> getDiseaseGroupIdsForAutomaticModelRuns();

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForBatching(int diseaseGroupId, DateTime batchEndDate);

    /**
     * Gets a list of recent disease occurrences that have been validated (they have a target expert weighting).
     * @param diseaseGroupId The disease group ID.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForTrainingPredictor(int diseaseGroupId);
}
