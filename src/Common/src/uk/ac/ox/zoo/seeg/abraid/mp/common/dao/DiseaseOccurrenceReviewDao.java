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
     * Gets all reviews by one expert.
     * @param expertId The expert's Id.
     * @return A list of the expert's reviews.
     */
    List<DiseaseOccurrenceReview> getByExpertId(Integer expertId);

    /**
     * Gets all reviews by one expert.
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
}
