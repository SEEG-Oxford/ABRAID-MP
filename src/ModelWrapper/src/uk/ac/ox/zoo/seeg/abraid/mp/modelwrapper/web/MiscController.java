package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

/**
 * Controller for the ModelWrapper misc items configuration forms.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class MiscController {
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

        String currentValue = null;
        try {
            currentValue = configurationService.getRExecutablePath();
        } catch (ConfigurationException e) {
            // If the value if the file is invalid then we should definitely try the new one.
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
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (value != configurationService.getMaxModelRunDuration()) {
            configurationService.setMaxModelRunDuration(value);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
