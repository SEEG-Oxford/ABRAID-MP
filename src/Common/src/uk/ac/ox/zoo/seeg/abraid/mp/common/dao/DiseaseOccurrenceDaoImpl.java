package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * The DiseaseOccurrence entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseOccurrenceDaoImpl extends AbstractDao<DiseaseOccurrence, Integer> implements DiseaseOccurrenceDao {
    public DiseaseOccurrenceDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     */
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId,
                                                                        Integer validatorDiseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesYetToBeReviewed",
                "expertId", expertId, "validatorDiseaseGroupId", validatorDiseaseGroupId);
    }


    /**
     * Get disease occurrences that match the specified disease group, location, alert and occurrence start date.
     * Used to check for the existence of a disease occurrence.
     * @param diseaseGroup The disease group.
     * @param location The location.
     * @param alert The alert.
     * @param occurrenceDate The occurrence date.
     * @return A list of matching disease occurrences.
     */
    public List<DiseaseOccurrence> getDiseaseOccurrencesForExistenceCheck(DiseaseGroup diseaseGroup,
                                                                          Location location, Alert alert,
                                                                          DateTime occurrenceDate) {
        return listNamedQuery("getDiseaseOccurrencesForExistenceCheck",
                "diseaseGroup", diseaseGroup, "location", location, "alert", alert,
                "occurrenceDate", occurrenceDate);
    }

    /**
     * Gets disease occurrences for generating the disease extent for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param minimumValidationWeighting All disease occurrences must have a validation weighting greater than this
     *                                   value.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value.
     * @param feedIds All disease occurrences must result from one of these feeds. If feed IDs is null or zero,
     *                accepts all feeds.
     * @param isGlobal True if the disease group is global, otherwise false.
     * @return A list of disease occurrences.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DiseaseOccurrenceForDiseaseExtent> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            List<Integer> feedIds, boolean isGlobal) {
        // Restrict to the given list of feed IDs (if any)
        boolean includeFeedIds = (feedIds != null && feedIds.size() > 0);

        Query query = namedQuery(includeFeedIds ? "getDiseaseOccurrencesForDiseaseExtentByFeedIds" :
                                                  "getDiseaseOccurrencesForDiseaseExtent");

        query.setParameter("diseaseGroupId", diseaseGroupId);
        query.setParameter("minimumValidationWeighting", minimumValidationWeighting);
        query.setParameter("minimumOccurrenceDate", minimumOccurrenceDate);
        query.setParameter("isGlobal", isGlobal);
        if (includeFeedIds) {
            query.setParameterList("feedIds", feedIds);
        }

        return query.list();
    }


    /**
     * Gets disease occurrences for the model run.
     * @param diseaseGroupId The ID of the disease group.
     * @return Disease occurrences for the model run.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRun(Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesForModelRun", "diseaseGroupId", diseaseGroupId);
    }
}
