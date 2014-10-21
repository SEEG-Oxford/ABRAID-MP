package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Collection;

/**
 * Service interface for model run inputs and outputs.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunService {
    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if no model run.
     */
    ModelRun getModelRunByName(String name);

    /**
     * Saves a model run.
     * @param modelRun The model run to save.
     */
    void saveModelRun(ModelRun modelRun);

    /**
     * Gets the latest model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest model run, or null if there are no model runs.
     */
    ModelRun getLastRequestedModelRun(int diseaseGroupId);

    /**
     * Gets the latest completed model run for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return The latest completed model run, or null if there are no completed model runs.
     */
    ModelRun getLastCompletedModelRun(int diseaseGroupId);

    /**
     * Returns whether or not disease occurrence batching has ever completed for the specified disease group.
     * @param diseaseGroupId The specified disease group's ID.
     * @return True if batching has completed at least once for this disease group, otherwise false.
     */
    boolean hasBatchingEverCompleted(int diseaseGroupId);

    /**
     * Returns the input date, with the number of days between scheduled model runs subtracted.
     * @param dateTime The input date.
     * @return The input date minus the number of days between scheduled model runs.
     */
    DateTime subtractDaysBetweenModelRuns(DateTime dateTime);

    /**
     * Gets all of the completed model runs.
     * @return The completed model runs.
     */
    Collection<ModelRun> getCompletedModelRuns();
}
