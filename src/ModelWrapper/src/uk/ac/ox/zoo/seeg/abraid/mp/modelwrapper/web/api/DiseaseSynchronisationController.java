package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonDisease;

import java.io.IOException;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * Controller for synchronising disease changes from public site to a ModelWrapper instance.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DiseaseSynchronisationController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(DiseaseSynchronisationController.class);
    private static final String ONE_DISEASES_SYNCHRONISED = "One diseases synchronised from main ABRAID platform (%s).";
    private static final String ALL_DISEASES_SYNCHRONISED = "All diseases synchronised from main ABRAID platform.";

    private final ConfigurationService configurationService;

    @Autowired
    public DiseaseSynchronisationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @RequestMapping(value = "/api/diseases/{diseaseId}",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity syncDisease(@PathVariable Integer diseaseId, @RequestBody JsonDisease requestDisease) throws IOException {
        if (requestDisease.getId() != diseaseId || requestDisease.isValid()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        JsonCovariateConfiguration covariateConfiguration = configurationService.getCovariateConfiguration();
        syncDisease(requestDisease, covariateConfiguration);
        configurationService.setCovariateConfiguration(covariateConfiguration);

        LOGGER.info(String.format(ONE_DISEASES_SYNCHRONISED, requestDisease.getName()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/api/diseases",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity syncAll(@RequestBody WrappedList<JsonDisease> requestDiseases) throws IOException {
        if (extract(requestDiseases.getList(), on(JsonDisease.class).isValid()).contains(false)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        JsonCovariateConfiguration covariateConfiguration = configurationService.getCovariateConfiguration();
        for (JsonDisease requestDisease : requestDiseases.getList()) {
            syncDisease(requestDisease, covariateConfiguration);
        }
        configurationService.setCovariateConfiguration(covariateConfiguration);

        LOGGER.info(ALL_DISEASES_SYNCHRONISED);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void syncDisease(JsonDisease requestDisease, JsonCovariateConfiguration covariateConfiguration) {
        JsonDisease storedDisease = selectUnique(
                covariateConfiguration.getDiseases(),
                having(on(JsonDisease.class).getId(), equalTo(requestDisease.getId())));

        if (storedDisease == null) {
            covariateConfiguration.getDiseases().add(requestDisease);
        } else {
            storedDisease.setName(requestDisease.getName());
        }
    }
}
