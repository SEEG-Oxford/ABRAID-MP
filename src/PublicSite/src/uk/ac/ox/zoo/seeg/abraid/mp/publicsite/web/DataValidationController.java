package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

import java.util.Set;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController {
    private ExpertService expertService;

    @Autowired
    public DataValidationController(ExpertService expertService) {
        this.expertService = expertService;
    }
    /**
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/datavalidation", method = RequestMethod.GET)
    public String showPage(Model model) {
        Set<DiseaseGroup> diseaseInterests = expertService.getDiseaseInterests(1);
        model.addAttribute("diseaseInterests", diseaseInterests);
        return "datavalidation";
    }
}
