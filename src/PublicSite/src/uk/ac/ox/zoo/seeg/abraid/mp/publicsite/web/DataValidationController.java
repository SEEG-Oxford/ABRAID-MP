package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController {

    /**
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/datavalidation", method = RequestMethod.GET)
    public String showPage() {
        return "datavalidation";
    }
}
