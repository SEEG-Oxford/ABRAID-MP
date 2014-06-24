package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;

import java.util.List;

/**
 * Interface for the AdminUnitReview entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitReviewDao {
    /**
     * Gets the AdminUnitReview by its id.
     * @param id The id of the review.
     * @return The review.
     */
    AdminUnitReview getById(Integer id);

    /**
     * Gets the total number of reviews submitted by the specified expert.
     * @param expertId The expert's Id.
     * @return The count of the expert's reviews.
     */
    Long getCountByExpertId(Integer expertId);

    /**
     * Gets all the reviews of administrative units, submitted by the specified expert.
     * @param expertId The id of the expert.
     * @return A list of the expert's reviews.
     */
    List<AdminUnitReview> getByExpertId(Integer expertId);

    /**
     * Gets all the reviews of administrative units, for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the reviews for the disease group.
     */
    List<AdminUnitReview> getByDiseaseGroupId(Integer diseaseGroupId);

    /**
     * Gets all the reviews of administrative units, submitted by the specified expert, for the specified disease group.
     * @param expertId The id of the expert.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the expert's reviews for the disease group.
     */
    List<AdminUnitReview> getByExpertIdAndDiseaseGroupId(Integer expertId, Integer diseaseGroupId);

    /**
     * Gets the review, defined by the unique triplet of input arguments, if it exists in the database.
     * @param expertId The id of the expert.
     * @param diseaseGroupId The id of the disease group.
     * @param gaulCode The gaulCode of the administrative unit.
     * @return The adminUnitReview if it exists, otherwise null.
     */
    AdminUnitReview getAdminUnitReview(Integer expertId, Integer diseaseGroupId, Integer gaulCode);

    /**
     * Saves the specified review.
     * @param adminUnitReview The review to save.
     */
    void save(AdminUnitReview adminUnitReview);
}
