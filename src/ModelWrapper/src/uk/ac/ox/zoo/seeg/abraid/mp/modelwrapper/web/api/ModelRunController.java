package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.api;

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
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelOutputHandlerWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunnerAsyncWrapper;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelStatusReporter;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelStatusReporterImpl;

/**
 * Controller for the ModelWrapper model run triggers.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(ModelRunController.class);
    private static final String LOG_QUEUING_NEW_BACKGROUND_MODEL_RUN =
            "Queuing new background model run for disease group %d (model run name %s)";
    private static final String LOG_EXCEPTION_STARTING_MODEL_RUN = "Exception starting model run.";

    private final RunConfigurationFactory runConfigurationFactory;
    private final ModelRunnerAsyncWrapper modelRunnerAsyncWrapper;
    private final ModelOutputHandlerWebService modelOutputHandlerWebService;
    private final AbraidJsonObjectMapper objectMapper;

    @Autowired
    public ModelRunController(
            RunConfigurationFactory runConfigurationFactory,
            ModelRunnerAsyncWrapper modelRunnerAsyncWrapper,
            ModelOutputHandlerWebService modelOutputHandlerWebService,
            AbraidJsonObjectMapper objectMapper) {
        this.runConfigurationFactory = runConfigurationFactory;
        this.modelRunnerAsyncWrapper = modelRunnerAsyncWrapper;
        this.modelOutputHandlerWebService = modelOutputHandlerWebService;
        this.objectMapper = objectMapper;
    }

    /**
     * Triggers a new model run with the given occurrences.
     * @param runData The run data to model.
     * @return 204 for success, 400 for invalid parameters or 500 if server cannot start model run.
     */
    @RequestMapping(value = "/api/model/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JsonModelRunResponse> startRun(@RequestBody JsonModelRun runData) {
        if (runData == null || !runData.isValid() || !runData.getDisease().isValid()) {
            return createErrorResponse("Run data must be provided and be valid.", HttpStatus.BAD_REQUEST);
        }

        RunConfiguration runConfiguration;
        try {
            runConfiguration = runConfigurationFactory.createDefaultConfiguration(
                    runData.getDisease().getId(),
                    runData.getDisease().isGlobal(),
                    runData.getDisease().getAbbreviation());

            ModelStatusReporter modelStatusReporter = new ModelStatusReporterImpl(
                    runConfiguration.getRunName(),
                    runConfiguration.getWorkingDirectoryPath(),
                    modelOutputHandlerWebService,
                    objectMapper);

            // Ignore result for now
            LOGGER.info(String.format(LOG_QUEUING_NEW_BACKGROUND_MODEL_RUN, runData.getDisease().getId(),
                    runConfiguration.getRunName()));

            modelRunnerAsyncWrapper.startModel(
                    runConfiguration, runData.getOccurrences(), runData.getExtentWeightings(), modelStatusReporter);
        } catch (Exception e) {
            LOGGER.error(LOG_EXCEPTION_STARTING_MODEL_RUN, e);
            return createErrorResponse("Could not start model run. See server logs for more details.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return createSuccessResponse(runConfiguration.getRunName());
    }

    private ResponseEntity<JsonModelRunResponse> createSuccessResponse(String modelRunName) {
        return new ResponseEntity<>(new JsonModelRunResponse(modelRunName, null), HttpStatus.OK);
    }

    private ResponseEntity<JsonModelRunResponse> createErrorResponse(String errorText, HttpStatus status) {
        return new ResponseEntity<>(new JsonModelRunResponse(null, errorText), status);
    }
}
