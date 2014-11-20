package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Controller for displaying the about page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AboutController extends AbstractController {

    public AboutController() {
    }

    /**
     * Shows the about page.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String show() {
        return "about";
    }
}
