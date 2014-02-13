package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for the Atlas Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class IndexController {

    private DiseaseService diseaseService;
    private ExpertService expertService;

    @Autowired
    public IndexController(DiseaseService diseaseService, ExpertService expertService) {
        this.diseaseService = diseaseService;
        this.expertService = expertService;
    }

    /**
     * Gets all diseases in database, sorts them alphabetically, and adds to the model map.
     * @param model The data model map.
     * @return The ftl page view name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAll(Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Expert expert = expertService.getExpertByEmail(username);
        model.addAttribute("expertname", expert.getName());

        List<Disease> allDiseases = diseaseService.getAllDiseases();
        Collections.sort(allDiseases, new Comparator<Disease>() {
            @Override
            public int compare(Disease o1, Disease o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.addAttribute("diseases", allDiseases);
        return "index";
    }
}
