package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunRequesterException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonModelRunInformation;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonModelRunInformationBuilder;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonValidatorDiseaseGroup;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static org.springframework.util.StringUtils.hasText;

/**
 * Controller for the disease group page of system administration.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AdminDiseaseGroupController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AdminDiseaseGroupController.class);
    private static final String DISEASE_GROUP_JSON_CONVERSION_ERROR = "Cannot convert disease groups to JSON";

    /** The base URL for the system administration disease group controller methods. */
    public static final String ADMIN_DISEASE_GROUP_BASE_URL = "/admin/diseasegroup";

    private DiseaseService diseaseService;
    private GeoJsonObjectMapper geoJsonObjectMapper;
    private ModelRunWorkflowService modelRunWorkflowService;
    private ModelRunService modelRunService;

    @Autowired
    public AdminDiseaseGroupController(DiseaseService diseaseService, GeoJsonObjectMapper geoJsonObjectMapper,
                                       ModelRunWorkflowService modelRunWorkflowService,
                                       ModelRunService modelRunService) {
        this.diseaseService = diseaseService;
        this.geoJsonObjectMapper = geoJsonObjectMapper;
        this.modelRunWorkflowService = modelRunWorkflowService;
        this.modelRunService = modelRunService;
    }

    /**
     * Returns the initial view to display.
     * @param model The model.
     * @return The ftl page name.
     * @throws com.fasterxml.jackson.core.JsonProcessingException if there is an error during JSON serialization
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/", method = RequestMethod.GET)
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

    /**
     * Returns model run information for the specified disease group.
     * @param diseaseGroupId The id of the disease group for which to return model run information.
     * @return Model run information in a JSON object.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(
            value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/modelruninformation",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JsonModelRunInformation> getModelRunInformation(@PathVariable Integer diseaseGroupId) {
        ModelRun lastRequestedModelRun = modelRunService.getLastRequestedModelRun(diseaseGroupId);
        ModelRun lastCompletedModelRun = modelRunService.getLastCompletedModelRun(diseaseGroupId);
        DiseaseOccurrenceStatistics statistics = diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId);
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

        JsonModelRunInformation info = new JsonModelRunInformationBuilder()
                .populateLastModelRunText(lastRequestedModelRun)
                .populateHasModelBeenSuccessfullyRun(lastCompletedModelRun)
                .populateDiseaseOccurrencesText(statistics)
                .populateCanRunModelWithReason(diseaseGroup)
                .get();

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group for which to request the model run.
     * @return An error message string (empty if no error).
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(
            value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/requestmodelrun",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> requestModelRun(@PathVariable Integer diseaseGroupId) {
        try {
            modelRunWorkflowService.prepareForAndRequestManuallyTriggeredModelRun(diseaseGroupId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ModelRunRequesterException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    private List<ValidatorDiseaseGroup> getSortedValidatorDiseaseGroups() {
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        return sort(validatorDiseaseGroups, on(ValidatorDiseaseGroup.class).getName());
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
     * @throws Exception if cannot fetch disease group from database.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/save",
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity save(@PathVariable Integer diseaseGroupId, @RequestBody JsonDiseaseGroup settings) throws Exception {

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (validInputs(diseaseGroup, settings)) {
            if (saveProperties(diseaseGroup, settings)) {
                return new ResponseEntity(diseaseGroup.getId(), HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Add a new disease group, with the provided parameters.
     * @throws Exception if cannot fetch disease group from database.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/add",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity add(@RequestBody JsonDiseaseGroup settings) throws Exception {

        if (validInputs(settings)) {
            DiseaseGroup diseaseGroup = new DiseaseGroup();
            if (saveProperties(diseaseGroup, settings)) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    private boolean validInputs(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        return (diseaseGroup != null) && hasText(settings.getName()) && hasText(settings.getGroupType());
    }

    private boolean validInputs(JsonDiseaseGroup settings) {
        return hasText(settings.getName()) && hasText(settings.getGroupType());
    }

    private boolean saveProperties(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        diseaseGroup.setName(settings.getName());
        diseaseGroup.setPublicName(settings.getPublicName());
        diseaseGroup.setShortName(settings.getShortName());
        diseaseGroup.setAbbreviation(settings.getAbbreviation());
        DiseaseGroupType type = DiseaseGroupType.valueOf(settings.getGroupType());
        diseaseGroup.setGroupType(type);
        diseaseGroup.setGlobal(settings.getIsGlobal());
        diseaseGroup.setMinNewOccurrencesTrigger(settings.getMinNewOccurrencesTrigger());
        diseaseGroup.setMinDataVolume(settings.getMinDataVolume());
        diseaseGroup.setMinDistinctCountries(settings.getMinDistinctCountries());
        diseaseGroup.setMinHighFrequencyCountries(settings.getMinHighFrequencyCountries());
        diseaseGroup.setHighFrequencyThreshold(settings.getHighFrequencyThreshold());
        diseaseGroup.setOccursInAfrica(settings.getOccursInAfrica());
        if (setParentDiseaseGroup(diseaseGroup, settings) && setValidatorDiseaseGroup(diseaseGroup, settings)) {
            diseaseService.saveDiseaseGroup(diseaseGroup);
            return true;
        } else {
            return false;
        }
    }

    private boolean setParentDiseaseGroup(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        Integer parentId = settings.getParentDiseaseGroup().getId();
        if (diseaseGroup.getGroupType() == DiseaseGroupType.CLUSTER || parentId == null) {
            return true;
        }

        DiseaseGroup parentDiseaseGroup = diseaseService.getDiseaseGroupById(parentId);
        diseaseGroup.setParentGroup(parentDiseaseGroup);
        return (parentDiseaseGroup != null);
    }

    private boolean setValidatorDiseaseGroup(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        Integer validatorId = settings.getValidatorDiseaseGroup().getId();
        if (validatorId == null) {
            return true;
        }
        ValidatorDiseaseGroup validatorDiseaseGroup = diseaseService.getValidatorDiseaseGroupById(validatorId);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        return (validatorDiseaseGroup != null);
    }
}
