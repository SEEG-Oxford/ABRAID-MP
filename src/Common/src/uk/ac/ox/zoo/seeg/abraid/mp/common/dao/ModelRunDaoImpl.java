package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Collection;
import java.util.List;

/**
 * The ModelRun entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDaoImpl extends AbstractDao<ModelRun, Integer> implements ModelRunDao {
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
        return firstOrNull(listNamedQuery("getMostRecentlyRequestedModelRunWhichCompleted", "diseaseGroupId", diseaseGroupId));
    }

    /**
     * Gets the last completed model run (by response date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getMostRecentlyFinishedModelRunWhichCompleted(int diseaseGroupId) {
        return firstOrNull(listNamedQuery("getMostRecentlyFinishedModelRunWhichCompleted", "diseaseGroupId", diseaseGroupId));
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

    private ModelRun firstOrNull(List<ModelRun> modelRuns) {
        return (modelRuns != null && modelRuns.size() > 0) ? modelRuns.get(0) : null;
    }
}
