package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;

import java.util.List;

/**
 * The DiseaseOccurrenceReview entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseOccurrenceReviewDaoImpl extends AbstractDao<DiseaseOccurrenceReview, Integer>
        implements DiseaseOccurrenceReviewDao {
    public DiseaseOccurrenceReviewDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets all reviews (for all time) for the disease occurrences which have new reviews.
     * @param lastRetrievalDate The date on which the disease occurrence reviews were last retrieved.
     * @return A list of the reviews of disease occurrences whose weightings needs updating.
     */
    @Override
    public List<DiseaseOccurrenceReview> getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(
            LocalDateTime lastRetrievalDate) {
        return listNamedQuery("getAllDiseaseOccurrencesReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval",
                "lastRetrievalDate", lastRetrievalDate);
    }


    /**
     * Gets the total number of reviews submitted by the specified expert.
     * @param expertId The expert's Id.
     * @return The count of the expert's reviews.
     */
    @Override
    public Long getCountByExpertId(Integer expertId) {
        Query query = getParameterisedNamedQuery("getDiseaseOccurrenceReviewCountByExpertId", "expertId", expertId);
        return (Long) query.uniqueResult();
    }

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param expertId The id of the specified expert.
     * @return True if the review already exists, otherwise false.
     */
    @Override
    public boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId) {
        Query query = getParameterisedNamedQuery("getDiseaseOccurrenceReviewByExpertIdAndDiseaseOccurrenceId",
                "expertId", expertId, "diseaseOccurrenceId", diseaseOccurrenceId);
        return query.uniqueResult() != null;
    }
}
