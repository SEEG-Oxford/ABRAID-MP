package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelDisease;

import java.io.IOException;
import java.util.Collection;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.collection.LambdaCollections.with;

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
            "Invalid request to update covariates (specified configuration is not valid).";
    private static final String LOG_COVARIATE_CONFIGURATION_UPDATE_FAILED =
            "Covariate configuration update failed.";
    private static final String LOG_COVARIATE_CONFIGURATION_UPDATED_SUCCESSFULLY =
            "Covariate configuration updated successfully.";
    private static final String LOG_NEW_COVARIATE = "New covariate uploaded (%s, %s).";

    private final CovariatesControllerHelper covariatesControllerHelper;
    private final CovariatesControllerValidator validator;
    private final AbraidJsonObjectMapper objectMapper;

    @Autowired
    public CovariatesController(CovariatesControllerHelper covariatesControllerHelper,
                                CovariatesControllerValidator covariatesControllerValidator,
                                AbraidJsonObjectMapper objectMapper) {
        this.covariatesControllerHelper = covariatesControllerHelper;
        this.validator = covariatesControllerValidator;
        this.objectMapper = objectMapper;
    }

    /**
     * Request map for the covariates page.
     * @param model The ftl data model.
     * @return The ftl covariates page name.
     * @throws java.io.IOException thrown if the existing configuration could not be read or is invalid.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admin/covariates", method = RequestMethod.GET)
    public String showCovariatesPage(Model model) throws IOException {
        try {
            JsonCovariateConfiguration covariateConfig = covariatesControllerHelper.getCovariateConfiguration();
            covariateConfig.setDiseases(with(covariateConfig.getDiseases()).sort(on(JsonModelDisease.class).getName()));
            String covariateJson = objectMapper.writer().writeValueAsString(covariateConfig);
            model.addAttribute("initialData", covariateJson);
            return "admin/covariates";
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
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admin/covariates/config",
                    method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> updateCovariates(@RequestBody JsonCovariateConfiguration config)
            throws IOException  {
        Collection<String> validationMessages = validator.validateCovariateConfiguration(config);
        if (config == null || !validationMessages.isEmpty()) {
            LOGGER.warn(LOG_INVALID_REQUEST_TO_UPDATE_COVARIATES);
            return new ResponseEntity<>(validationMessages, HttpStatus.BAD_REQUEST);
        }

        try {
            covariatesControllerHelper.setCovariateConfiguration(config);
        } catch (Exception e) {
            LOGGER.error(LOG_COVARIATE_CONFIGURATION_UPDATE_FAILED); // Exception already logged at lower level
            throw new IOException(LOG_COVARIATE_CONFIGURATION_UPDATE_FAILED, e);
        }

        LOGGER.info(LOG_COVARIATE_CONFIGURATION_UPDATED_SUCCESSFULLY);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the submission of the covariate file upload form.
     * @param name The name of the added covariate.
     * @param discrete True if this covariate contains discrete values
     * @param subdirectory The subdirectory to add the new file to.
     * @param file The new covariate file.
     * @return A response entity with JsonFileUploadResponse for compatibility with iframe based upload.
     * @throws java.io.IOException Throw if there is an issue writing the covariate file at the specified location.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admin/covariates/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<JsonFileUploadResponse> addCovariateFile(
            String name, boolean discrete, String subdirectory, MultipartFile file) throws IOException {
        String targetPath = covariatesControllerHelper.extractTargetPath(subdirectory, file);
        Collection<String> validationMessages = validator
                .validateCovariateUpload(name, subdirectory, file, targetPath);

        if (!validationMessages.isEmpty()) {
            return new ResponseEntity<>(new JsonFileUploadResponse(false, validationMessages), HttpStatus.BAD_REQUEST);
        } else {
            // Create the file on server
            covariatesControllerHelper.saveNewCovariateFile(name, discrete, targetPath, file);
            LOGGER.info(String.format(LOG_NEW_COVARIATE, name, targetPath));

            return new ResponseEntity<>(new JsonFileUploadResponse(true, validationMessages), HttpStatus.OK);
        }
    }




}
