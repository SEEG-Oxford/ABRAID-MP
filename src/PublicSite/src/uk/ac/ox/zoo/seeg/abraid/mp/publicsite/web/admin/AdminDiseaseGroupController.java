package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import ch.lambdaj.function.matcher.LambdaJMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.BatchDatesValidator;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelWrapperWebServiceAsyncWrapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;

import static ch.lambdaj.Lambda.*;
import static org.springframework.util.StringUtils.hasText;

/**
 * Controller for the disease group page of system administration.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AdminDiseaseGroupController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AdminDiseaseGroupController.class);
    private static final String DISEASE_GROUP_JSON_CONVERSION_ERROR = "Cannot convert disease groups to JSON";
    private static final String SAVE_DISEASE_GROUP_SUCCESS = "Successfully saved changes to disease group %d (%s)";
    private static final String SAVE_DISEASE_GROUP_ERROR = "Error saving changes to disease group %d (%s)";
    private static final String ADD_DISEASE_GROUP_SUCCESS = "Successfully added new disease group %d (%s)";
    private static final String ADD_DISEASE_GROUP_ERROR = "Error adding new disease group (%s)";
    private static final String INVALID_COMBINATION_OF_INPUTS_ERROR = "Invalid combination of inputs";

    private static final String FAILED_TO_SYNCHRONISE_DISEASE_GROUPS =
            "Failed to synchronise the disease groups with all model wrapper instances";
    /** The base URL for the system administration disease group controller methods. */
    public static final String ADMIN_DISEASE_GROUP_BASE_URL = "/admin/diseases";

    private DiseaseService diseaseService;
    private AbraidJsonObjectMapper objectMapper;
    private ModelRunWorkflowService modelRunWorkflowService;
    private ModelRunService modelRunService;
    private DiseaseOccurrenceSpreadHelper helper;
    private ModelWrapperWebServiceAsyncWrapper modelWrapperWebServiceAsyncWrapper;
    private BatchDatesValidator batchDatesValidator;

    private Comparator<String> caseInsensitiveComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    };

    @Autowired
    public AdminDiseaseGroupController(DiseaseService diseaseService, AbraidJsonObjectMapper objectMapper,
                                       ModelRunWorkflowService modelRunWorkflowService,
                                       ModelRunService modelRunService, DiseaseOccurrenceSpreadHelper helper,
                                       BatchDatesValidator batchDatesValidator,
                                       ModelWrapperWebServiceAsyncWrapper modelWrapperWebServiceAsyncWrapper) {
        this.diseaseService = diseaseService;
        this.objectMapper = objectMapper;
        this.modelRunWorkflowService = modelRunWorkflowService;
        this.modelRunService = modelRunService;
        this.helper = helper;
        this.modelWrapperWebServiceAsyncWrapper = modelWrapperWebServiceAsyncWrapper;
        this.batchDatesValidator = batchDatesValidator;
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

            return "admin/diseasegroups/index";
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
        ModelRun lastCompletedModelRun = modelRunService.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId);
        DiseaseOccurrenceStatistics statistics = diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId);
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        List<DiseaseOccurrence> goldStandardOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, true);

        JsonModelRunInformation info = new JsonModelRunInformationBuilder()
                .populateLastModelRunText(lastRequestedModelRun)
                .populateHasModelBeenSuccessfullyRun(lastCompletedModelRun)
                .populateDiseaseOccurrencesText(statistics)
                .populateCanRunModelWithReason(diseaseGroup)
                .populateBatchDateParameters(lastCompletedModelRun, statistics)
                .populateHasGoldStandardOccurrences(goldStandardOccurrences)
                .get();

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    /**
     * Generates a disease extent for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be used, otherwise false.
     * @return An error status: 204 for success, 404 if disease group cannot be found in database.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/generatediseaseextent",
            method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity generateDiseaseExtent(@PathVariable int diseaseGroupId,
                                                boolean onlyUseGoldStandardOccurrences) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        DiseaseProcessType processType = onlyUseGoldStandardOccurrences ?
                DiseaseProcessType.MANUAL_GOLD_STANDARD : DiseaseProcessType.MANUAL;

        modelRunWorkflowService.generateDiseaseExtent(diseaseGroupId, processType);
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group for which to request the model run.
     * @param batchStartDate The start date of the occurrences batch. Must be in ISO 8601 format for correct parsing.
     * @param batchEndDate The end date of the occurrences batch. Must be in ISO 8601 format for correct parsing.
     * @param onlyUseGoldStandardOccurrences True if only "gold standard" occurrences should be used, otherwise false.
     * @return An error message string (empty if no error).
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(
            value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/requestmodelrun",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public synchronized ResponseEntity<String> requestModelRun(@PathVariable int diseaseGroupId, String batchStartDate,
                                                               String batchEndDate,
                                                               boolean onlyUseGoldStandardOccurrences) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!checkCorrectModelRunParametersProvided(onlyUseGoldStandardOccurrences, batchStartDate, batchEndDate)) {
            return new ResponseEntity<>(INVALID_COMBINATION_OF_INPUTS_ERROR, HttpStatus.BAD_REQUEST);
        }

        try {
            if (onlyUseGoldStandardOccurrences) {
                modelRunWorkflowService.prepareForAndRequestModelRun(
                        diseaseGroupId, DiseaseProcessType.MANUAL_GOLD_STANDARD, null, null);
            } else {
                // Ensure that the batch date range is from start of day to end of day, then validate the dates
                DateTime parsedBatchStartDate = parseAndShiftToStartOfDay(batchStartDate);
                DateTime parsedBatchEndDate = parseAndShiftToEndOfDay(batchEndDate);
                batchDatesValidator.validate(diseaseGroup, parsedBatchStartDate, parsedBatchEndDate);

                modelRunWorkflowService.prepareForAndRequestModelRun(
                        diseaseGroupId, DiseaseProcessType.MANUAL, parsedBatchStartDate, parsedBatchEndDate);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ModelRunWorkflowException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * Enables automatic model runs for the specified disease group.
     * @param diseaseGroupId The id of the disease group for which to enable automatic model runs.
     * @return An error status: 204 for success, 404 if disease group cannot be found in database.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/automaticmodelruns",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity enableAutomaticModelRuns(@PathVariable int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup != null) {
            modelRunWorkflowService.enableAutomaticModelRuns(diseaseGroupId);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Save the updated values of the disease group's parameters.
     * @param diseaseGroupId The id of the disease group.
     * @param settings The JSON containing the new values to save.
     * @return HTTP Status code: 204 for success, 400 if any inputs are invalid.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/save",
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity save(@PathVariable int diseaseGroupId, @RequestBody JsonDiseaseGroup settings) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if ((diseaseGroup != null) && validInputs(settings)) {
            if (saveProperties(diseaseGroup, settings)) {
                LOGGER.info(String.format(SAVE_DISEASE_GROUP_SUCCESS, diseaseGroupId, settings.getName()));
                syncDiseaseGroupWithModelWrapper(diseaseGroup);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
        LOGGER.info(String.format(SAVE_DISEASE_GROUP_ERROR, diseaseGroupId, settings.getName()));
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Synchronises all sufficiently configured diseases with each known ModelWrapper instance.
     * @return 204 for success, otherwise 500.
     * @throws java.lang.Exception if a problem is encountered communicating with the ModelWrapper instances.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/sync", method = RequestMethod.POST)
    public ResponseEntity syncAllDiseasesWithModelWrapper() throws Exception {
        Collection<DiseaseGroup> diseaseGroups = diseaseService.getAllDiseaseGroups();
        diseaseGroups = filter(new LambdaJMatcher<DiseaseGroup>() {
            @Override
            public boolean matches(Object o) {
                return checkDiseaseIsSufficientlyConfiguredToSyncWithModelWrapper((DiseaseGroup) o);
            }
        }, diseaseGroups);

        Future<Boolean> result = modelWrapperWebServiceAsyncWrapper.publishAllDiseases(diseaseGroups);

        if (!result.get()) {
            LOGGER.warn(FAILED_TO_SYNCHRONISE_DISEASE_GROUPS);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void syncDiseaseGroupWithModelWrapper(DiseaseGroup diseaseGroup) {
        if (checkDiseaseIsSufficientlyConfiguredToSyncWithModelWrapper(diseaseGroup)) {
            modelWrapperWebServiceAsyncWrapper.publishSingleDisease(diseaseGroup);
        }
    }

    private boolean checkDiseaseIsSufficientlyConfiguredToSyncWithModelWrapper(DiseaseGroup diseaseGroup) {
        return
            diseaseGroup.getPublicName() != null &&
            diseaseGroup.getShortName() != null &&
            diseaseGroup.getAbbreviation() != null &&
            diseaseGroup.isGlobal() != null &&
            diseaseGroup.getGroupType() != null;
    }

    /**
     * Add a new disease group, with the provided parameters.
     * @param settings The new values to be saved.
     * @return HTTP Status code: 204 for success, 400 if any inputs are invalid.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/add",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity add(@RequestBody JsonDiseaseGroup settings) {
        if (validInputs(settings)) {
            DiseaseGroup diseaseGroup = new DiseaseGroup();
            if (saveProperties(diseaseGroup, settings)) {
                LOGGER.info(String.format(ADD_DISEASE_GROUP_SUCCESS, diseaseGroup.getId(), settings.getName()));
                syncDiseaseGroupWithModelWrapper(diseaseGroup);
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
        }
        LOGGER.info(String.format(ADD_DISEASE_GROUP_ERROR, settings.getName()));
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Gets a page containing the disease occurrence spread table, for the specified disease group.
     * @param model The model.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease occurrence spread table page.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_DISEASE_GROUP_BASE_URL + "/{diseaseGroupId}/spread", method = RequestMethod.GET)
    public String getDiseaseOccurrenceSpread(Model model, @PathVariable int diseaseGroupId) {
        DiseaseOccurrenceSpreadTable table = helper.getDiseaseOccurrenceSpreadTable(diseaseGroupId);
        model.addAttribute("table", table);
        return "admin/diseasegroups/occurrencespread";
    }

    private List<DiseaseGroup> getSortedDiseaseGroups() {
        List<DiseaseGroup> diseaseGroups = diseaseService.getAllDiseaseGroups();
        return sort(diseaseGroups, on(DiseaseGroup.class).getName(), caseInsensitiveComparator);
    }

    private String convertDiseaseGroupsToJson(List<DiseaseGroup> diseaseGroups) throws JsonProcessingException {
        List<JsonDiseaseGroup> jsonDiseaseGroups = new ArrayList<>();
        for (DiseaseGroup diseaseGroup : diseaseGroups) {
            jsonDiseaseGroups.add(new JsonDiseaseGroup(diseaseGroup));
        }
        return objectMapper.writeValueAsString(jsonDiseaseGroups);
    }

    private List<ValidatorDiseaseGroup> getSortedValidatorDiseaseGroups() {
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        return sort(validatorDiseaseGroups, on(ValidatorDiseaseGroup.class).getName(), caseInsensitiveComparator);
    }

    private String convertValidatorDiseaseGroupsToJson(List<ValidatorDiseaseGroup> validatorDiseaseGroups)
            throws JsonProcessingException {
        List<JsonValidatorDiseaseGroup> jsonValidatorDiseaseGroups = new ArrayList<>();
        for (ValidatorDiseaseGroup validatorDiseaseGroup : validatorDiseaseGroups) {
            jsonValidatorDiseaseGroups.add(new JsonValidatorDiseaseGroup(validatorDiseaseGroup));
        }
        return objectMapper.writeValueAsString(jsonValidatorDiseaseGroups);
    }

    private boolean validInputs(JsonDiseaseGroup settings) {
        String groupType = settings.getGroupType();
        return hasText(settings.getName()) && hasText(groupType) && isValidGroupType(groupType);
    }

    private boolean isValidGroupType(String groupType) {
        try {
            DiseaseGroupType.valueOf(groupType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean saveProperties(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        diseaseGroup.setName(settings.getName());
        diseaseGroup.setPublicName(settings.getPublicName());
        diseaseGroup.setShortName(settings.getShortName());
        diseaseGroup.setAbbreviation(settings.getAbbreviation());
        diseaseGroup.setGroupType(DiseaseGroupType.valueOf(settings.getGroupType()));
        diseaseGroup.setGlobal(settings.getIsGlobal());
        diseaseGroup.setMinNewLocationsTrigger(settings.getMinNewLocations());
        diseaseGroup.setMinEnvironmentalSuitability(settings.getMinEnvironmentalSuitability());
        diseaseGroup.setMinDistanceFromDiseaseExtent(settings.getMinDistanceFromDiseaseExtent());
        diseaseGroup.setMinDataVolume(settings.getMinDataVolume());
        diseaseGroup.setMinDistinctCountries(settings.getMinDistinctCountries());
        diseaseGroup.setMinHighFrequencyCountries(settings.getMinHighFrequencyCountries());
        diseaseGroup.setHighFrequencyThreshold(settings.getHighFrequencyThreshold());
        diseaseGroup.setOccursInAfrica(settings.getOccursInAfrica());
        diseaseGroup.setUseMachineLearning(settings.getUseMachineLearning());
        diseaseGroup.setMaxEnvironmentalSuitabilityWithoutML(settings.getMaxEnvironmentalSuitabilityWithoutML());
        setDiseaseExtentParameters(diseaseGroup, settings.getDiseaseExtentParameters());
        if (setParentDiseaseGroup(diseaseGroup, settings) && setValidatorDiseaseGroup(diseaseGroup, settings)) {
            diseaseService.saveDiseaseGroup(diseaseGroup);
            return true;
        } else {
            return false;
        }
    }

    private boolean setParentDiseaseGroup(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        if (!hasParentDiseaseGroupSpecified(settings) || diseaseGroup.getGroupType() == DiseaseGroupType.CLUSTER) {
            return true;
        }

        Integer parentId = settings.getParentDiseaseGroup().getId();
        DiseaseGroup parentDiseaseGroup = diseaseService.getDiseaseGroupById(parentId);
        diseaseGroup.setParentGroup(parentDiseaseGroup);
        return (parentDiseaseGroup != null);
    }

    private boolean hasParentDiseaseGroupSpecified(JsonDiseaseGroup settings) {
        return ((settings.getParentDiseaseGroup() != null) && (settings.getParentDiseaseGroup().getId() != null));
    }

    private boolean setValidatorDiseaseGroup(DiseaseGroup diseaseGroup, JsonDiseaseGroup settings) {
        if (!hasValidatorDiseaseGroupSpecified(settings)) {
            return true;
        }
        Integer validatorId = settings.getValidatorDiseaseGroup().getId();
        ValidatorDiseaseGroup validatorDiseaseGroup = diseaseService.getValidatorDiseaseGroupById(validatorId);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        return (validatorDiseaseGroup != null);
    }

    private boolean hasValidatorDiseaseGroupSpecified(JsonDiseaseGroup settings) {
        return ((settings.getValidatorDiseaseGroup() != null) && (settings.getValidatorDiseaseGroup().getId() != null));
    }

    private void setDiseaseExtentParameters(DiseaseGroup diseaseGroup, JsonDiseaseExtent newValues) {
        if (newValues != null) {
            if (diseaseGroup.getDiseaseExtentParameters() == null) {
                addDiseaseExtent(diseaseGroup, newValues);
            } else {
                updateDiseaseExtent(diseaseGroup, newValues);
            }
        }
    }

    private void addDiseaseExtent(DiseaseGroup diseaseGroup, JsonDiseaseExtent newValues) {
        DiseaseExtent parameters = new DiseaseExtent(
                diseaseGroup,
                newValues.getMinValidationWeighting(),
                newValues.getMinOccurrencesForPresence(),
                newValues.getMinOccurrencesForPossiblePresence(),
                newValues.getMaxMonthsAgoForHigherOccurrenceScore(),
                newValues.getLowerOccurrenceScore(),
                newValues.getHigherOccurrenceScore()
        );
        diseaseGroup.setDiseaseExtentParameters(parameters);
    }

    private void updateDiseaseExtent(DiseaseGroup diseaseGroup, JsonDiseaseExtent newValues) {
        DiseaseExtent parameters = diseaseGroup.getDiseaseExtentParameters();
        parameters.setMinValidationWeighting(newValues.getMinValidationWeighting());
        parameters.setMinOccurrencesForPresence(newValues.getMinOccurrencesForPresence());
        parameters.setMinOccurrencesForPossiblePresence(newValues.getMinOccurrencesForPossiblePresence());
        parameters.setMaxMonthsAgoForHigherOccurrenceScore(newValues.getMaxMonthsAgoForHigherOccurrenceScore());
        parameters.setLowerOccurrenceScore(newValues.getLowerOccurrenceScore());
        parameters.setHigherOccurrenceScore(newValues.getHigherOccurrenceScore());
    }

    private boolean checkCorrectModelRunParametersProvided(
            boolean onlyUseGoldStandardOccurrences, String batchStartDate, String batchEndDate) {
        boolean goldRun = (onlyUseGoldStandardOccurrences) && (batchEndDate == null) && (batchStartDate == null);
        boolean manualRun = (!onlyUseGoldStandardOccurrences) && (batchEndDate != null) && (batchStartDate != null);
        return goldRun || manualRun;
    }

    private DateTime parseAndShiftToStartOfDay(String dateString) throws IllegalArgumentException {
        return parseDateTime(dateString).withTimeAtStartOfDay();
    }

    private DateTime parseAndShiftToEndOfDay(String dateString) throws IllegalArgumentException {
        return parseAndShiftToStartOfDay(dateString).plusDays(1).minusMillis(1);
    }

    private DateTime parseDateTime(String dateString) throws IllegalArgumentException {
        DateTime parsed = DateTime.parse(dateString);
        if (parsed == null) {
            throw new IllegalArgumentException("Could not parse date time");
        }
        return parsed;
    }
}
