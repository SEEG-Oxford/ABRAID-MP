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
     * Gets all reviews by one expert.
     * @param expertId The expert's Id.
     * @return A list of the expert's reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getByExpertId(Integer expertId) {
        return listNamedQuery("getDiseaseOccurrenceReviewsByExpertId", "expertId", expertId);
    }

    /**
     * Gets all reviews by one expert for one disease group.
     * @param expertId The expert's Id.
     * @param diseaseGroupId The diseaseGroup's Id.
     * @return A list of the expert's reviews for one disease group.
     */
    @Override
    public List<DiseaseOccurrenceReview> getByExpertIdAndDiseaseGroupId(Integer expertId, Integer diseaseGroupId) {
        Query query = namedQuery("getDiseaseOccurrenceReviewsByExpertIdAndDiseaseGroupId");
        query.setParameter("expertId", expertId);
        query.setParameter("diseaseGroupId", diseaseGroupId);
        return list(query);
    }
}
