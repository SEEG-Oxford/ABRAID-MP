package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;

/**
 * Controller for the ModelWrapper model run triggers.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunController {
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
     * @return 204 for success, 400 for invalid parameters or 500 if server can not start model run.
     */
    @RequestMapping(value = "/model/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> startRun(@RequestBody GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData) {
        if (occurrenceData == null) {
            return new ResponseEntity<String>("Must be provided", HttpStatus.BAD_REQUEST);
        }

        try {
            RunConfiguration runConfiguration = runConfigurationFactory.createDefaultConfiguration();
            modelRunner.runModel(runConfiguration, occurrenceData); // Ignore result for now
        } catch (Exception e) {
            return new ResponseEntity<String>("Could not start model run.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
}
