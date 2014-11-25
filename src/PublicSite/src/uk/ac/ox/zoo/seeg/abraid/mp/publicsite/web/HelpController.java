package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Controller to deliver the content of the help text iframe on Data Validation modal panel.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class HelpController extends AbstractController {

    public HelpController() {
    }

    /**
     * Return the help text iframe content.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String showPage() {
        return "help";
    }
}
