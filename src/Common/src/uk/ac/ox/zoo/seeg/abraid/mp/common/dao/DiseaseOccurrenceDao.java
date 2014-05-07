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
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId, Integer validatorDiseaseGroupId);

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
     *                                   value.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value.
     * @param feedIds All disease occurrences must result from one of these feeds. If feed IDs is null or zero,
     *                accepts all feeds.
     * @param isGlobal True if the disease group is global, otherwise false.
     * @return A list of disease occurrences.
     */
    List<DiseaseOccurrenceForDiseaseExtent> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            List<Integer> feedIds, boolean isGlobal);
}
