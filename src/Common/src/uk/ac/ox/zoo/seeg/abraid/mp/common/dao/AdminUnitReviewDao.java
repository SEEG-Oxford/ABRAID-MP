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
     * Gets all the reviews of administrative units, submitted by the specified expert.
     * @param expertId The id of the expert.
     * @return A list of the expert's reviews.
     */
    List<AdminUnitReview> getByExpertId(Integer expertId);

    /**
     * Saves the specified review.
     * @param adminUnitReview The review to save.
     */
    void save(AdminUnitReview adminUnitReview);
}
