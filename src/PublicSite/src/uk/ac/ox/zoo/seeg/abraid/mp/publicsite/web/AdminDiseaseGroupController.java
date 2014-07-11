package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroupType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonValidatorDiseaseGroup;

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
            model.addAttribute("diseaseGroups", getSortedDiseaseGroupsJson());
            model.addAttribute("validatorDiseaseGroups", getSortedValidatorDiseaseGroups());
            return "admindiseasegroup";
        } catch (JsonProcessingException e) {
            LOGGER.error(DISEASE_GROUP_JSON_CONVERSION_ERROR, e);
            throw e;
        }
    }

    private String getSortedDiseaseGroupsJson() throws JsonProcessingException {
        List<DiseaseGroup> diseaseGroups = diseaseService.getAllDiseaseGroups();
        sort(diseaseGroups, on(DiseaseGroup.class).getName());
        return convertDiseaseGroupsToJson(diseaseGroups);
    }

    private String getSortedValidatorDiseaseGroups() throws JsonProcessingException {
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        sort(validatorDiseaseGroups, on(ValidatorDiseaseGroup.class).getName());
        return convertValidatorDiseaseGroupsToJson(validatorDiseaseGroups);
    }

    private String convertDiseaseGroupsToJson(List<DiseaseGroup> diseaseGroups) throws JsonProcessingException {
        List<JsonDiseaseGroup> jsonDiseaseGroups = new ArrayList<>();
        for (DiseaseGroup diseaseGroup : diseaseGroups) {
            jsonDiseaseGroups.add(new JsonDiseaseGroup(diseaseGroup));
        }
        return geoJsonObjectMapper.writeValueAsString(jsonDiseaseGroups);
    }

    private String convertValidatorDiseaseGroupsToJson(List<ValidatorDiseaseGroup> validatorDiseaseGroups) throws JsonProcessingException {
        List<JsonValidatorDiseaseGroup> jsonValidatorDiseaseGroups = new ArrayList<>();
        for (ValidatorDiseaseGroup validatorDiseaseGroup : validatorDiseaseGroups) {
            jsonValidatorDiseaseGroups.add(new JsonValidatorDiseaseGroup(validatorDiseaseGroup));
        }
        return geoJsonObjectMapper.writeValueAsString(jsonValidatorDiseaseGroups);
    }

    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admindiseasegroup/{diseaseGroupId}/save",
                    method = RequestMethod.POST)
    public ResponseEntity saveChanges(@PathVariable Integer diseaseGroupId, String name, String publicName,
                                      String shortName, String abbreviation, String groupType, boolean isGlobal,
                                      JsonDiseaseGroup parentDiseaseGroup, JsonValidatorDiseaseGroup validatorDiseaseGroup) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setName(name);
        diseaseGroup.setPublicName(publicName);
        diseaseGroup.setShortName(shortName);
        diseaseGroup.setAbbreviation(abbreviation);
        diseaseGroup.setGroupType(DiseaseGroupType.valueOf(groupType));
        diseaseGroup.setGlobal(isGlobal);
        diseaseGroup.setParentGroup(parentDiseaseGroup);
        diseaseService.saveDiseaseGroup(diseaseGroup);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
