package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

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
     * Gets all the reviews of administrative units, submitted by the specified expert.
     * @param expertId The id of the expert.
     * @return A list of the expert's reviews.
     */
    @Override
    public List<AdminUnitReview> getByExpertId(Integer expertId) {
        return listNamedQuery("getAdminUnitReviewsByExpertId", "expertId", expertId);
    }
}
