package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseProcessType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;

/**
 * Service interface to support the workflow surrounding a model run request.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunWorkflowService {

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param processType The type of process that is being performed (auto/manual/gold).
     * @param batchStartDate The start date for batching (if validator parameter batching should happen after the model
     * run is completed), otherwise null. (Only required for DiseaseProcessType.MANUAL)
     * @param batchEndDate The end date for batching (if it should happen), otherwise null.
     *                     (Only required for DiseaseProcessType.MANUAL)
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    void prepareForAndRequestModelRun(int diseaseGroupId, DiseaseProcessType processType,
                                      DateTime batchStartDate, DateTime batchEndDate) throws ModelRunWorkflowException;

    /**
     * Calculates and saves the new weighting for each active expert.
     */
    void updateExpertsWeightings();

    /**
     * Process any occurrences currently on the validator.
     * First updating the expert weighting of all validator occurrences, then remove the appropriate occurrences from
     * the validator (setting their final weightings in the process).
     * @param diseaseGroupId The disease group ID.
     * @param processType The type of process that is being performed (auto/manual/gold).
     */
    void processOccurrencesOnDataValidator(int diseaseGroupId, DiseaseProcessType processType);

    /**
     * Generates the disease extent for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     * @param processType The type of process that is being performed (auto/manual/gold).
     */
    void generateDiseaseExtent(int diseaseGroupId, DiseaseProcessType processType);

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    void enableAutomaticModelRuns(int diseaseGroupId);
}
