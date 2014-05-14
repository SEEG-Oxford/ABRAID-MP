package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.LocalDateTime;
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
     * Gets all reviews (for all time) for the disease occurrences which have new reviews.
     * @param lastRetrieval The date on which the disease occurrence reviews were last retrieved.
     * @return A list of the reviews of disease occurrences whose weightings needs updating.
     */
    List<DiseaseOccurrenceReview> getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(
            LocalDateTime lastRetrieval);

    /**
     * Gets the total number of reviews submitted by the specified expert.
     * @param expertId The expert's Id.
     * @return The count of the expert's reviews.
     */
    Long getCountByExpertId(Integer expertId);

    /**
     * Saves the specified review.
     * @param diseaseOccurrenceReview The review to save.
     */
    void save(DiseaseOccurrenceReview diseaseOccurrenceReview);

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param expertId The id of the specified expert.
     * @return True if the review already exists, otherwise false.
     */
    boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId);
}
