package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonDiseaseGroup;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

/**
 * Controller for the disease group page of system administration.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AdminDiseaseGroupController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AdminDiseaseGroupController.class);
    private static final String DISEASE_GROUP_JSON_CONVERSION_ERROR = "Cannot convert disease groups to JSON";

    private DiseaseService diseaseService;
    private GeoJsonObjectMapper geoJsonObjectMapper;

    @Autowired
    public AdminDiseaseGroupController(DiseaseService diseaseService, GeoJsonObjectMapper geoJsonObjectMapper) {
        this.diseaseService = diseaseService;
        this.geoJsonObjectMapper = geoJsonObjectMapper;
    }

    /**
     * Return the initial view to display.
     * @param model The model.
     * @return The ftl page name.
     * @throws com.fasterxml.jackson.core.JsonProcessingException if there is an error during JSON serialization
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admindiseasegroup", method = RequestMethod.GET)
    public String showPage(Model model) throws JsonProcessingException {
        try {
            // Include information about all disease groups in the "initialData" attribute
            List<DiseaseGroup> diseaseGroups = getSortedDiseaseGroups();
            String diseaseGroupJson = convertDiseaseGroupsToJson(diseaseGroups);
            model.addAttribute("initialData", diseaseGroupJson);
            return "admindiseasegroup";
        } catch (JsonProcessingException e) {
            LOGGER.error(DISEASE_GROUP_JSON_CONVERSION_ERROR, e);
            throw e;
        }
    }

    private List<DiseaseGroup> getSortedDiseaseGroups() {
        List<DiseaseGroup> diseaseGroups = diseaseService.getAllDiseaseGroups();
        return sort(diseaseGroups, on(DiseaseGroup.class).getName());
    }

    private String convertDiseaseGroupsToJson(List<DiseaseGroup> diseaseGroups) throws JsonProcessingException {
        List<JsonDiseaseGroup> jsonDiseaseGroups = new ArrayList<>();
        for (DiseaseGroup diseaseGroup : diseaseGroups) {
            jsonDiseaseGroups.add(new JsonDiseaseGroup(diseaseGroup));
        }
        return geoJsonObjectMapper.writeValueAsString(jsonDiseaseGroups);
    }
}
