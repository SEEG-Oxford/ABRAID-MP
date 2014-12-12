package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.List;

/**
 * The DiseaseOccurrence entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseOccurrenceDaoImpl extends AbstractDao<DiseaseOccurrence, Integer> implements DiseaseOccurrenceDao {
    // HQL fragments used to build queries to obtain disease occurrences
    private static final String DISEASE_EXTENT_QUERY = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
            "where d.diseaseGroup.id = :diseaseGroupId " +
            "and d.status = 'READY' " +
            "and d.location.adminUnitGlobalGaulCode is not null " +
            "and d.location.adminUnitTropicalGaulCode is not null ";

    private static final String DISEASE_EXTENT_WEIGHTING_CLAUSE =
            "and d.finalWeighting is not null " +
            "and (d.validationWeighting is null or d.validationWeighting >= :minimumValidationWeighting) ";

    private static final String DISEASE_EXTENT_OCCURRENCE_DATE_CLAUSE =
            "and d.occurrenceDate >= :minimumOccurrenceDate ";

    private static final String GOLD_STANDARD_OCCURRENCES_CLAUSE =
            "and d.alert in (from Alert where feed.provenance.name = '" + ProvenanceNames.MANUAL_GOLD_STANDARD + "') ";

    private static final String MODEL_RUN_REQUEST_QUERY = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
            "where d.diseaseGroup.id = :diseaseGroupId " +
            "and d.status = 'READY' " +
            "and d.location.precision <> 'COUNTRY' ";

    private static final String MODEL_RUN_REQUEST_FINAL_WEIGHTING_ABOVE_ZERO_CLAUSE =
            "and d.finalWeighting > 0 ";

    private static final String MODEL_RUN_REQUEST_ORDER_BY_CLAUSE =
            "order by d.occurrenceDate desc";

    private static final int WEEKS_AGO_FOR_TRAINING_DATA_CUT_OFF_DATE = 4;

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

    /**
     * Gets all disease occurrences for the specified disease group.
     * @param diseaseGroupId The disease group's ID.
     * @return All disease occurrences for the specified disease group.
     */
    @Override
    public List<DiseaseOccurrence> getByDiseaseGroupId(int diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesByDiseaseGroupId", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets all disease occurrences for the specified disease group and occurrence statuses.
     * @param diseaseGroupId The disease group's ID.
     * @param statuses One or more disease occurrence statuses.
     * @return All disease occurrences for the specified disease group and statuses.
     */
    public List<DiseaseOccurrence> getByDiseaseGroupIdAndStatuses(int diseaseGroupId,
                                                                  DiseaseOccurrenceStatus... statuses) {
        List<DiseaseOccurrenceStatus> statusesList = Arrays.asList(statuses);
        return listNamedQuery("getDiseaseOccurrencesByDiseaseGroupIdAndStatuses", "diseaseGroupId", diseaseGroupId,
                "statuses", statusesList);
    }

    /**
     * Gets a list of occurrence points, for the specified validator disease group, for which the specified expert has
     * not yet submitted a review. Only SEEG users may view occurrences of disease groups during setup phase.
     * Other external users may only view occurrences of disease groups with automatic model runs enabled.
     * @param expertId The id of the specified expert.
     * @param userIsSeeg Whether the expert is a member of SEEG, and therefore should review more occurrences.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     */
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId, boolean userIsSeeg,
                                                                        Integer validatorDiseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesYetToBeReviewedByExpert",
                "expertId", expertId, "userIsSeeg", userIsSeeg, "validatorDiseaseGroupId", validatorDiseaseGroupId);
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
     * value, and must have a final weighting. If null, the validation and final weightings are ignored.
     * @param minimumOccurrenceDate All disease occurrences must have an occurrence date after this value. If null,
     * the occurrence date is ignored.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise
     * false.
     * @return A list of disease occurrences.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<DiseaseOccurrence> getDiseaseOccurrencesForDiseaseExtent(
            Integer diseaseGroupId, Double minimumValidationWeighting, DateTime minimumOccurrenceDate,
            boolean onlyUseGoldStandardOccurrences) {
        String queryString = DISEASE_EXTENT_QUERY;

        if (minimumValidationWeighting != null) {
            queryString += DISEASE_EXTENT_WEIGHTING_CLAUSE;
        }
        if (minimumOccurrenceDate != null) {
            queryString += DISEASE_EXTENT_OCCURRENCE_DATE_CLAUSE;
        }
        if (onlyUseGoldStandardOccurrences) {
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
     * Gets disease occurrences currently in validation, for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return A list of disease occurrences currently being validated by experts.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesInValidation(Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesInValidation", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets disease occurrences for the specified disease group which are yet to have a final weighting assigned.
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
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be retrieved, otherwise
     * false.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId,
                                                                           boolean onlyUseGoldStandardOccurrences) {
        String queryString = MODEL_RUN_REQUEST_QUERY;
        if (onlyUseGoldStandardOccurrences) {
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
     * Gets the number of distinct locations from the new disease occurrences for the specified disease group.
     * A "new" occurrence has status READY or IN_REVIEW, and a suitable created_date.
     * Occurrences must additionally satisfy that environmental suitability and distance from disease extent values are
     * greater than minimum specified on the disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @param startDate Occurrences must be newer than this date.
     * @param endDate Occurrences must be older than this date, to ensure they have had ample time in validation.
     * @return The number of locations.
     */
    @Override
    public long getDistinctLocationsCountForTriggeringModelRun(Integer diseaseGroupId,
                                                               DateTime startDate, DateTime endDate) {
        Query query = getParameterisedNamedQuery("getDistinctLocationsCountForTriggeringModelRun",
                "diseaseGroupId", diseaseGroupId, "startDate", startDate, "endDate", endDate);
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

    /**
     * Gets a list of disease occurrences for validation batching, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param batchStartDate The start date of the batch.
     * @param batchEndDate The end date of the batch.
     * @return A list of disease occurrences.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForBatching(int diseaseGroupId,
                                                                    DateTime batchStartDate, DateTime batchEndDate) {
        return listNamedQuery("getDiseaseOccurrencesForBatching", "diseaseGroupId", diseaseGroupId,
                "batchStartDate", batchStartDate, "batchEndDate", batchEndDate);
    }

    /**
     * Gets a list of recent disease occurrences that have been validated (they have a target expert weighting).
     * @param diseaseGroupId The disease group ID.
     * @return A list of disease occurrences.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForTrainingPredictor(int diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesForTrainingPredictor", "diseaseGroupId", diseaseGroupId,
                "cutOffDate", DateTime.now().minusWeeks(WEEKS_AGO_FOR_TRAINING_DATA_CUT_OFF_DATE));
    }

    /**
     * Gets the number of occurrences that are eligible for being sent to the model, between the start and end dates.
     * This helps to estimate whether the number of occurrences in a batch will be sufficient for a model run.
     * @param diseaseGroupId The disease group ID.
     * @param startDate The start date.
     * @param endDate The end date.
     * @return The number of occurrences that are eligible for being sent to the model. This is all occurrences
     * except those that have been discarded, or points with COUNTRY precision.
     */
    @Override
    public long getNumberOfOccurrencesEligibleForModelRun(int diseaseGroupId, DateTime startDate, DateTime endDate) {
        Query query = getParameterisedNamedQuery("getNumberOfDiseaseOccurrencesEligibleForModelRun", "diseaseGroupId",
                diseaseGroupId, "startDate", startDate, "endDate", endDate);
        return (long) query.uniqueResult();
    }
}
