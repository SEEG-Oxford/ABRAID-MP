package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

/**
 * Controller for the experts page of system administration.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AdminExpertsController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AdminExpertsController.class);

    private ExpertService expertService;
    private GeoJsonObjectMapper json;

    @Autowired
    public AdminExpertsController(ExpertService expertService, GeoJsonObjectMapper geoJsonObjectMapper) {
        this.expertService = expertService;
        this.json = geoJsonObjectMapper;
    }

    /**
     * Returns the initial view to display.
     * @param model The model.
     * @return The ftl page name.
     * @throws com.fasterxml.jackson.core.JsonProcessingException if there is an error during JSON serialization
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admin/experts", method = RequestMethod.GET)
    public String showPage(Model model) throws JsonProcessingException {
            model.addAttribute("experts", json.writeValueAsString(expertService.getAllExperts()));
            return "admin/experts";
    }
}
