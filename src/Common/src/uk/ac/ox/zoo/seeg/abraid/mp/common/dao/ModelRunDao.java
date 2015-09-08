package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Collection;
import java.util.List;

/**
 * Interface for the ModelRun entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunDao {
    /**
     * Gets all model runs.
     * @return All model runs.
     */
    List<ModelRun> getAll();

    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if it does not exist.
     */
    ModelRun getByName(String name);

    /**
     * Saves the specified model run.
     * @param modelRun The model run to save.
     */
    void save(ModelRun modelRun);

    /**
     * Gets the last requested model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last requested model run, or null if there are no model runs.
     */
    ModelRun getLastRequestedModelRun(int diseaseGroupId);

    /**
     * Gets the last completed model run (by request date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last completed model run, or null if there are no completed model runs.
     */
    ModelRun getMostRecentlyRequestedModelRunWhichCompleted(int diseaseGroupId);

    /**
     * Gets the last completed model run (by response date) for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The last completed model run, or null if there are no completed model runs.
     */
    ModelRun getMostRecentlyFinishedModelRunWhichCompleted(int diseaseGroupId);

    /**
     * Returns whether or not disease occurrence batching has ever completed for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return True if batching has completed at least once for this disease group, otherwise false.
     */
    boolean hasBatchingEverCompleted(int diseaseGroupId);

    /**
     * Gets all the completed model runs of disease groups in setup, and - for disease groups not in setup - gets all
     * the completed model runs requested after automatic model runs were enabled.
     * @return The completed model runs to be displayed on Atlas.
     */
    Collection<ModelRun> getCompletedModelRunsForDisplay();

    /**
     * Gets all of the servers that have been used for model runs, first sorted by the number of active model runs,
     * then sorted by the number of inactive model runs. Sorted by ascending usage.
     * @return The ordered list of servers.
     */
    List<String> getModelRunRequestServersByUsage();

    /**
     * Gets all the model runs for the given disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return All the model runs for the given disease group
     */
    Collection<ModelRun> getModelRunsForDiseaseGroup(int diseaseGroupId);

    /**
     * Gets a filtered subset of the model runs (completed - automatic).
     * @param name The name to filter on, or null.
     * @param diseaseGroupId The disease to filter on, or null.
     * @param minResponseDate The min response date to filter on, or null.
     * @param maxResponseDate The max response date to filter on, or null.
     * @return A filtered list of model runs.
     */
      List<ModelRun> getFilteredModelRuns(String name, Integer diseaseGroupId,
                                              LocalDate minResponseDate, LocalDate maxResponseDate);
}
