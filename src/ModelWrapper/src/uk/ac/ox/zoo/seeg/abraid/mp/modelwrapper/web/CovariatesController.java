package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.CovariateObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for the ModelWrapper Covariates page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class CovariatesController {
    private static final Logger LOGGER = Logger.getLogger(CovariatesController.class);
    private static final String LOG_EXISTING_COVARIATE_CONFIGURATION_IS_INVALID =
            "Existing covariate configuration is invalid.";
    private static final String LOG_INVALID_REQUEST_TO_UPDATE_COVARIATES =
            "Invalid request to update covariates (specifed configuration is not valid).";
    private static final String LOG_COVARIATE_CONFIGURATION_UPDATE_FAILED =
            "Covariate configuration update failed.";
    private static final String LOG_COVARIATE_CONFIGURATION_UPDATED_SUCCESSFULLY =
            "Covariate configuration updated successfully.";

    private final ConfigurationService configurationService;

    @Autowired
    public CovariatesController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Request map for the covariates page.
     * @param model The ftl data model.
     * @return The ftl covariates page name.
     * @throws java.io.IOException thrown if the existing configuration could not be read or is invalid.
     */
    @RequestMapping(value = "/covariates", method = RequestMethod.GET)
    public String showCovariatesPage(Model model) throws IOException {
        ObjectMapper jsonConverter = new CovariateObjectMapper();

        try {
            JsonCovariateConfiguration covariateConfig = configurationService.getCovariateConfiguration();
            String covariateJson = jsonConverter.writeValueAsString(covariateConfig);
            model.addAttribute("initialData", covariateJson);
            return "covariates";
        } catch (IOException e) {
            LOGGER.error(LOG_EXISTING_COVARIATE_CONFIGURATION_IS_INVALID); // Exception already logged at lower level
            throw new IOException(LOG_EXISTING_COVARIATE_CONFIGURATION_IS_INVALID, e);
        }
    }

    /**
     * Request map for updating the covariates configuration.
     * @param config The new covariate configuration.
     * @return 204 for success or 400 for bad input.
     * @throws java.io.IOException thrown if the configuration could not be saved.
     */
    @RequestMapping(value = "/covariates/config",
                    method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCovariates(@RequestBody JsonCovariateConfiguration config) throws IOException  {
        if (config == null || !config.isValid()) {
            LOGGER.warn(LOG_INVALID_REQUEST_TO_UPDATE_COVARIATES);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try {
            configurationService.setCovariateConfiguration(config);
        } catch (IOException e) {
            LOGGER.error(LOG_COVARIATE_CONFIGURATION_UPDATE_FAILED); // Exception already logged at lower level
            throw new IOException(LOG_COVARIATE_CONFIGURATION_UPDATE_FAILED, e);
        }

        LOGGER.info(LOG_COVARIATE_CONFIGURATION_UPDATED_SUCCESSFULLY);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the submission of the covariate file upload form.
     * @param name The name to use for the new file.
     * @param subdirectory The subdirectory to add the new file to.
     * @param file The new covariate file.
     * @return A response entity with JsonFileUploadResponse for compatibility with iframe based upload.
     */
    @RequestMapping(value = "/covariates/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<JsonFileUploadResponse> uploadFileHandler(String name, String subdirectory,
                                                                    MultipartFile file) {
        List<String> messages = Arrays.asList("Test message 1", "Test message 2");
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new JsonFileUploadResponse(false, messages), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new JsonFileUploadResponse(true, messages), HttpStatus.OK);
        }
    }
}
