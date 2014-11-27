package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Interface for the DiseaseOccurrence entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOccurrenceDao {
    /**
     * Gets all disease occurrences.
     * @return All disease occurrences.
     */
    List<DiseaseOccurrence> getAll();

    /**
     * Gets a disease occurrence by ID.
     * @param id The disease occurrence ID.
     * @return The disease occurrence, or null if it does not exist.
     */
    DiseaseOccurrence getById(Integer id);

    /**
     * Gets disease occurrences with the specified IDs.
     * @param diseaseOccurrenceIds The disease occurrence IDs.
     * @return The disease occurrences with the specified IDs.
     */
    List<DiseaseOccurrence> getByIds(List<Integer> diseaseOccurrenceIds);

    /**
     * Gets all disease occurrences for the specified disease group.
     * @param diseaseGroupId The disease group's ID.
     * @return All disease occurrences for the specified disease group.
     */
    List<DiseaseOccurrence> getByDiseaseGroupId(int diseaseGroupId);

    /**
     * Gets all disease occurrences for the specified disease group and occurrence statuses.
     * @param diseaseGroupId The disease group's ID.
     * @param statuses One or more disease occurrence statuses.
     * @return All disease occurrences for the specified disease group and statuses.
     */
    List<DiseaseOccurrence> getByDiseaseGroupIdAndStatuses(int diseaseGroupId, DiseaseOccurrenceStatus... statuses);

    /**
     * Gets a list of occurrence points, for the specified validator disease group, for which the specified expert has
     * not yet submitted a review. Only SEEG users may view occurrences of disease groups during setup phase.
     * Other external users may only view occurrences of disease groups with automatic model runs enabled.
     * @param expertId The id of the specified expert.
     * @param userIsSeeg Whether the expert is a member of SEEG, and therefore should review more occurrences.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId, boolean userIsSeeg,
                                                                         Integer validatorDiseaseGroupId);

    /**
     * Saves the specified disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    void save(DiseaseOccurrence diseaseOccurrence);

    /**
     * Get disease occurrences that match the specified disease group, location, alert and occurrence start date.
     * Used to check for the existence of a disease occurrence.
     * @param diseaseGroup The disease group.
     * @param location The location.
     * @param alert The alert.
     * @param occurrenceDate The occurrence date.
     * @return A list of matching disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForExistenceCheck(DiseaseGroup diseaseGroup,
                                                                   Location location, Alert alert,
                                                                   DateTime occurrenceDate);

    /**
     * Gets disease occurrences for generating the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param minimumValidationWeighting All disease occurrences must have a validation weighting greater than this
     * value, and must have a final weighting. If null, the validation and final weightings are ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise
     * false.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForDiseaseExtent(
           Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
           boolean onlyUseGoldStandardOccurrences);

    /**
     * Gets disease occurrences currently in validation, for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId);

    /**
     * Gets disease occurrences for the specified disease group which are yet to have a final weighting assigned.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(Integer diseaseGroupId);

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise
     * false.
     * @return Disease occurrences for a request to run the model.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId,
                                                                    boolean onlyUseGoldStandardOccurrences);

    /**
     * Gets the list of new disease occurrences for the specified disease group.
     * A "new" occurrence has status READY or IN_REVIEW, and a suitable created_date.
     * Occurrences must additionally satisfy that environmental suitability and distance from disease extent values are
     * greater than minimum specified for the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param startDate Occurrences must be newer than this date.
     * @param endDate Occurrences must be older than this date, to ensure they have had ample time in validation.
     * @return The list of relevant new occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForTriggeringModelRun(Integer diseaseGroupId,
                                                                       DateTime startDate, DateTime endDate);

    /**
     * Gets statistics about the occurrences of the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The statistics.
     */
    DiseaseOccurrenceStatistics getDiseaseOccurrenceStatistics(int diseaseGroupId);

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchStartDate The start date of the batch.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForBatching(int diseaseGroupId,
                                                             DateTime batchStartDate, DateTime batchEndDate);

    /**
     * Gets a list of recent disease occurrences that have been validated (they have a target expert weighting).
     * @param diseaseGroupId The disease group ID.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForTrainingPredictor(int diseaseGroupId);

    /**
     * Gets the number of occurrences that are eligible for being sent to the model, between the start and end dates.
     * This helps to estimate whether the number of occurrences in a batch will be sufficient for a model run.
     * @param diseaseGroupId The disease group ID.
     * @param startDate The start date.
     * @param endDate The end date.
     * @return The number of occurrences that are eligible for being sent to the model. This is all occurrences
     * except those that have been discarded, or points with COUNTRY precision.
     */
    long getNumberOfOccurrencesEligibleForModelRun(int diseaseGroupId, DateTime startDate, DateTime endDate);
}
