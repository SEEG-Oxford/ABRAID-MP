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

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * Controller for synchronising disease changes from public site to a ModelWrapper instance.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DiseaseUpdateController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(DiseaseUpdateController.class);
    private static final String ONE_DISEASES_SYNCHRONISED = "One diseases synchronised from main ABRAID platform (%s).";
    private static final String ALL_DISEASES_SYNCHRONISED = "All diseases synchronised from main ABRAID platform.";

    private final ConfigurationService configurationService;

    @Autowired
    public DiseaseUpdateController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Update a single disease by id.
     * @param diseaseId The id of the disease to update.
     * @param requestDisease The updated disease.
     * @return 204 for success or 400 if invalid input is provided.
     * @throws Exception thrown if the updated configuration can not be saved correctly
     */
    @RequestMapping(value = "/api/diseases/{diseaseId}",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity updateDisease(@PathVariable Integer diseaseId, @RequestBody JsonDisease requestDisease)
            throws Exception {
        if (requestDisease.getId() != diseaseId || requestDisease.isValid()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        JsonCovariateConfiguration covariateConfiguration = configurationService.getCovariateConfiguration();
        syncDisease(requestDisease, covariateConfiguration);
        configurationService.setCovariateConfiguration(covariateConfiguration);

        LOGGER.info(String.format(ONE_DISEASES_SYNCHRONISED, requestDisease.getName()));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Update all of the disease.
     * @param requestDiseases The updated diseases.
     * @return 204 for success or 400 if invalid input is provided.
     * @throws Exception thrown if the updated configuration can not be saved correctly
     */
    @RequestMapping(value = "/api/diseases",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity updateAll(@RequestBody WrappedList<JsonDisease> requestDiseases) throws Exception {
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
