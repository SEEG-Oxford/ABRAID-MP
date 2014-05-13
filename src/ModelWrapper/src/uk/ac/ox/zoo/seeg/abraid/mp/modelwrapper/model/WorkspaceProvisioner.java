package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Interface to provide a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public interface WorkspaceProvisioner {
    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @param occurrenceData The occurrences to use in the model.
     * @param extentWeightings The mapping from GAUL code to disease extent class weighting.
     * @return The model wrapper script file to run.
     * @throws IOException Thrown if the directory can not be correctly provisioned.
     */
    File provisionWorkspace(RunConfiguration configuration,
                            GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData,
                            Map<Integer, Integer> extentWeightings)
            throws IOException;
}
