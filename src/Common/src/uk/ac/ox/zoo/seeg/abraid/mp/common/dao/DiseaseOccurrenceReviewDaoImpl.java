package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
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
     * Gets all reviews by the specified expert.
     * @param expertId The expert's Id.
     * @return A list of the expert's reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getByExpertId(Integer expertId) {
        return listNamedQuery("getDiseaseOccurrenceReviewsByExpertId", "expertId", expertId);
    }

    /**
     * Gets all reviews by the specified expert, for the specified disease group.
     * @param expertId The expert's Id.
     * @param diseaseGroupId The disease group's Id.
     * @return A list of the expert's reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getByExpertIdAndDiseaseGroupId(Integer expertId, Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrenceReviewsByExpertIdAndDiseaseGroupId",
                "expertId", expertId, "diseaseGroupId", diseaseGroupId);
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
