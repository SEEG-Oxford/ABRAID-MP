package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Collection;
import java.util.List;

/**
 * The ModelRun entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDaoImpl extends AbstractDao<ModelRun, Integer> implements ModelRunDao {
    private static final String GET_FILTERED_RUNS_BASE_QUERY =
            "select distinct mr " +
            "from ModelRun mr " +
            "join fetch mr.diseaseGroup dg " +
            "left outer join fetch mr.covariateInfluences ci " +
            "where mr.status = 'COMPLETED' " +
            "and dg.automaticModelRunsStartDate is not null " +
            "and mr.requestDate >= dg.automaticModelRunsStartDate ";
    private static final String GET_FILTERED_RUNS_ORDER =
            "order by mr.responseDate desc";
    private static final String NAME_FILTER =
            "and mr.name=:name ";
    private static final String DISEASE_GROUP_FILTER =
            "and dg.id=:diseaseGroupId ";
    private static final String MIN_RESPONSE_DATE_FILTER =
            "and mr.responseDate >= :minResponseDate ";
    private static final String MAX_RESPONSE_DATE_FILTER =
            "and mr.responseDate <= :maxResponseDate ";

    public ModelRunDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ModelRun getByName(String name) {
        return uniqueResultNamedQuery("getModelRunByName", "name", name);
    }

    /**
     * Gets the last requested model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last requested model run, or null if there are no model runs.
     */
    @Override
    public ModelRun getLastRequestedModelRun(int diseaseGroupId) {
        return firstOrNull(listNamedQuery("getLastRequestedModelRun", "diseaseGroupId", diseaseGroupId));
    }

    /**
     * Gets the last completed model run (by request date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getMostRecentlyRequestedModelRunWhichCompleted(int diseaseGroupId) {
        return firstOrNull(listNamedQuery(
                "getMostRecentlyRequestedModelRunWhichCompleted", "diseaseGroupId", diseaseGroupId));
    }

    /**
     * Gets the last completed model run (by response date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getMostRecentlyFinishedModelRunWhichCompleted(int diseaseGroupId) {
        return firstOrNull(listNamedQuery(
                "getMostRecentlyFinishedModelRunWhichCompleted", "diseaseGroupId", diseaseGroupId));
    }


    /**
     * Returns whether or not disease occurrence batching has ever completed for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return True if batching has completed at least once for this disease group, otherwise false.
     */
    @Override
    public boolean hasBatchingEverCompleted(int diseaseGroupId) {
        Query query = getParameterisedNamedQuery("hasBatchingEverCompleted", "diseaseGroupId", diseaseGroupId);
        long count = (long) query.uniqueResult();
        return (count > 0);
    }

    /**
     * Gets all the completed model runs of disease groups in setup, and - for disease groups not in setup - gets all
     * the completed model runs requested after automatic model runs were enabled.
     * @return The completed model runs to be displayed on Atlas.
     */
    @Override
    public Collection<ModelRun> getCompletedModelRunsForDisplay() {
        return listNamedQuery("getCompletedModelRunsForDisplay");
    }

    /**
     * Gets all of the servers that have been used for model runs, first sorted by the number of active model runs,
     * then sorted by the number of inactive model runs. Sorted by ascending usage.
     * @return The ordered list of servers.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getModelRunRequestServersByUsage() {
        Query query = namedQuery("getModelRunRequestServersByUsage");
        return query.list();
    }

    /**
     * Gets all the model runs for the given disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return All the model runs for the given disease group
     */
    @Override
    public Collection<ModelRun> getModelRunsForDiseaseGroup(int diseaseGroupId) {
        return listNamedQuery("getModelRunsForDiseaseGroup", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets a filtered subset of the model runs (completed - automatic).
     * @param name The name to filter on, or null.
     * @param diseaseGroupId The disease to filter on, or null.
     * @param minResponseDate The min response date to filter on, or null.
     * @param maxResponseDate The max response date to filter on, or null.
     * @return A filtered list of model runs.
     */
    @Override
    public List<ModelRun> getFilteredModelRuns(String name, Integer diseaseGroupId,
                                                     LocalDate minResponseDate, LocalDate maxResponseDate) {
        String queryString = GET_FILTERED_RUNS_BASE_QUERY;
        if (name != null) {
            queryString += NAME_FILTER;
        }
        if (diseaseGroupId != null) {
            queryString += DISEASE_GROUP_FILTER;
        }
        if (minResponseDate != null) {
            queryString += MIN_RESPONSE_DATE_FILTER;
        }
        if (maxResponseDate != null) {
            queryString += MAX_RESPONSE_DATE_FILTER;
        }

        queryString += GET_FILTERED_RUNS_ORDER;

        Query query = currentSession().createQuery(queryString);

        if (name != null) {
            query.setParameter("name", name);
        }
        if (diseaseGroupId != null) {
            query.setParameter("diseaseGroupId", diseaseGroupId);
        }
        if (minResponseDate != null) {
            query.setParameter("minResponseDate", minResponseDate.toDateTimeAtStartOfDay());
        }
        if (maxResponseDate != null) {
            query.setParameter("maxResponseDate", maxResponseDate.toDateTimeAtStartOfDay().plusDays(1).minusMillis(1));
        }

        return list(query);
    }

    private ModelRun firstOrNull(List<ModelRun> modelRuns) {
        return (modelRuns != null && modelRuns.size() > 0) ? modelRuns.get(0) : null;
    }
}
