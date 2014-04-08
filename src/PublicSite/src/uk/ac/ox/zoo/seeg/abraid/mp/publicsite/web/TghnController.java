package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class TghnController {

    @RequestMapping(value = "/tghn", method = RequestMethod.GET)
    public String showPage() {
        return "tghn";
    }
}
