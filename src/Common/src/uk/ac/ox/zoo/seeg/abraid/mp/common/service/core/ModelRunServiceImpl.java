package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Collection;
import java.util.List;

/**
 * Service interface for model run inputs and outputs.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ModelRunServiceImpl implements ModelRunService {
    private final ModelRunDao modelRunDao;

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
     * Gets the latest completed model run (by request date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getMostRecentlyRequestedModelRunWhichCompleted(int diseaseGroupId) {
        return modelRunDao.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId);
    }

    /**
     * Gets the latest completed model run (by response date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest completed model run, or null if there are no completed model runs.
     */
    @Override
    public ModelRun getMostRecentlyFinishedModelRunWhichCompleted(int diseaseGroupId) {
        return modelRunDao.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);
    }

    @Override
    public boolean hasBatchingEverCompleted(int diseaseGroupId) {
        return modelRunDao.hasBatchingEverCompleted(diseaseGroupId);
    }

    /**
     * Gets all the completed model runs of disease groups in setup, and - for disease groups not in setup - gets all
     * the completed model runs requested after automatic model runs were enabled.
     * @return The completed model runs to be displayed on Atlas.
     */
    @Override
    public Collection<ModelRun> getCompletedModelRunsForDisplay() {
        return modelRunDao.getCompletedModelRunsForDisplay();
    }

    /**
     * Gets all of the servers that have been used for model runs, first sorted by the number of active model runs,
     * then sorted by the number of inactive model runs. Sorted by ascending usage.
     * @return The ordered list of servers.
     */
    @Override
    public List<String> getModelRunRequestServersByUsage() {
        return modelRunDao.getModelRunRequestServersByUsage();
    }

    /**
     * Gets all the model runs for the given disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return All the model runs for the given disease group
     */
    @Override
    public Collection<ModelRun> getModelRunsForDiseaseGroup(int diseaseGroupId) {
        return modelRunDao.getModelRunsForDiseaseGroup(diseaseGroupId);
    }
}
