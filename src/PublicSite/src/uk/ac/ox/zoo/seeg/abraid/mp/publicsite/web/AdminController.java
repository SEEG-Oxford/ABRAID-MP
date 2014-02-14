package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

/**
 * Controller for temporary "Admin" page,
 * only ROLE_ADMINISTRATOR users are authorised to view.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AdminController {

    private ExpertService expertService;

    @Autowired
    public AdminController(ExpertService expertService) {
        this.expertService = expertService;
    }

    /**
     * Constructs a personalised welcome message to logged in user.
     * @param model The data model map.
     * @return The ftl page view name.
     */
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String greetUser(Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Expert expert = expertService.getExpertByEmail(username);
        model.addAttribute("welcomemessage", "Hello " + expert.getName());
        return "admin";
    }
}
