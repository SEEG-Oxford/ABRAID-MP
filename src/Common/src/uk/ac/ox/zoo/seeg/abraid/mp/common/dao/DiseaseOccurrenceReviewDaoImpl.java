package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

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
     * Gets all reviews by one expert.
     * @param expertId The expert's Id.
     * @return A list of the expert's reviews.
     */
    @Override
    public List<DiseaseOccurrenceReview> getByExpertIdAndDiseaseGroupId(Integer expertId, Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrenceReviewsByExpertIdAndDiseaseGroupId",
                "expertId", expertId, "diseaseGroupId", diseaseGroupId);
    }
}
