package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Interface to provide a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public interface WorkspaceProvisioner {
    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @param modelData The data to use in the model.
     * @return The model wrapper script file to run.
     * @throws IOException Thrown if the directory can not be correctly provisioned.
     */
    File provisionWorkspace(RunConfiguration configuration, GeoJsonDiseaseOccurrenceFeatureCollection modelData)
            throws IOException;
}
