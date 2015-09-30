package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.io.File;
import java.io.IOException;

/**
 * Interface to provide a mechanism for generating model run scripts.
 * Copyright (c) 2014 University of Oxford
 */
public interface ScriptGenerator {
    /**
     * Creates a model run script file in the working directory for the given configuration.
     * @param modellingConfiguration The model run configuration.
     * @param workingDirectory The directory in which the script should be created.
     * @param diseaseGroup The disease group being modelled.
     * @return The script file.
     * @throws IOException Thrown in response to issues creating the script file.
     */
    File generateScript(ModellingConfiguration modellingConfiguration, File workingDirectory, DiseaseGroup diseaseGroup)
            throws IOException;
}
