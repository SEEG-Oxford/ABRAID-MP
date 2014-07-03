package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow;

import java.util.Map;

/**
 * Service interface to support the workflow surrounding a model run request.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunWorkflowService {
    /**
     * Gets the new weighting for each active expert.
     * @return A map from expert ID to the new weighting value.
     */
    Map<Integer, Double> calculateExpertsWeightings();

    /**
     * Prepares for and requests a model run, for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    void prepareForAndRequestModelRun(int diseaseGroupId);

    /**
     * Saves the new weighting for each expert.
     * @param newExpertsWeightings The map from expert to the new weighting value.
     */
    void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings);
}
