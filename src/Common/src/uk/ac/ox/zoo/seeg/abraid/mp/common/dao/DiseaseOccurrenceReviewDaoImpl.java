package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;

import java.util.List;

/**
 * The DiseaseOccurrenceReview entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseOccurrenceReviewDaoImpl extends AbstractDao<DiseaseOccurrenceReview, Integer>
        implements DiseaseOccurrenceReviewDao {
    // HQL fragments used to build queries to obtain disease occurrence reviews
    private static final String REVIEWS_FOR_MODEL_RUN_PREP_QUERY =
            "from DiseaseOccurrenceReview " +
            "where diseaseOccurrence.diseaseGroup.id = :diseaseGroupId " +
            "and diseaseOccurrence.status = '" + DiseaseOccurrenceStatus.IN_REVIEW + "' ";

    private static final String OCCURRENCES_WITH_NEW_REVIEWS_CLAUSE =
            "and diseaseOccurrence.id in " +
            "   (select r.diseaseOccurrence.id" +
            "    from DiseaseOccurrenceReview r" +
            "    where r.createdDate > :lastModelRunPrepDate)";

    public DiseaseOccurrenceReviewDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets all reviews (for all time) for the disease occurrences which are in review and have new reviews.
     * @param lastModelRunPrepDate The date on which the disease occurrence reviews were last retrieved, or null to
     *                             retrieve all reviews.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrence reviews.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DiseaseOccurrenceReview> getDiseaseOccurrenceReviewsForModelRunPrep(DateTime lastModelRunPrepDate,
                                                                                    Integer diseaseGroupId) {
        String queryString = REVIEWS_FOR_MODEL_RUN_PREP_QUERY;

        if (lastModelRunPrepDate != null) {
            queryString += OCCURRENCES_WITH_NEW_REVIEWS_CLAUSE;
        }

        Query query = currentSession().createQuery(queryString);
        query.setParameter("diseaseGroupId", diseaseGroupId);
        if (lastModelRunPrepDate != null) {
            query.setParameter("lastModelRunPrepDate", lastModelRunPrepDate);
        }

        return query.list();
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
