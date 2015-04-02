package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
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
     * Gets the total number of reviews submitted by the specified expert, including repeat reviews. The (expert,
     * disease group, admin unit) triplet is not unique; an expert can submit another review if the class has changed.
     * @param expertId The expert's Id.
     * @return The count of the expert's reviews.
     */
    Long getCountByExpertId(Integer expertId);

    /**
     * Gets all the reviews of administrative units, for the specified disease group, including repeat reviews.
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
     * Gets the date of the last admin unit review submitted by a specific expert, disease group and gaul code.
     * @param expertId The expert's Id.
     * @param diseaseGroupId The disease group's Id.
     * @param gaulCode The admin unit's gaulCode.
     * @return The date of the last admin unit review.
     */
    DateTime getLastReviewDateByExpertIdAndDiseaseGroupIdAndGaulCode(
            Integer expertId, Integer diseaseGroupId, Integer gaulCode);

    /**
     * Gets the date of the last admin unit review submitted by a specific expert.
     * @param expertId The expert's Id.
     * @return The date of the last admin unit review.
     */
    DateTime getLastReviewDateByExpertId(Integer expertId);

    /**
     * Saves the specified review.
     * @param adminUnitReview The review to save.
     */
    void save(AdminUnitReview adminUnitReview);

}
