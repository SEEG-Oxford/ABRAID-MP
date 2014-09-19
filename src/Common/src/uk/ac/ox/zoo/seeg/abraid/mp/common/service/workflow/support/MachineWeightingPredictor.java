package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.List;

/**
 * Machine learning component used to predict the weighting.
 * Copyright (c) 2014 University of Oxford
 */
public class MachineWeightingPredictor {

    public MachineWeightingPredictor() {
    }

    /**
     * Train the model with the list of occurrences.
     * @param occurrences The occurrences with which to train the predictor.
     */
    public void train(List<DiseaseOccurrence> occurrences) {

    }

    /**
     * Predict the weighting of a new occurrence.
     * @param occurrence The occurrence.
     * @return The predicted value for weighting.
     */
    public Double findMachineWeighting(DiseaseOccurrence occurrence) {
        return null;
    }
}
