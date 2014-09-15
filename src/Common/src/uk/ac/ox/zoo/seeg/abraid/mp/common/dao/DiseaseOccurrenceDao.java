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
     * @return all disease occurrences for the specified disease group.
     */
    List<DiseaseOccurrence> getByDiseaseGroupId(int diseaseGroupId);

    /**
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId,
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
     * value. If null, the validation weighting is ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param isGlobal True if the disease group is global, otherwise false.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrenceForDiseaseExtent> getDiseaseOccurrencesForDiseaseExtent(
           Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate, boolean isGlobal);

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is false.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId);

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is true
     * and finalWeighting is currently null.
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
     * @return Disease occurrences for a request to run the model.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId);

    /**
     * Gets the list of new disease occurrences for the specified disease group.
     * A "new" occurrence has is_validated not null and a created_date that is more than a week ago.
     * Occurrence must additionally satisfy that environmental suitability and distance from disease extent values are
     * greater than minimum specified for the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The list of relevant new occurrences..
     */
    List<DiseaseOccurrence> getNewOccurrencesByDiseaseGroup(Integer diseaseGroupId);

    /**
     * Gets statistics about the occurrences of the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The statistics.
     */
    DiseaseOccurrenceStatistics getDiseaseOccurrenceStatistics(int diseaseGroupId);

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrence> getOccurrencesForBatching(int diseaseGroupId, DateTime batchEndDate);
}
