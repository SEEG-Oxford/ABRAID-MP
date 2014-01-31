package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

/**
 * Copyright (c) 2014 University of Oxford
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloWorldController {
    @RequestMapping(value="/helloWorld", method = RequestMethod.GET)
    public String helloWorld(@RequestParam("messageId") int messageId, Model model) {
        String message = "The default message";
        if (messageId == 1) {
            message = "The special message";
        }

        model.addAttribute("message", message);

        // This is the view name (helloWorld.ftl in WEB-INF/freemarker)
        return "helloWorld";
    }
}
