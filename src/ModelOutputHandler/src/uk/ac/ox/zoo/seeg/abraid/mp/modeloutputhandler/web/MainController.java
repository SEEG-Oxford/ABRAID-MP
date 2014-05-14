package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Main controller for the model output handler.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class MainController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    private static final String LOG_RECEIVED_OUTPUTS = "Received model run outputs";
    private static final String LOG_EXCEPTION_HANDLING_OUTPUTS = "Exception handling model run outputs.";

    /**
     * Handles the output of a model run.
     * @param modelRunZip The model run's outputs, as a zip file.
     * @return 204 for success, 400 for invalid parameters or 500 if server cannot start model run.
     */
    @RequestMapping(value = "/modeloutputhandler/handleoutputs", method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<String> handleModelOutputs(@RequestBody Object modelRunZip) {
        try {
            LOGGER.info(LOG_RECEIVED_OUTPUTS);
            handleOutputs(modelRunZip);
        } catch (Exception e) {
            LOGGER.error(LOG_EXCEPTION_HANDLING_OUTPUTS, e);
            return createErrorResponse("Could not handle model run outputs. See server logs for more details.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return createSuccessResponse();
    }

    private void handleOutputs(Object modelRunZip) {

    }

    private ResponseEntity<String> createSuccessResponse() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private ResponseEntity<String> createErrorResponse(String errorText, HttpStatus status) {
        return new ResponseEntity<>(errorText, status);
    }
}
