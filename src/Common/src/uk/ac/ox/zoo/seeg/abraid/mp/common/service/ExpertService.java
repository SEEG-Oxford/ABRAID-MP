package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service interface for experts.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ExpertService {
    /**
     * Gets a list of all experts.
     * @return A list of all experts.
     */
    List<Expert> getAllExperts();

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    Expert getExpertByEmail(String email);

    /**
     * Gets a list of occurrence points, for the specified validator disease group, for which the specified expert has
     * not yet submitted a review.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or validatorDiseaseGroupId cannot be found in the database.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId, Integer validatorDiseaseGroupId)
            throws IllegalArgumentException;

    /**
     * Gets the specified expert's disease interests.
     *
     * @param expertId The id of the specified expert.
     * @return The list of validator disease groups the expert can validate.
     */
    List<ValidatorDiseaseGroup> getDiseaseInterests(Integer expertId);

    /**
     * Gets the number of disease occurrence reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of disease occurrence reviews for the specified expert.
     */
    Integer getDiseaseOccurrenceReviewCount(Integer expertId);

    /**
     * Gets the number of disease occurrence reviews an expert has submitted, per validator disease group.
     * @param expertId The id of the specified expert.
     * @param diseaseInterests The list of validator disease groups the expert can validate.
     * @return The map from validator disease group name to its corresponding diseaseOccurrenceReviewCount.
     */
    Map<String, Integer> getDiseaseOccurrenceReviewCountPerValidatorDiseaseGroup(Integer expertId,
                                                                         List<ValidatorDiseaseGroup> diseaseInterests);

    /**
     * Gets the number of disease occurrences, per validator disease group.
     * @param diseaseInterests The list of validator disease groups the expert can validate.
     * @return The map from validator disease group name to its corresponding count of disease occurrences.
     */
    Map<String, Integer> getDiseaseOccurrenceCountPerValidatorDiseaseGroup(List<ValidatorDiseaseGroup> diseaseInterests);

    /**
     * Saves the disease occurrence review.
     * @param expertEmail The email address of the expert providing review.
     * @param occurrenceId The id of the disease occurrence.
     * @param response The expert's response.
     */
    void saveDiseaseOccurrenceReview(String expertEmail, Integer occurrenceId,
                                     DiseaseOccurrenceReviewResponse response);

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void saveExpert(Expert expert);

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param expertId The id of the specified expert.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @return True if the review already exists, otherwise false.
     */
    boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId);
}
