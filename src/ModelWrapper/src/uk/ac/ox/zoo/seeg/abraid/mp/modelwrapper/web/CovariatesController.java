package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.CovariateObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;

/**
 * Controller for the ModelWrapper Covariates page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class CovariatesController {
    private final ConfigurationService configurationService;

    @Autowired
    public CovariatesController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Request map for the covariates page.
     * @param model The ftl data model.
     * @return The ftl index page name.
     * @throws Exception thrown in response to invalid covariate configuration json in covariate directory.
     */
    @RequestMapping(value = "/covariates", method = RequestMethod.GET)
    public String showCovariatesPage(Model model) throws Exception {
        ObjectMapper jsonConverter = new CovariateObjectMapper();
        JsonCovariateConfiguration covariateConfig = configurationService.getCovariateConfiguration();

        try {
            String covariateJson = jsonConverter.writeValueAsString(covariateConfig);
            model.addAttribute("initialData", covariateJson);
            return "covariates";
        } catch (Exception e) {
            throw new Exception("Existing covariate configuration is invalid.");
        }
    }

}
