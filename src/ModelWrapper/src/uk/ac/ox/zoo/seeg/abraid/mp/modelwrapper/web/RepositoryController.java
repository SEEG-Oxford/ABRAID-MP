package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.SourceCodeManager;

import java.util.List;

/**
 * Controller for the ModelWrapper repository configuration forms.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class RepositoryController {
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
    @RequestMapping(value = "/repo/sync", method  = RequestMethod.POST)
    public ResponseEntity<List<String>> syncRepository(String repositoryUrl) {
        boolean validRequest = !StringUtils.isEmpty(repositoryUrl);

        if (validRequest) {
            String oldUrl = configurationService.getModelRepositoryUrl();
            configurationService.setModelRepositoryUrl(repositoryUrl);

            try {
                sourceCodeManager.updateRepository();
                List<String> versions = sourceCodeManager.getAvailableVersions();

                if (!repositoryUrl.equals(oldUrl)) {
                    // Version numbers from the old repository are clearly not valid
                    configurationService.setModelRepositoryVersion("");
                }

                // Respond with a 204, this is equivalent to a 200 (OK) but without any content.
                return new ResponseEntity<List<String>>(versions, HttpStatus.OK);
            } catch (Exception e) {
                configurationService.setModelRepositoryUrl(oldUrl);
            }
        }

        return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Updates the model repository version.
     * @param version The repository version
     * @return 204 for success, 400 for failure.
     */
    @RequestMapping(value = "/repo/version", method  = RequestMethod.POST)
    public ResponseEntity setModelVersion(String version) {
        boolean validRequest = !StringUtils.isEmpty(version);

        if (validRequest) {
            try {
                if (sourceCodeManager.getAvailableVersions().contains(version)) {
                    configurationService.setModelRepositoryVersion(version);
                } else {
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                }

                // Respond with a 204, this is equivalent to a 200 (OK) but without any content.
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
