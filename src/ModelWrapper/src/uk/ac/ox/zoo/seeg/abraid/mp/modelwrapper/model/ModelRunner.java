package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.IOException;
import java.util.Map;

/**
 * Interface to provide an entry point for model runs.
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunner {
    /**
     * Starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @param occurrenceData The occurrence data to model with.
     * @param extentWeightings The mapping from GAUL code to disease extent class weighting.
     * @return The process handler for the launched process.
     * @throws ProcessException Thrown in response to errors in the model.
     * @throws IOException Thrown if the workspace cannot be correctly provisioned.
     */
    ModelProcessHandler runModel(RunConfiguration configuration,
                                 GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData,
                                 Map<Integer, Integer> extentWeightings)
            throws ProcessException, IOException;
}
