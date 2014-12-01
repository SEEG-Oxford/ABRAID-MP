package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;

import java.util.Map;

/**
 * Service interface to support the workflow surrounding a model run request.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunWorkflowService {
    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when manually triggering a model run.
     * @param diseaseGroupId The disease group ID.
     * @param batchStartDate The start date for batching (if validator parameter batching should happen after the model
     * run is completed), otherwise null.
     * @param batchEndDate The end date for batching (if it should happen), otherwise null.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    void prepareForAndRequestManuallyTriggeredModelRun(int diseaseGroupId,
                                                       DateTime batchStartDate, DateTime batchEndDate)
            throws ModelRunWorkflowException;

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * This method is designed for use when automatically triggering one or more model runs.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    void prepareForAndRequestAutomaticModelRun(int diseaseGroupId) throws ModelRunWorkflowException;

    /**
     * Prepares for and requests a model run using "gold standard" disease occurrences, for the specified disease group.
     * This method is designed for use during disease group set-up, when a known set of good-quality occurrences has
     * been uploaded to send to the model.
     * @param diseaseGroupId The disease group ID.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    void prepareForAndRequestModelRunUsingGoldStandardOccurrences(int diseaseGroupId) throws ModelRunWorkflowException;

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    void enableAutomaticModelRuns(int diseaseGroupId);

    /**
     * Calculates and saves the new weighting for each active expert.
     */
    void updateExpertsWeightings();

    /**
     * Generates the disease extent for the specified disease group.
     * @param diseaseGroup The disease group.
     */
    void generateDiseaseExtent(DiseaseGroup diseaseGroup);

    /**
     * Generate the disease extent for the specified disease group, only using "gold standard" disease occurrences.
     * This method is designed for use during disease group set-up, when a known set of good-quality occurrences has
     * been uploaded to send to the model.
     * @param diseaseGroup The disease group.
     */
    void generateDiseaseExtentUsingGoldStandardOccurrences(DiseaseGroup diseaseGroup);
}
