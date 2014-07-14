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
            List<DiseaseGroup> diseaseGroups = getSortedDiseaseGroups();
            String diseaseGroupsJson = convertDiseaseGroupsToJson(diseaseGroups);
            model.addAttribute("diseaseGroups", diseaseGroupsJson);

            List<ValidatorDiseaseGroup> validatorDiseaseGroups = getSortedValidatorDiseaseGroups();
            String validatorDiseaseGroupsJson = convertValidatorDiseaseGroupsToJson(validatorDiseaseGroups);
            model.addAttribute("validatorDiseaseGroups", validatorDiseaseGroupsJson);

            return "admindiseasegroup";
        } catch (JsonProcessingException e) {
            LOGGER.error(DISEASE_GROUP_JSON_CONVERSION_ERROR, e);
            throw e;
        }
    }

    private List<DiseaseGroup> getSortedDiseaseGroups() {
        List<DiseaseGroup> diseaseGroups = diseaseService.getAllDiseaseGroups();
        sort(diseaseGroups, on(DiseaseGroup.class).getName());
        return diseaseGroups;
    }

    private String convertDiseaseGroupsToJson(List<DiseaseGroup> diseaseGroups) throws JsonProcessingException {
        List<JsonDiseaseGroup> jsonDiseaseGroups = new ArrayList<>();
        for (DiseaseGroup diseaseGroup : diseaseGroups) {
            jsonDiseaseGroups.add(new JsonDiseaseGroup(diseaseGroup));
        }
        return geoJsonObjectMapper.writeValueAsString(jsonDiseaseGroups);
    }

    private List<ValidatorDiseaseGroup> getSortedValidatorDiseaseGroups(){
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        sort(validatorDiseaseGroups, on(ValidatorDiseaseGroup.class).getName());
        return validatorDiseaseGroups;
    }

    private String convertValidatorDiseaseGroupsToJson(List<ValidatorDiseaseGroup> validatorDiseaseGroups)
            throws JsonProcessingException {
        List<JsonValidatorDiseaseGroup> jsonValidatorDiseaseGroups = new ArrayList<>();
        for (ValidatorDiseaseGroup validatorDiseaseGroup : validatorDiseaseGroups) {
            jsonValidatorDiseaseGroups.add(new JsonValidatorDiseaseGroup(validatorDiseaseGroup));
        }
        return geoJsonObjectMapper.writeValueAsString(jsonValidatorDiseaseGroups);
    }

    /**
     * Save the updated values of the disease group's parameters.
     * @param diseaseGroupId The id of the disease group.
     * @param name The name.
     * @param publicName The name used for public display.
     * @param shortName A shorter version of the name.
     * @param abbreviation The shortest version of the name.
     * @param groupType The DiseaseGroupType: Single, Cluster or Microcluster.
     * @param isGlobal Whether the disease group is global or tropical.
     * @param parentDiseaseGroupId The id of the disease group's parent disease group.
     * @param validatorDiseaseGroupId The id of the disease group's validator disease group.
     * @return A HTTP status code response entity: 200 for success, 400 for failure.
     * @throws Exception
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admindiseasegroup/{diseaseGroupId}/savemainsettings",
                    method = RequestMethod.POST)
    public ResponseEntity saveChanges(@PathVariable Integer diseaseGroupId, String name, String publicName,
                                      String shortName, String abbreviation, String groupType, Boolean isGlobal,
                                      Integer parentDiseaseGroupId, Integer validatorDiseaseGroupId) throws Exception {
        try {
            if (validInputs(name, groupType)) {
                DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
                saveProperties(diseaseGroup, name, publicName, shortName, abbreviation, groupType, isGlobal,
                    parentDiseaseGroupId, validatorDiseaseGroupId);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOGGER.error("Error", e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean validInputs(String name, String groupType) {
        return (name != null) && (groupType != null);
    }

    private void saveProperties(DiseaseGroup diseaseGroup, String name, String publicName, String shortName,
                                String abbreviation, String groupType, Boolean isGlobal, Integer parentId,
                                Integer validatorId){
        diseaseGroup.setName(name);
        diseaseGroup.setPublicName(publicName);
        diseaseGroup.setShortName(shortName);
        diseaseGroup.setAbbreviation(abbreviation);
        DiseaseGroupType type = DiseaseGroupType.valueOf(groupType);
        diseaseGroup.setGroupType(type);
        diseaseGroup.setGlobal(isGlobal);
        setParentDiseaseGroup(diseaseGroup, parentId);
        setValidatorDiseaseGroup(diseaseGroup, validatorId);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private void setParentDiseaseGroup(DiseaseGroup diseaseGroup, Integer parentId) {
        if ((diseaseGroup.getGroupType() != DiseaseGroupType.CLUSTER) && (parentId != null)) {
            DiseaseGroup parentDiseaseGroup = diseaseService.getDiseaseGroupById(parentId);
            diseaseGroup.setParentGroup(parentDiseaseGroup);
        }
    }

    private void setValidatorDiseaseGroup(DiseaseGroup diseaseGroup, Integer validatorId) {
        if (validatorId != null) {
            ValidatorDiseaseGroup validatorDiseaseGroup = diseaseService.getValidatorDiseaseGroupById(validatorId);
            diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        }
    }
}
