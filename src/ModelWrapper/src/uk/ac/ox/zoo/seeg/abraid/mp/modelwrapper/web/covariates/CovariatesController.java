package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.covariates;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
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
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

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
    private static final String ERROR_CREATE_SUBDIRECTORY = "Could not create subdirectory for new covariate file";
    private static final String LOG_NEW_COVARIATE = "New covariate uploaded (%s, %s).";

    private final ConfigurationService configurationService;
    private final CovariatesControllerValidator validator;

    @Autowired
    public CovariatesController(ConfigurationService configurationService,
                                CovariatesControllerValidator covariatesControllerValidator) {
        this.configurationService = configurationService;
        this.validator = covariatesControllerValidator;
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
     * @param name The name of the added covariate.
     * @param subdirectory The subdirectory to add the new file to.
     * @param file The new covariate file.
     * @return A response entity with JsonFileUploadResponse for compatibility with iframe based upload.
     * @throws java.io.IOException Throw if there is an issue writing the covariate file at the specified location.
     */
    @RequestMapping(value = "/covariates/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<JsonFileUploadResponse> addCovariateFile(
            String name, String subdirectory, MultipartFile file) throws IOException {
        String covariateDirectory = configurationService.getCovariateDirectory();
        JsonCovariateConfiguration covariateConfiguration = configurationService.getCovariateConfiguration();
        String path = extractTargetPath(subdirectory, file, covariateDirectory);

        Collection<String> validationMessages = validator
                .validateCovariateUpload(name, subdirectory, file, path, covariateDirectory, covariateConfiguration);

        if (!validationMessages.isEmpty()) {
            return new ResponseEntity<>(new JsonFileUploadResponse(false, validationMessages), HttpStatus.BAD_REQUEST);
        } else {
            // Create the file on server
            writeCovariateFile(file, path);

            // Add the entry in the covariate config
            String relativePath = updateCovariateConfig(name, covariateDirectory, covariateConfiguration, path);

            LOGGER.info(String.format(LOG_NEW_COVARIATE, name, relativePath));
            return new ResponseEntity<>(new JsonFileUploadResponse(true, validationMessages), HttpStatus.OK);
        }
    }

    private String updateCovariateConfig(String name, String covariateDirectory, JsonCovariateConfiguration config,
                                         String path) throws IOException {
        String relativePath = extractRelativePath(covariateDirectory, path).toString();
        config.getFiles().add(new JsonCovariateFile(
                relativePath,
                name,
                null,
                false,
                new ArrayList<Integer>()
        ));
        configurationService.setCovariateConfiguration(config);
        return relativePath;
    }

    private void writeCovariateFile(MultipartFile file, String path) throws IOException {
        // Create directory
        createDirectoryForCovariate(path);

        File serverFile = Paths.get(path).toFile();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(file.getBytes());
        stream.close();
    }

    private void createDirectoryForCovariate(String path) throws IOException {
        File dir = Paths.get(path).getParent().toFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException(ERROR_CREATE_SUBDIRECTORY);
            }
        }
    }

    private String extractTargetPath(String subdirectory, MultipartFile file, String covariateDirectory) {
        Path path = Paths.get(covariateDirectory, subdirectory, file.getOriginalFilename());
        return FilenameUtils.separatorsToUnix(path.toAbsolutePath().toString());
    }

    private Path extractRelativePath(String covariateDirectory, String path) {
        Path parent = Paths.get(covariateDirectory).toAbsolutePath();
        Path child = Paths.get(path).toAbsolutePath();
        return parent.relativize(child);
    }

}
