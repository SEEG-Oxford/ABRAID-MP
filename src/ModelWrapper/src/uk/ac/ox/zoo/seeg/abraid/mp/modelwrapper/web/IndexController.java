package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the ModelWrapper Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class IndexController {
    /**
     * Request map for the index page.
     * @return The ftl index page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndexPage() {
        return "index";
    }
}
