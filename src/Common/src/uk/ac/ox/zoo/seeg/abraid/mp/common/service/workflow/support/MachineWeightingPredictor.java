package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.util.List;

/**
 * Machine learning component used to predict the weighting.
 * Copyright (c) 2014 University of Oxford
 */
public class MachineWeightingPredictor {

    private MachineLearningWebService webService;

    private static final Logger LOGGER = Logger.getLogger(MachineWeightingPredictor.class);
    private static final String TRAINING_FAILURE = "Unable to train predictor for disease group.";
    private static final String PREDICTION_FAILURE = "Unable to get prediction for occurrence.";

    public MachineWeightingPredictor(MachineLearningWebService webService) {
        this.webService = webService;
    }

    /**
     * Train the model with the list of occurrences.
     * @param diseaseGroupId The ID of the disease group to which the occurrences belong.
     * @param occurrences The occurrences with which to train the predictor.
     * @throws MachineWeightingPredictorException if the json cannot be processed, or the request cannot be made.
     */
    public void train(int diseaseGroupId, List<DiseaseOccurrence> occurrences)
            throws MachineWeightingPredictorException {
        try {
            webService.sendTrainingData(diseaseGroupId, occurrences);
        } catch (WebServiceClientException|JsonProcessingException e) {
            LOGGER.error(e.getMessage());
            throw new MachineWeightingPredictorException(TRAINING_FAILURE);
        }
    }

    /**
     * Predict the weighting of a new occurrence.
     * @param occurrence The occurrence.
     * @return The predicted value for weighting.
     * @throws MachineWeightingPredictorException if the request cannot be made or the response cannot be handled.
     */
    public Double findMachineWeighting(DiseaseOccurrence occurrence) throws MachineWeightingPredictorException {
        try {
            return webService.getPrediction(occurrence);
        } catch (JsonProcessingException|WebServiceClientException|NumberFormatException e) {
            LOGGER.error(e.getMessage());
            throw new MachineWeightingPredictorException(PREDICTION_FAILURE);
        }
    }
}
