package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

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
     * Gets the last completed model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getLastCompletedModelRun(int diseaseGroupId) {
        return firstOrNull(listNamedQuery("getLastCompletedModelRun", "diseaseGroupId", diseaseGroupId));
    }

    private ModelRun firstOrNull(List<ModelRun> modelRuns) {
        return (modelRuns != null && modelRuns.size() > 0) ? modelRuns.get(0) : null;
    }
}
