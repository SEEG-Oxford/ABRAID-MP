package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Controller for the Atlas Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AtlasController extends AbstractController {
    private ModelRunService modelRunService;

    @Autowired
    public AtlasController(ModelRunService modelRunService) {
        this.modelRunService = modelRunService;
    }

    /**
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showPage() {
        return "atlas/index";
    }

    @RequestMapping(value = "/atlas/content", method = RequestMethod.GET)
    public String showAtlas(Model model) {

        model.addAttribute("layers", "[{\"disease\":\"dengue\",\"runs\":[{\"timestamp\":\"latest\",\"name\":\"deng_2014-05-16-13-28-57_482ae3ca-ab30-414d-acce-388baae7d83c\"}]}]");
        return "atlas/content";
    }
}
