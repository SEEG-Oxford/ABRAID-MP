package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
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

import java.io.File;

/**
 * Controller for the ModelWrapper misc items configuration forms.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class MiscController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(MiscController.class);
    private static final String LOG_EXCEPTION_GETTING_R_PATH = "Exception getting R path.";
    private static final String LOG_SUPPLIED_MAX_TOO_SHORT = "User supplied max run duration too short: %s";
    private static final String LOG_SUPPLIED_DIRECTORY_NOT_USABLE = "User supplied covariate dir not usable: %s";

    private static final int MINIMUM_MAX_RUN_DURATION = 1000;
    private final ConfigurationService configurationService;

    @Autowired
    public MiscController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Updates the path to use for the R executable when performing model runs.
     * @param value The path to the R executable.
     * @return 204 for success, 400 for failure.
     */
    @RequestMapping(value = "/misc/rpath", method = RequestMethod.POST)
    public ResponseEntity updateRExecutablePath(String value) {
        if (StringUtils.isEmpty(value)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String currentValue;
        try {
            currentValue = configurationService.getRExecutablePath();
        } catch (ConfigurationException e) {
            // If the value if the file is invalid then we should definitely try the new one.
            LOGGER.error(LOG_EXCEPTION_GETTING_R_PATH, e);
            currentValue = "";
        }

        if (!value.equals(currentValue)) {
            configurationService.setRExecutablePath(value);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Updates the max run duration to use when performing model runs.
     * @param value The max run duration
     * @return 204 for success.
     */
    @RequestMapping(value = "/misc/runduration", method = RequestMethod.POST)
    public ResponseEntity updateMaxRunDuration(int value) {
        if (value < MINIMUM_MAX_RUN_DURATION) {
            LOGGER.info(String.format(LOG_SUPPLIED_MAX_TOO_SHORT, value));
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (value != configurationService.getMaxModelRunDuration()) {
            configurationService.setMaxModelRunDuration(value);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Updates the covariate directory to use when performing model runs.
     * @param value The covariate directory.
     * @return 204 for success.
     */
    @RequestMapping(value = "/misc/covariatedirectory", method = RequestMethod.POST)
    public ResponseEntity updateCovariateDirectory(String value) {
        if (StringUtils.isEmpty(value)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (!value.equals(configurationService.getCovariateDirectory())) {
            File newDirectory = new File(value);
            if (!newDirectory.exists() || !newDirectory.isDirectory() || !newDirectory.canRead()) {
                LOGGER.info(String.format(LOG_SUPPLIED_DIRECTORY_NOT_USABLE, value));
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            configurationService.setCovariateDirectory(value);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
