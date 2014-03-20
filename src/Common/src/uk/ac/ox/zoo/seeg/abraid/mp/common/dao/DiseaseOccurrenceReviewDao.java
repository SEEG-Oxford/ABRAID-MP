package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;

import java.util.List;

/**
 * Interface for the DiseaseOccurrenceReview entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseOccurrenceReviewDao {
    /**
     * Gets all reviews for all experts.
     * @return A list of all reviews by all experts.
     */
    List<DiseaseOccurrenceReview> getAll();

    /**
     * Gets all reviews by the specified expert.
     * @param expertId The expert's Id.
     * @return A list of the expert's reviews.
     */
    List<DiseaseOccurrenceReview> getByExpertId(Integer expertId);

    /**
     * Gets all reviews by the specified expert, for the specified disease group.
     * @param expertId The expert's Id.
     * @param diseaseGroupId The disease group's Id.
     * @return A list of the expert's reviews.
     */
    List<DiseaseOccurrenceReview> getByExpertIdAndDiseaseGroupId(Integer expertId, Integer diseaseGroupId);

    /**
     * Saves the specified review.
     * @param diseaseOccurrenceReview The review to save.
     */
    void save(DiseaseOccurrenceReview diseaseOccurrenceReview);

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert, already exists in the database.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param expertId The id of the specified expert.
     * @return True if the review already exists, otherwise false.
     */
    boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId);
}
