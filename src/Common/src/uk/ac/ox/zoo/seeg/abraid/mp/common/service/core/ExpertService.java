package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

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
     * Gets an expert by ID.
     * @param expertId The ID of the expert.
     * @return The expert, or null if not found.
     */
    Expert getExpertById(int expertId);

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    Expert getExpertByEmail(String email);

    /**
     * Gets the specified expert's disease interests.
     *
     * @param expertId The id of the specified expert.
     * @return The list of validator disease groups the expert can validate.
     */
    List<ValidatorDiseaseGroup> getDiseaseInterests(Integer expertId);

    /**
     * Gets a list of occurrence points, for the specified validator disease group, for which the specified expert has
     * not yet submitted a review. Only SEEG users may view occurrences of disease groups during setup phase.
     * Other external users may only view occurrences of disease groups with automatic model runs enabled.
     * @param expertId The id of the specified expert.
     * @param userIsSeeg Whether the expert is a member of SEEG, and therefore should review more occurrences.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or validatorDiseaseGroupId cannot be found in the
     * database.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId, boolean userIsSeeg,
                                                                         Integer validatorDiseaseGroupId)
            throws IllegalArgumentException;

    /**
     * Gets the number of disease occurrence reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of disease occurrence reviews for the specified expert.
     */
    Long getDiseaseOccurrenceReviewCount(Integer expertId);

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param expertId The id of the specified expert.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @return True if the review already exists, otherwise false.
     */
    boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId);

    /**
     * Gets all reviews for the specified disease group, including repeat reviews.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of reviews.
     */
    List<AdminUnitReview> getAllAdminUnitReviewsForDiseaseGroup(Integer diseaseGroupId);

    /**
     * Gets all reviews submitted by the specified expert, for the specified disease group.
     * @param expertId The id of the specified expert.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of reviews.
     */
    List<AdminUnitReview> getAllAdminUnitReviewsForDiseaseGroup(Integer expertId, Integer diseaseGroupId);

    /**
     * Creates a new PasswordResetRequest entry in the database. This will also delete any other PasswordResetRequest
     * for this user and any PasswordResetRequests older than 24 hours.
     * @param email The email address of the associated expert.
     * @param key The key used to secure the PasswordResetRequest.
     * @return The ID of the new PasswordResetRequest.
     */
    Integer createAndSavePasswordResetRequest(String email, String key);

    /**
     * Gets a PasswordResetRequest by id.
     * This will also delete any PasswordResetRequests older than 24 hours.
     * @param id The id.
     * @return The PasswordResetRequest.
     */
    PasswordResetRequest getPasswordResetRequest(Integer id);

    /**
     * Verifies a PasswordResetRequest key against the stored key hash for a PasswordResetRequest identified by its id.
     * This will also delete any PasswordResetRequests older than 24 hours.
     * @param id The id of the PasswordResetRequest to check against.
     * @param key The key to check.
     * @return true if the check was successful.
     */
    boolean checkPasswordResetRequest(Integer id, String key);

    /**
     * Deletes a PasswordResetRequest.
     * This will also delete any PasswordResetRequests older than 24 hours.
     * @param passwordResetRequest The PasswordResetRequest.
     */
    void deletePasswordResetRequest(PasswordResetRequest passwordResetRequest);

    /**
     * Gets the number of admin unit reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of admin unit reviews for the specified expert.
     */
    Long getAdminUnitReviewCount(Integer expertId);

    /**
     * Gets a page worth of publicly visible experts.
     * @param pageNumber The page number to return.
     * @param pageSize The size of the pages to split the visible experts into.
     * @return A page worth of publicly visible experts
     */
    List<Expert> getPageOfPubliclyVisibleExperts(int pageNumber, int pageSize);

    /**
     * Gets a count of the publicly visible experts.
     * @return The count.
     */
    long getCountOfPubliclyVisibleExperts();

    /**
     * Gets the date of the last review submitted by a specific expert.
     * @param expertId The expert's Id.
     * @return The date of the last review.
     */
    DateTime getLastReviewDate(Integer expertId);

    /**
     * Saves the disease occurrence review.
     * @param expertId The id of the expert providing review.
     * @param occurrenceId The id of the disease occurrence.
     * @param response The expert's response.
     */
    void saveDiseaseOccurrenceReview(Integer expertId, Integer occurrenceId,
                                     DiseaseOccurrenceReviewResponse response);

    /**
     * Saves the review of the administrative unit.
     * @param expertId The id of the expert providing review.
     * @param diseaseGroupId The id of the disease group.
     * @param gaulCode The (global or tropical) gaulCode of the administrative unit.
     * @param response The expert's response.
     */
    void saveAdminUnitReview(Integer expertId, Integer diseaseGroupId, Integer gaulCode, DiseaseExtentClass response);

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void saveExpert(Expert expert);
}
