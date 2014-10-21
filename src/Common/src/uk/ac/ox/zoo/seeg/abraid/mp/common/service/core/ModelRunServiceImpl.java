package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Collection;

/**
 * Service interface for model run inputs and outputs.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ModelRunServiceImpl implements ModelRunService {
    private ModelRunDao modelRunDao;

    private static final int DAYS_BETWEEN_MODEL_RUNS = 7;

    public ModelRunServiceImpl(ModelRunDao modelRunDao) {
        this.modelRunDao = modelRunDao;
    }

    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if no model run.
     */
    public ModelRun getModelRunByName(String name) {
        return modelRunDao.getByName(name);
    }

    /**
     * Saves a model run.
     * @param modelRun The model run to save.
     */
    public void saveModelRun(ModelRun modelRun) {
        modelRunDao.save(modelRun);
    }

    /**
     * Gets the latest requested model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest requested model run, or null if there are no model runs.
     */
    @Override
    public ModelRun getLastRequestedModelRun(int diseaseGroupId) {
        return modelRunDao.getLastRequestedModelRun(diseaseGroupId);
    }

    /**
     * Gets the latest completed model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getLastCompletedModelRun(int diseaseGroupId) {
        return modelRunDao.getLastCompletedModelRun(diseaseGroupId);
    }

    @Override
    public boolean hasBatchingEverCompleted(int diseaseGroupId) {
        return modelRunDao.hasBatchingEverCompleted(diseaseGroupId);
    }

    /**
     * Returns the input date, with the number of days between scheduled model runs subtracted.
     * @param dateTime The input date.
     * @return The input date minus the number of days between scheduled model runs.
     */
    @Override
    public DateTime subtractDaysBetweenModelRuns(DateTime dateTime) {
        return dateTime.minusDays(DAYS_BETWEEN_MODEL_RUNS).withTimeAtStartOfDay();
    }

    /**
     * Gets all of the completed model runs.
     * @return The completed model runs.
     */
    @Override
    public Collection<ModelRun> getCompletedModelRuns() {
        return modelRunDao.getCompletedModelRuns();
    }
}
