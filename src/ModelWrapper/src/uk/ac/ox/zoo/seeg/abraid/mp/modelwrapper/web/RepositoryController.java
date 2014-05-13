package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.SourceCodeManager;

import java.util.List;

/**
 * Controller for the ModelWrapper repository configuration forms.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class RepositoryController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(RepositoryController.class);
    private static final String LOG_REJECTING_MODEL_VERSION = "Rejecting model version, as not present in repository.";
    private static final String LOG_FAILED_TO_GET_LIST_OF_VERSIONS = "Failed to get list of versions from repository.";
    private static final String LOG_TRYING_TO_SYNC_REPOSITORY = "Trying to sync repository url: %s";
    private static final String LOG_CLEARING_MODEL_VERSION = "Clearing model version (due to new repo url)";
    private static final String LOG_SYNC_REPO_SUCCESSFUL = "Sync repo successful";
    private static final String LOG_SYNC_REPO_FAILED = "Sync repo failed.";
    private static final String LOG_REVERTING_MODEL_REPOSITORY_URL = "Reverting model repository url.";
    private static final String LOG_VERSION_CONFIGURATION_NOT_UPDATED = "Version configuration not updated.";

    private final ConfigurationService configurationService;
    private final SourceCodeManager sourceCodeManager;

    @Autowired
    public RepositoryController(ConfigurationService configurationService, SourceCodeManager sourceCodeManager) {
        this.configurationService = configurationService;
        this.sourceCodeManager = sourceCodeManager;
    }

    /**
     * Updates the model repository url and synchronises the local repository copy.
     * @param repositoryUrl The remote repository url
     * @return 200 for success (with versions), 400 for failure.
     */
    @RequestMapping(value = "/repo/sync", method = RequestMethod.POST)
    public ResponseEntity<List<String>> syncRepository(String repositoryUrl) {
        boolean validRequest = !StringUtils.isEmpty(repositoryUrl);

        if (validRequest) {
            String oldUrl = configurationService.getModelRepositoryUrl();
            LOGGER.info(String.format(LOG_TRYING_TO_SYNC_REPOSITORY, repositoryUrl));
            if (!repositoryUrl.equals(oldUrl)) {
                configurationService.setModelRepositoryUrl(repositoryUrl);
            }

            try {
                sourceCodeManager.updateRepository();
                List<String> versions = sourceCodeManager.getAvailableVersions();

                if (!repositoryUrl.equals(oldUrl)) {
                    // Version numbers from the old repository are clearly not valid
                    LOGGER.info(LOG_CLEARING_MODEL_VERSION);
                    configurationService.setModelRepositoryVersion("");
                }

                LOGGER.info(LOG_SYNC_REPO_SUCCESSFUL);
                // Respond with a 204, this is equivalent to a 200 (OK) but without any content.
                return new ResponseEntity<List<String>>(versions, HttpStatus.OK);
            } catch (Exception e) {
                LOGGER.error(LOG_SYNC_REPO_FAILED, e);
                if (!repositoryUrl.equals(oldUrl)) {
                    LOGGER.info(LOG_REVERTING_MODEL_REPOSITORY_URL);
                    configurationService.setModelRepositoryUrl(oldUrl);
                }
            }
        }

        return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Updates the model repository version.
     * @param version The repository version
     * @return 204 for success, 400 for failure.
     */
    @RequestMapping(value = "/repo/version", method = RequestMethod.POST)
    public ResponseEntity setModelVersion(String version) {
        boolean validRequest = !StringUtils.isEmpty(version);

        if (validRequest) {
            try {
                if (version.equals(configurationService.getModelRepositoryVersion())) {
                    return new ResponseEntity(HttpStatus.NO_CONTENT);
                }

                if (sourceCodeManager.getAvailableVersions().contains(version)) {
                    configurationService.setModelRepositoryVersion(version);
                } else {
                    LOGGER.info(LOG_REJECTING_MODEL_VERSION);
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                }

                // Respond with a 204, this is equivalent to a 200 (OK) but without any content.
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                LOGGER.error(LOG_FAILED_TO_GET_LIST_OF_VERSIONS, e);
                LOGGER.info(LOG_VERSION_CONFIGURATION_NOT_UPDATED);
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
