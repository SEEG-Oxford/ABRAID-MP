package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;

import java.util.List;

/**
 * The AdminUnitReview entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitReviewDaoImpl extends AbstractDao<AdminUnitReview, Integer> implements AdminUnitReviewDao {
    public AdminUnitReviewDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets the total number of reviews submitted by the specified expert.
     * @param expertId The expert's Id.
     * @return The count of the expert's reviews.
     */
    @Override
    public Long getCountByExpertId(Integer expertId) {
        Query query = getParameterisedNamedQuery("getAdminUnitReviewCountByExpertId", "expertId", expertId);
        return (Long) query.uniqueResult();
    }

    /**
     * Gets all the reviews of administrative units, for the specified disease group, including repeat reviews.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the reviews for the disease group.
     */
    @Override
    public List<AdminUnitReview> getByDiseaseGroupId(Integer diseaseGroupId) {
        return listNamedQuery("getAdminUnitReviewsByDiseaseGroupId", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets all the reviews of administrative units, submitted by the specified expert, for the specified disease group.
     * @param expertId The id of the expert.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the expert's reviews for the disease group.
     */
    @Override
    public List<AdminUnitReview> getByExpertIdAndDiseaseGroupId(Integer expertId, Integer diseaseGroupId) {
        return listNamedQuery("getAdminUnitReviewsByExpertIdAndDiseaseGroupId", "expertId", expertId, "diseaseGroupId",
                diseaseGroupId);
    }
}
