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
    // HQL fragments used to build a query to obtain disease occurrences for disease extent generation
    private static final String DISEASE_EXTENT_QUERY =
            "select new uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceForDiseaseExtent" +
            "       (d.occurrenceDate, case when :isGlobal = true then" +
            "        d.location.adminUnitGlobalGaulCode else d.location.adminUnitTropicalGaulCode end) " +
            "from DiseaseOccurrence d " +
            "where d.diseaseGroup.id = :diseaseGroupId " +
            "and d.isValidated = true " +
            "and d.finalWeighting is not null " +
            "and d.location.adminUnitGlobalGaulCode is not null " +
            "and d.location.adminUnitTropicalGaulCode is not null ";

    private static final String DISEASE_EXTENT_VALIDATION_WEIGHTING_CLAUSE =
            "and (d.validationWeighting is null or d.validationWeighting >= :minimumValidationWeighting) ";

    private static final String DISEASE_EXTENT_OCCURRENCE_DATE_CLAUSE =
            "and d.occurrenceDate >= :minimumOccurrenceDate ";

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
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId,
                                                                        Integer validatorDiseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesYetToBeReviewedByExpert",
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
     * value. If null, the validation weighting is ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param isGlobal True if the disease group is global, otherwise false.
     * @return A list of disease occurrences.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DiseaseOccurrenceForDiseaseExtent> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            boolean isGlobal) {
        String queryString = DISEASE_EXTENT_QUERY;

        if (minimumValidationWeighting != null) {
            queryString += DISEASE_EXTENT_VALIDATION_WEIGHTING_CLAUSE;
        }
        if (minimumOccurrenceDate != null) {
            queryString += DISEASE_EXTENT_OCCURRENCE_DATE_CLAUSE;
        }

        Query query = currentSession().createQuery(queryString);
        query.setParameter("diseaseGroupId", diseaseGroupId);
        query.setParameter("isGlobal", isGlobal);
        if (minimumValidationWeighting != null) {
            query.setParameter("minimumValidationWeighting", minimumValidationWeighting);
        }
        if (minimumOccurrenceDate != null) {
            query.setParameter("minimumOccurrenceDate", minimumOccurrenceDate);
        }

        return query.list();
    }

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is false.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesInValidation", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets disease occurrences for the specified disease group whose isValidated flag is true
     * and finalWeighting is currently null.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesYetToHaveFinalWeightingAssigned", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesForModelRunRequest", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets the number of new disease occurrences for the specified disease group. A "new" occurrence has is_validated
     * not null and a created_date that is more recent than the last_mode_run_prep_date for the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The count.
     */
    @Override
    public long getNewOccurrencesCountByDiseaseGroup(Integer diseaseGroupId) {
        Query query = getParameterisedNamedQuery("getNewOccurrencesCountByDiseaseGroup",
                "diseaseGroupId", diseaseGroupId);
        return (long) query.uniqueResult();
    }

    /**
     * Gets statistics about the occurrences of the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @return The statistics.
     */
    public DiseaseOccurrenceStatistics getDiseaseOccurrenceStatistics(int diseaseGroupId) {
        Query query = getParameterisedNamedQuery("getDiseaseOccurrenceStatistics", "diseaseGroupId", diseaseGroupId);
        return (DiseaseOccurrenceStatistics) query.uniqueResult();
    }
}
