package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseExtentFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support.ResponseView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController extends AbstractController {
    /** Base URL for the Data Validation page. */
    public static final String DATA_VALIDATION_BASE_URL = "/datavalidation";
    /** Display name for the default disease to display to an anonymous user, corresponding to disease in static json.*/
    private static final String DEFAULT_VALIDATOR_DISEASE_GROUP_NAME = "dengue";
    private static final String DEFAULT_DISEASE_GROUP_SHORT_NAME = "dengue";
    private static final int DEFAULT_DISEASE_GROUP_ID = 87;
    private final CurrentUserService currentUserService;
    private final DiseaseService diseaseService;
    private final ExpertService expertService;
    private final GeometryService geometryService;

    @Autowired
    public DataValidationController(CurrentUserService currentUserService, DiseaseService diseaseService,
                                    ExpertService expertService, GeometryService geometryService) {
        this.currentUserService = currentUserService;
        this.diseaseService = diseaseService;
        this.expertService = expertService;
        this.geometryService = geometryService;
    }

    /**
     * Return the data validation page, in which the iframe (containing all page content) sits.
     * @return The ftl page name.
     */
    @RequestMapping(value = DATA_VALIDATION_BASE_URL, method = RequestMethod.GET)
    public String showTab() {
        return "datavalidation/index";
    }

    /**
     * Return the content of the help text iframe on Data Validation help modal panel.
     * @return The ftl page name.
     */
    @RequestMapping(value = DATA_VALIDATION_BASE_URL + "/help", method = RequestMethod.GET)
    public String showHelp() {
        return "datavalidation/help";
    }

    /**
     * Return the view to display within the iframe, and provide the currently logged in user's disease interests.
     * @param model The model map.
     * @return The ftl page name.
     */
    @RequestMapping(value = DATA_VALIDATION_BASE_URL + "/content", method = RequestMethod.GET)
    @Transactional(rollbackFor = Exception.class)
    public String showPage(Model model) {
        Integer expertId = currentUserService.getCurrentUserId();

        boolean userLoggedIn = (expertId != null);
        boolean userIsSeeg = false;
        boolean showHelpText = false;

        Integer diseaseOccurrenceReviewCount = 0;
        Integer adminUnitReviewCount = 0;

        List<DiseaseGroup> diseasesRequiringExtentInput = new ArrayList<>();
        List<DiseaseGroup> diseasesRequiringOccurrenceInput = new ArrayList<>();
        if (userLoggedIn) {
            Expert expert = getExpert(expertId);
            userIsSeeg = expert.isSeegMember();
            showHelpText = !expert.hasSeenHelpText();
            if (showHelpText) {
                expert.setHasSeenHelpText(true);
                expertService.saveExpert(expert);
            }

            diseasesRequiringExtentInput = diseaseService.getDiseaseGroupsNeedingExtentReviewByExpert(expert);
            diseasesRequiringOccurrenceInput =  diseaseService.getDiseaseGroupsNeedingOccurrenceReviewByExpert(expert);

            diseaseOccurrenceReviewCount = expertService.getDiseaseOccurrenceReviewCount(expertId).intValue();
            adminUnitReviewCount = expertService.getAdminUnitReviewCount(expertId).intValue();
            List<ValidatorDiseaseGroup> diseaseInterests = getSortedDiseaseInterests(expertId);

            model.addAttribute("diseaseInterests", diseaseInterests);
            model.addAttribute("allOtherDiseases",
                    getSortedValidatorDiseaseGroupsExcludingDiseaseInterests(diseaseInterests));
            model.addAttribute("validatorDiseaseGroupMap", diseaseService.getValidatorDiseaseGroupMap());
        } else {
            model.addAttribute("defaultValidatorDiseaseGroupName", DEFAULT_VALIDATOR_DISEASE_GROUP_NAME);
            model.addAttribute("defaultDiseaseGroupShortName", DEFAULT_DISEASE_GROUP_SHORT_NAME);
        }

        model.addAttribute("diseasesRequiringExtentInput", extractIds(diseasesRequiringExtentInput));
        model.addAttribute("diseasesRequiringOccurrenceInput", extractIds(diseasesRequiringOccurrenceInput));
        model.addAttribute("userLoggedIn", userLoggedIn);
        model.addAttribute("userSeeg", userIsSeeg);
        model.addAttribute("showHelpText", showHelpText);
        model.addAttribute("diseaseOccurrenceReviewCount", diseaseOccurrenceReviewCount);
        model.addAttribute("adminUnitReviewCount", adminUnitReviewCount);

        return "datavalidation/content";
    }

    private List<Integer> extractIds(List<DiseaseGroup> diseaseGroups) {
        return extract(diseaseGroups, on(DiseaseGroup.class).getId());
    }

    private List<ValidatorDiseaseGroup> getSortedDiseaseInterests(int expertId) {
        List<ValidatorDiseaseGroup> diseaseInterests = expertService.getDiseaseInterests(expertId);
        sortOnName(diseaseInterests);
        return diseaseInterests;
    }

    private List<ValidatorDiseaseGroup> getSortedValidatorDiseaseGroupsExcludingDiseaseInterests(
            List<ValidatorDiseaseGroup> diseaseInterests) {
        List<ValidatorDiseaseGroup> list = diseaseService.getAllValidatorDiseaseGroups();
        list.removeAll(diseaseInterests);
        sortOnName(list);
        return list;
    }

    private void sortOnName(List<ValidatorDiseaseGroup> validatorDiseaseGroupList) {
        Collections.sort(validatorDiseaseGroupList, new Comparator<ValidatorDiseaseGroup>() {
            @Override
            public int compare(ValidatorDiseaseGroup o1, ValidatorDiseaseGroup o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
    }

    /**
     * Returns the disease occurrence points in need of review by the current user for a given disease id.
     * Only SEEG users may view occurrences of disease groups before first model run prep.
     * Other external users may only view occurrences of disease groups with automatic model runs enabled.
     * @param validatorDiseaseGroupId The id of the validator disease group for which to return occurrence points.
     * @return A GeoJSON DTO containing the occurrence points.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = DATA_VALIDATION_BASE_URL + "/diseases/{validatorDiseaseGroupId}/occurrences",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> getDiseaseOccurrencesForReviewByCurrentUser(
            @PathVariable Integer validatorDiseaseGroupId) {
        Integer expertId = currentUserService.getCurrentUserId();
        boolean userIsSeeg = userIsSeegMember(expertId);

        try {
            List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(
                    expertId, userIsSeeg, validatorDiseaseGroupId);
            return new ResponseEntity<>(new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Saves the expert's review to the database.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @param occurrenceId The id of the disease occurrence being reviewed.
     * @param review The string submitted by the expert, should only be YES, UNSURE or NO.
     * @return A HTTP status code response entity.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
        value = DATA_VALIDATION_BASE_URL + "/diseases/{validatorDiseaseGroupId}/occurrences/{occurrenceId}/validate",
        method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity submitDiseaseOccurrenceReview(@PathVariable Integer validatorDiseaseGroupId,
                                                        @PathVariable Integer occurrenceId,
                                                        String review) {
        Integer expertId = currentUserService.getCurrentUserId();

        // Basic validation
        if (validatorDiseaseGroupId == null || occurrenceId == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Convert the input values to domain objects, and check they all mapped across correctly
        DiseaseOccurrence occurrence = diseaseService.getDiseaseOccurrenceById(occurrenceId);
        ValidatorDiseaseGroup validatorDiseaseGroup =
                diseaseService.getValidatorDiseaseGroupById(validatorDiseaseGroupId);
        DiseaseOccurrenceReviewResponse diseaseOccurrenceReviewResponse =
                DiseaseOccurrenceReviewResponse.parseFromString(review);
        if (occurrence == null || validatorDiseaseGroup == null ||
                (review != null && diseaseOccurrenceReviewResponse == null)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Do extended checks -
        // * The specified occurrence matches the specified validator disease group.
        // * The specified occurrence hasn't already been reviewed by this expert.
        // * The specified occurrence's disease group is accessible to this expert.
        if (!doesDiseaseOccurrenceBelongToValidatorDiseaseGroup(occurrence, validatorDiseaseGroup) ||
                hasExpertReviewedOccurrence(expertId, occurrence)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (!diseaseGroupIsAccessibleToExpert(expertId, occurrence.getDiseaseGroup())) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        expertService.saveDiseaseOccurrenceReview(expertId, occurrenceId, diseaseOccurrenceReviewResponse);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns the admin units, and their disease extent class, for a given disease id.
     * @param diseaseGroupId The id of the disease group for which the extent class is of interest.
     * @return A GeoJSON DTO containing the admin units.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = DATA_VALIDATION_BASE_URL + "/diseases/{diseaseGroupId}/adminunits",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> getDiseaseExtentForDiseaseGroup(
            @PathVariable Integer diseaseGroupId) {
        Integer expertId = currentUserService.getCurrentUserId();

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        GeoJsonDiseaseExtentFeatureCollection featureCollection;
        // Only SEEG members may view disease extent for disease groups before first model run prep.
        // Other users see an empty extent.
        if (diseaseGroupIsAccessibleToExpert(expertId, diseaseGroup)) {
            List<AdminUnitDiseaseExtentClass> diseaseExtent =
                    diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);
            List<AdminUnitReview> reviews =
                    expertService.getAllAdminUnitReviewsForDiseaseGroup(expertId, diseaseGroupId);
            featureCollection = new GeoJsonDiseaseExtentFeatureCollection(diseaseExtent, reviews);
        } else {
            featureCollection = new GeoJsonDiseaseExtentFeatureCollection(
                    new ArrayList<AdminUnitDiseaseExtentClass>(), new ArrayList<AdminUnitReview>());
        }
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    /**
     * Returns the admin units, and their disease extent class, for the default disease (Dengue). The feature collection
     * is constructed with an empty list of reviews (an anonymous user cannot submit reviews), so the polygons will all
     * be selectable on Data Validation page.
     * @return A GeoJSON DTO containing the admin units.
     */
    @RequestMapping(
            value = DATA_VALIDATION_BASE_URL + "/defaultadminunits",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> getDefaultDiseaseExtent() {
        List<AdminUnitDiseaseExtentClass> diseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(DEFAULT_DISEASE_GROUP_ID);
        GeoJsonDiseaseExtentFeatureCollection featureCollection =
                new GeoJsonDiseaseExtentFeatureCollection(diseaseExtent, new ArrayList<AdminUnitReview>());
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    /**
     * Returns the latest disease occurrences corresponding to an admin unit.
     * @param diseaseGroupId The id of the disease group for which the extent class is of interest.
     * @param gaulCode The gaulCode of the admin unit (global or tropical, depending on disease group's isGlobal flag).
     * @return A GeoJSON DTO containing the disease occurrences.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = DATA_VALIDATION_BASE_URL + "/diseases/{diseaseGroupId}/adminunits/{gaulCode}/occurrences",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection>
        getLatestDiseaseOccurrencesForAdminUnitDiseaseExtentClass(@PathVariable Integer diseaseGroupId,
                                                                  @PathVariable Integer gaulCode) {
        try {
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
            return new ResponseEntity<>(new GeoJsonDiseaseOccurrenceFeatureCollection(
                    diseaseService.getLatestValidatorOccurrencesForAdminUnitDiseaseExtentClass(diseaseGroup, gaulCode)
            ), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns the latest disease occurrences corresponding to an admin unit for the default disease, to anonymous user.
     * @param gaulCode The gaulCode of the admin unit (global or tropical, depending on disease group's isGlobal flag).
     * @return A GeoJSON DTO containing the disease occurrences.
     */
    @RequestMapping(
            value = DATA_VALIDATION_BASE_URL + "/defaultadminunits/{gaulCode}/occurrences",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection>
    getLatestDiseaseOccurrencesForAdminUnitDiseaseExtentClassForDefaultDiseaseGroup(@PathVariable Integer gaulCode) {
        return getLatestDiseaseOccurrencesForAdminUnitDiseaseExtentClass(DEFAULT_DISEASE_GROUP_ID, gaulCode);
    }

    /**
     * Saves the expert's review to the database.
     * @param diseaseGroupId The id of the disease group.
     * @param gaulCode The gaulCode of the admin unit being reviewed.
     * @param review The string submitted by the expert, should only be one of:
     *               PRESENCE, POSSIBLE_PRESENCE, UNCERTAIN, POSSIBLE_ABSENCE, or ABSENCE.
     * @return A HTTP status code response entity.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = DATA_VALIDATION_BASE_URL + "/diseases/{diseaseGroupId}/adminunits/{gaulCode}/validate",
            method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity submitAdminUnitReview(@PathVariable Integer diseaseGroupId, @PathVariable Integer gaulCode,
                                                String review) {
        Integer expertId = currentUserService.getCurrentUserId();

        // Basic validation - review is allowed to be null ("I don't know")
        if (diseaseGroupId == null || gaulCode == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Convert the input values to domain objects, and check they all mapped across correctly
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        AdminUnitGlobalOrTropical adminUnit =
                geometryService.getAdminUnitGlobalOrTropicalByGaulCode(diseaseGroup, gaulCode);
        DiseaseExtentClass adminUnitReviewResponse = diseaseService.getDiseaseExtentClass(review);
        if (diseaseGroup == null || adminUnit == null || (review != null && adminUnitReviewResponse == null)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Do extended checks -
        // * The specified admin unit hasn't already been reviewed by this expert, in the current extent
        // * The specified occurrence's disease group is accessible to this expert.
        if (hasExpertReviewedAdminUnitSinceLastGeneration(expertId, diseaseGroup, adminUnit)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else if (!diseaseGroupIsAccessibleToExpert(expertId, diseaseGroup)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        expertService.saveAdminUnitReview(expertId, diseaseGroupId, gaulCode, adminUnitReviewResponse);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean userIsSeegMember(Integer expertId) {
        if (expertId == null) {
            return false;
        } else {
            Expert expert = getExpert(expertId);
            return expert.isSeegMember();
        }
    }

    private Expert getExpert(int expertId) {
        Expert expert = expertService.getExpertById(expertId);
        if (expert == null) {
            throw new IllegalArgumentException("Logged in user does not have an associated expert.");
        } else {
            return expert;
        }
    }

    private boolean diseaseGroupIsAccessibleToExpert(Integer expertId, DiseaseGroup diseaseGroup) {
        // Only SEEG members may interact with disease groups before the first model run is requested.
        return userIsSeegMember(expertId) || diseaseGroup.getLastModelRunPrepDate() != null;
    }

    private boolean doesDiseaseOccurrenceBelongToValidatorDiseaseGroup(
            DiseaseOccurrence occurrence, ValidatorDiseaseGroup validatorDiseaseGroup) {
        return validatorDiseaseGroup.equals(occurrence.getValidatorDiseaseGroup());
    }

    private boolean hasExpertReviewedOccurrence(Integer expertId, DiseaseOccurrence occurrence) {
        return expertService.doesDiseaseOccurrenceReviewExist(expertId, occurrence.getId());
    }

    private boolean hasExpertReviewedAdminUnitSinceLastGeneration(
            Integer expertId, DiseaseGroup diseaseGroup, AdminUnitGlobalOrTropical adminUnit) {
        return expertService.doesAdminUnitReviewExistForLatestDiseaseExtent(expertId, diseaseGroup, adminUnit);
    }
}
