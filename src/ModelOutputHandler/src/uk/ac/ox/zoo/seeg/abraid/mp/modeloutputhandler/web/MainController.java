package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.commons.io.FileUtils;
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
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Main controller for the model output handler.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class MainController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    private static final String LOG_RECEIVED_OUTPUTS = "Received model run outputs (body length %s bytes)";
    private static final String LOG_COULD_NOT_DELETE_TEMP_FILE = "Could not delete temporary file \"%s\"";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE =
            "Model outputs handler failed with error \"%s\". See ModelOutputHandler server logs for more details.";
    private static final String MODEL_FAILURE_EMAIL_TEMPLATE = "modelFailureEmail.ftl";
    private static final String MODEL_FAILURE_EMAIL_SUBJECT = "Failed Model Run";

    private final MainHandler mainHandler;
    private final HandlersAsyncWrapper handlersAsyncWrapper;
    private final EmailService emailService;

    @Autowired
    public MainController(
            MainHandler mainHandler, HandlersAsyncWrapper handlersAsyncWrapper, EmailService emailService) {
        this.mainHandler = mainHandler;
        this.handlersAsyncWrapper = handlersAsyncWrapper;
        this.emailService = emailService;
    }

    /**
     * Handles the output of a model run.
     * @param modelRunZip The model run's outputs, as a zip file.
     * @return 204 for success, 400 for invalid parameters or 500 if server cannot start model run.
     */
    @RequestMapping(value = "/handleoutputs", method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<String> handleModelOutputs(@RequestBody byte[] modelRunZip) {
        File modelRunZipFile = null;

        try {
            LOGGER.info(String.format(LOG_RECEIVED_OUTPUTS, modelRunZip.length));

            // Save the request body to a temporary file
            modelRunZipFile = saveRequestBodyToTemporaryFile(modelRunZip);
            // Ensure the request body is eligible for garbage collection
            modelRunZip = null;
            // Continue handling the outputs
            ModelRun modelRun = mainHandler.handleOutputs(modelRunZipFile);
            if (modelRun.getStatus() == ModelRunStatus.FAILED) {
                sendModelFailureEmail(modelRun);
            }
            handlersAsyncWrapper.handle(modelRun);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return createErrorResponse(String.format(INTERNAL_SERVER_ERROR_MESSAGE, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            deleteTemporaryFile(modelRunZipFile);
        }

        return createSuccessResponse();
    }

    private void sendModelFailureEmail(ModelRun modelRun) {
        Map<String, String> data = new HashMap<>();
        data.put("name", modelRun.getName());
        data.put("output", modelRun.getOutputText());
        data.put("error", modelRun.getErrorText());
        data.put("server", modelRun.getRequestServer());
        emailService.sendEmailInBackground(MODEL_FAILURE_EMAIL_SUBJECT, MODEL_FAILURE_EMAIL_TEMPLATE, data);
    }

    private File saveRequestBodyToTemporaryFile(byte[] modelRunZip) throws IOException {
        File modelRunZipFile = Files.createTempFile("model_run_outputs", ".zip").toFile();
        FileUtils.writeByteArrayToFile(modelRunZipFile, modelRunZip);
        return modelRunZipFile;
    }

    private void deleteTemporaryFile(File modelRunZipFile) {
        if (modelRunZipFile != null && modelRunZipFile.exists()) {
            if (!modelRunZipFile.delete()) {
                // Could not delete temporary file. Just log the error (this is not serious enough to fail the whole
                // model output handling).
                LOGGER.error(String.format(LOG_COULD_NOT_DELETE_TEMP_FILE, modelRunZipFile.getAbsolutePath()));
            }
        }
    }

    private ResponseEntity<String> createSuccessResponse() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<String> createErrorResponse(String errorText, HttpStatus status) {
        return new ResponseEntity<>(errorText, status);
    }
}
