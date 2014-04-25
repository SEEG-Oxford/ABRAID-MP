package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Serves only to deliver the trivial "TGHN" page for demonstration purposes.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class TghnController extends AbstractController {

    /**
     * Return the almost-empty page, in which the IFrame (containing all data validation page content) sits.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/tghn", method = RequestMethod.GET)
    public String showPage() {
        return "tghn";
    }
}
