package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Set;

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
     * not yet submitted a review. Only SEEG users may view occurrences of disease groups before first model run prep.
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
     * Get disease occurrences (excluding bias occurrences) that match the specified disease group, location, alert
     * and occurrence start date. Used to check for the existence of a disease occurrence.
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
     * @param statuses A set of disease occurrence statuses from which to return occurrences.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
            Integer diseaseGroupId, DiseaseOccurrenceStatus... statuses);

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
     * Gets the number of distinct locations from the new disease occurrences for the specified disease group.
     * A "new" occurrence has status READY, and a suitable created_date.
     * Occurrences must additionally satisfy one of:
     * + The distance from disease extent values is greater than minimum specified on the disease group (a new area).
     * + The environmental suitability values is less than max specified on the disease group (a new area).
     * @param diseaseGroupId The ID of the disease group.
     * @param locationsFromLastModelRun A list of location ids used in the last model run.
     * @param cutoffForAutomaticallyValidated Automatically validated occurrences must be newer than this date.
     * @param cutoffForManuallyValidated Manually validated occurrences must be newer than this date.
     * @param maxEnvironmentalSuitability The max environmental suitability of occurrences to consider.
     * @param minDistanceFromDiseaseExtent The minimum distance from disease extent of occurrences to consider.
     * @return The number of locations.
     */
    long getDistinctLocationsCountForTriggeringModelRun(Integer diseaseGroupId,
                                                        Set<Integer> locationsFromLastModelRun,
                                                        DateTime cutoffForAutomaticallyValidated,
                                                        DateTime cutoffForManuallyValidated,
                                                        Double maxEnvironmentalSuitability,
                                                        Double minDistanceFromDiseaseExtent);

    /**
     * Gets statistics about the occurrences of the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The statistics.
     */
    DiseaseOccurrenceStatistics getDiseaseOccurrenceStatistics(int diseaseGroupId);

    /**
     * Gets a list of disease occurrences for batching initialisation, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForBatchingInitialisation(int diseaseGroupId);

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
     * except those that have been discarded, or at locations marked as ineligible.
     */
    long getNumberOfOccurrencesEligibleForModelRun(int diseaseGroupId, DateTime startDate, DateTime endDate);

    /**
     * Gets the number of bespoke bias occurrences that have been uploaded for use with a specified diseases group,
     * regardless of suitability.
     * @param diseaseGroup The disease group being modelled.
     * @return The number of bias occurrences.
     */
    long getCountOfUnfilteredBespokeBiasOccurrences(DiseaseGroup diseaseGroup);

    /**
     * Gets an estimate of number of bespoke bias occurrences that have been uploaded for use with a specified diseases
     * group, which are suitable for use in a model. This is only an estimate as the occurrence date filter that is
     * applied during model runs is not applied.
     * @param diseaseGroup The disease group being modelled.
     * @return The number of bias occurrences.
     */
    long getEstimateCountOfFilteredBespokeBiasOccurrences(DiseaseGroup diseaseGroup);

    /**
     * Gets the estimate of number of occurrences that are available for use as a default/fallback bias set for a
     * specified disease group, which are suitable for use in a model. This is only an estimate as the occurrence date
     * filter that is applied during model runs is not applied.
     * @param diseaseGroup The disease group being modelled.
     * @return The number of bias occurrences.
     */
    long getEstimateCountOfFilteredDefaultBiasOccurrences(DiseaseGroup diseaseGroup);

    /**
     * Gets the bespoke bias occurrences that are should be used with a model run (for sample bias).
     * @param diseaseGroup The disease group being modelled.
     * @param startDate The start date of the model run input data range.
     * @param endDate The end date  of the model run input data range.
     * @return The bias occurrences.
     */
    List<DiseaseOccurrence> getBespokeBiasOccurrencesForModelRun(
            DiseaseGroup diseaseGroup, DateTime startDate, DateTime endDate);

    /**
     * Gets the default/fallback bias occurrences that are should be used with a model run (for sample bias).
     * This should be used when a bespoke dataset hasn't been provided.
     * @param diseaseGroup The disease group being modelled (will be excluded from bias set).
     * @param startDate The start date of the model run input data range.
     * @param endDate The end date  of the model run input data range.
     * @return The bias occurrences.
     */
    List<DiseaseOccurrence> getDefaultBiasOccurrencesForModelRun(
            DiseaseGroup diseaseGroup, DateTime startDate, DateTime endDate);

    /**
     * Delete all of the occurrence that are labelled as bias for the specified disease group.
     * @param diseaseGroupId Disease group for which to remove the bias points
     *                       (i.e. bias_disease_group_id, not disease_group_id).
     */
    void deleteDiseaseOccurrencesByBiasDiseaseId(int diseaseGroupId);
}
