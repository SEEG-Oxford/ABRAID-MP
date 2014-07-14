package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatistics;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunRequesterException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonModelRunInformation;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonModelRunInformationBuilder;

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
}
