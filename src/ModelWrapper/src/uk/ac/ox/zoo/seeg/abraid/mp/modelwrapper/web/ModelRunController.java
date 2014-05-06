package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;

/**
 * Controller for the ModelWrapper model run triggers.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(ModelRunController.class);
    private static final String LOG_STARTING_NEW_BACKGROUND_MODEL_RUN = "Starting new background model run";
    private static final String LOG_EXCEPTION_STARTING_MODEL_RUN = "Exception starting model run.";

    private final RunConfigurationFactory runConfigurationFactory;
    private final ModelRunner modelRunner;

    @Autowired
    public ModelRunController(RunConfigurationFactory runConfigurationFactory, ModelRunner modelRunner) {
        this.runConfigurationFactory = runConfigurationFactory;
        this.modelRunner = modelRunner;
    }

    /**
     * Triggers a new model run with the given occurrences.
     * @param occurrenceData A set of occurrences.
     * @return 204 for success, 400 for invalid parameters or 500 if server cannot start model run.
     */
    @RequestMapping(value = "/model/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> startRun(@RequestBody GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData) {
        if (occurrenceData == null) {
            return new ResponseEntity<String>("Occurrence data must be provided", HttpStatus.BAD_REQUEST);
        }

        try {
            LOGGER.info(LOG_STARTING_NEW_BACKGROUND_MODEL_RUN);

            // NOTE: Here I am using "foo" as a default disease name. The expectation is that it will be passed into
            // this method, maybe as a HTTP header, or maybe as part of a TBD json DTO that encapsulates the disease
            // occurrence feature collection and the disease extent data. There is no value in setting this up until we
            // know the format of the extent data.
            RunConfiguration runConfiguration = runConfigurationFactory.createDefaultConfiguration(1, "foo");
            modelRunner.runModel(runConfiguration, occurrenceData); // Ignore result for now
        } catch (Exception e) {
            LOGGER.error(LOG_EXCEPTION_STARTING_MODEL_RUN, e);
            return new ResponseEntity<String>("Could not start model run.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
}
