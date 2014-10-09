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
    // HQL fragments used to build queries to obtain disease occurrences
    private static final String DISEASE_EXTENT_QUERY =
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

    private static final String GOLD_STANDARD_OCCURRENCES_CLAUSE =
            "and d.finalWeighting = " + DiseaseOccurrence.GOLD_STANDARD_FINAL_WEIGHTING + " " +
            "and d.alert in (from Alert where feed.provenance.name = '" + ProvenanceNames.UPLOADED + "') ";

    private static final String MODEL_RUN_REQUEST_QUERY = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
            "where d.diseaseGroup.id = :diseaseGroupId " +
            "and d.isValidated = true ";

    private static final String MODEL_RUN_REQUEST_FINAL_WEIGHTING_ABOVE_ZERO_CLAUSE =
            "and d.finalWeighting > 0 ";

    private static final String MODEL_RUN_REQUEST_ORDER_BY_CLAUSE =
            "order by d.occurrenceDate desc";

    public DiseaseOccurrenceDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets disease occurrences with the specified IDs.
     * @param diseaseOccurrenceIds The disease occurrence IDs.
     * @return The disease occurrences with the specified IDs.
     */
    @Override
    public List<DiseaseOccurrence> getByIds(List<Integer> diseaseOccurrenceIds) {
        return listNamedQuery("getDiseaseOccurrencesByIds", "diseaseOccurrenceIds", diseaseOccurrenceIds);
    }

    @Override
    public List<DiseaseOccurrence> getByDiseaseGroupId(int diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesByDiseaseGroupId", "diseaseGroupId", diseaseGroupId);
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
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise false.
     * @return A list of disease occurrences.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DiseaseOccurrence> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            boolean isGlobal, boolean useGoldStandardOccurrences) {
        String queryString = DISEASE_EXTENT_QUERY;

        if (minimumValidationWeighting != null) {
            queryString += DISEASE_EXTENT_VALIDATION_WEIGHTING_CLAUSE;
        }
        if (minimumOccurrenceDate != null) {
            queryString += DISEASE_EXTENT_OCCURRENCE_DATE_CLAUSE;
        }
        if (useGoldStandardOccurrences) {
            queryString += GOLD_STANDARD_OCCURRENCES_CLAUSE;
        }

        Query query = currentSession().createQuery(queryString);
        query.setParameter("diseaseGroupId", diseaseGroupId);
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
     * @param mustHaveEnvironmentalSuitability True if the occurrence's environmental suitability must be non-null.
     *                                         False if it doesn't matter either way.
     * @return A list of disease occurrences that need their final weightings to be set.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
            Integer diseaseGroupId, boolean mustHaveEnvironmentalSuitability) {
        return listNamedQuery("getDiseaseOccurrencesYetToHaveFinalWeightingAssigned", "diseaseGroupId", diseaseGroupId,
                "mustHaveEnvironmentalSuitability", mustHaveEnvironmentalSuitability);
    }

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise false.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId,
                                                                           boolean useGoldStandardOccurrences) {
        String queryString = MODEL_RUN_REQUEST_QUERY;
        if (useGoldStandardOccurrences) {
            queryString += GOLD_STANDARD_OCCURRENCES_CLAUSE;
        } else {
            queryString += MODEL_RUN_REQUEST_FINAL_WEIGHTING_ABOVE_ZERO_CLAUSE;
        }
        queryString += MODEL_RUN_REQUEST_ORDER_BY_CLAUSE;

        Query query = currentSession().createQuery(queryString);
        query.setParameter("diseaseGroupId", diseaseGroupId);

        return list(query);
    }

    /**
     * Gets the list of new disease occurrences for the specified disease group.
     * A "new" occurrence has is_validated not null and a created_date that is more than a week ago.
     * Occurrence must additionally satisfy that environmental suitability and distance from disease extent values are
     * greater than minimum specified for the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param startDate Occurrences must be newer than this date.
     * @param endDate Occurrences must be older than this date, to ensure they have had ample time in validation.
     * @return The list of relevant new occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForTriggeringModelRun(Integer diseaseGroupId,
                                                                              DateTime startDate, DateTime endDate) {
        return listNamedQuery("getDiseaseOccurrencesForTriggeringModelRun",
                "diseaseGroupId", diseaseGroupId, "startDate", startDate, "endDate", endDate);
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

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DiseaseOccurrence> getOccurrencesForBatching(int diseaseGroupId, DateTime batchEndDate) {
        return listNamedQuery("getDiseaseOccurrencesForBatching", "diseaseGroupId", diseaseGroupId,
                "batchEndDate", batchEndDate);
    }
}
