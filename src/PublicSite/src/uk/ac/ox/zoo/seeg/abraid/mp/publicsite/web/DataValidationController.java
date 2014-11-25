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
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController extends AbstractController {
    /** Base URL for the geowiki. */
    public static final String GEOWIKI_BASE_URL = "/datavalidation";
    /** Display name for the default disease to display to an anonymous user, corresponding to disease in static json.*/
    private static final String DEFAULT_VALIDATOR_DISEASE_GROUP_NAME = "dengue";
    private static final String DEFAULT_DISEASE_GROUP_SHORT_NAME = "dengue";
    private final CurrentUserService currentUserService;
    private final DiseaseService diseaseService;
    private final ExpertService expertService;

    @Autowired
    public DataValidationController(CurrentUserService currentUserService, DiseaseService diseaseService,
                                    ExpertService expertService) {
        this.currentUserService = currentUserService;
        this.diseaseService = diseaseService;
        this.expertService = expertService;
    }

    /**
     * Return the data validation page, in which the iframe (containing all page content) sits.
     * @return The ftl page name.
     */
    @RequestMapping(value = GEOWIKI_BASE_URL, method = RequestMethod.GET)
    public String showTab() {
        return "datavalidation/index";
    }

    /**
     * Return the view to display within the iframe, and provide the currently logged in user's disease interests.
     * @param model The model map.
     * @return The ftl page name.
     */
    @RequestMapping(value = GEOWIKI_BASE_URL + "/content", method = RequestMethod.GET)
    @Transactional(rollbackFor = Exception.class)
    public String showPage(Model model) {
        PublicSiteUser user = currentUserService.getCurrentUser();

        boolean userLoggedIn = (user != null);
        boolean userIsSeeg = false;
        boolean showHelpText = false;

        Integer diseaseOccurrenceReviewCount = 0;
        Integer adminUnitReviewCount = 0;

        if (userLoggedIn) {
            int expertId = user.getId();

            Expert expert = getExpert(expertId);
            userIsSeeg = expert.isSeegMember();
            showHelpText = !expert.hasSeenHelpText();
            if (showHelpText) {
                expert.setHasSeenHelpText(true);
            }

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

        model.addAttribute("userLoggedIn", userLoggedIn);
        model.addAttribute("userSeeg", userIsSeeg);
        model.addAttribute("showHelpText", showHelpText);
        model.addAttribute("diseaseOccurrenceReviewCount", diseaseOccurrenceReviewCount);
        model.addAttribute("adminUnitReviewCount", adminUnitReviewCount);

        return "datavalidation/content";
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
     * Only SEEG users may view occurrences of disease groups during setup phase.
     * Other external users may only view occurrences of disease groups with automatic model runs enabled.
     * @param validatorDiseaseGroupId The id of the validator disease group for which to return occurrence points.
     * @return A GeoJSON DTO containing the occurrence points.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{validatorDiseaseGroupId}/occurrences",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> getDiseaseOccurrencesForReviewByCurrentUser(
            @PathVariable Integer validatorDiseaseGroupId) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        boolean userIsSeeg = userIsSeegMember(user);

        try {
            List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(
                user.getId(), userIsSeeg, validatorDiseaseGroupId);
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
            value = GEOWIKI_BASE_URL + "/diseases/{validatorDiseaseGroupId}/occurrences/{occurrenceId}/validate",
            method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity submitDiseaseOccurrenceReview(@PathVariable Integer validatorDiseaseGroupId,
                                                        @PathVariable Integer occurrenceId,
                                                        String review) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        Integer expertId = user.getId();

        // Only SEEG members may submit disease occurrence reviews for disease groups still in setup phase.
        DiseaseOccurrence occurrence = diseaseService.getDiseaseOccurrenceById(occurrenceId);
        if (!userIsSeegMember(user) && !occurrence.getDiseaseGroup().isAutomaticModelRunsEnabled()) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        // Convert the submitted string to its matching DiseaseOccurrenceReview enum.
        // Return a Bad Request ResponseEntity if value is anything other than YES, UNSURE or NO.
        DiseaseOccurrenceReviewResponse diseaseOccurrenceReviewResponse;
        try {
             diseaseOccurrenceReviewResponse = DiseaseOccurrenceReviewResponse.valueOf(review);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        boolean validInputParameters =
                diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(occurrenceId,
                validatorDiseaseGroupId) && (!expertService.doesDiseaseOccurrenceReviewExist(expertId, occurrenceId));

        if (validInputParameters) {
            expertService.saveDiseaseOccurrenceReview(expertId, occurrenceId, diseaseOccurrenceReviewResponse);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Returns the admin units, and their disease extent class, for a given disease id.
     * @param diseaseGroupId The id of the disease group for which the extent class is of interest.
     * @return A GeoJSON DTO containing the admin units.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseGroupId}/adminunits",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> getDiseaseExtentForDiseaseGroup(
            @PathVariable Integer diseaseGroupId) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        boolean userIsSEEG = userIsSeegMember(user);

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        GeoJsonDiseaseExtentFeatureCollection featureCollection = new GeoJsonDiseaseExtentFeatureCollection();

        // Only SEEG members may view disease extent for disease groups still in setup phase.
        // Other users see an empty extent.
        if (diseaseGroup.isAutomaticModelRunsEnabled() || userIsSEEG) {
            try {
                List<AdminUnitDiseaseExtentClass> diseaseExtent =
                        diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);
                List<AdminUnitReview> reviews =
                        expertService.getAllAdminUnitReviewsForDiseaseGroup(user.getId(), diseaseGroupId);
                featureCollection = new GeoJsonDiseaseExtentFeatureCollection(diseaseExtent, reviews);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(featureCollection, HttpStatus.OK);
    }

    /**
     * Returns the latest disease occurrences corresponding to an admin unit.
     * @param diseaseGroupId The id of the disease group for which the extent class is of interest.
     * @param gaulCode The gaulCode of the admin unit (global or tropical, depending on disease group isGlobal flag).
     * @return A GeoJSON DTO containing the admin units.
     */
    @Secured("ROLE_USER")
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseGroupId}/adminunits/{gaulCode}/occurrences",
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
                    diseaseService.getLatestOccurrencesForAdminUnitDiseaseExtentClass(diseaseGroup, gaulCode)
            ), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseGroupId}/adminunits/{gaulCode}/validate",
            method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity submitAdminUnitReview(@PathVariable Integer diseaseGroupId, @PathVariable Integer gaulCode,
                                                String review) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        boolean userIsSEEG = userIsSeegMember(user);
        Integer expertId = user.getId();

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        if (diseaseGroup == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // Only SEEG members may submit disease extent reviews for disease groups still in setup phase.
        if (!userIsSEEG && !diseaseGroup.isAutomaticModelRunsEnabled()) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        // Convert the submitted string to its matching DiseaseExtentClass row. Return a Bad Request ResponseEntity if
        // the review value is not found in the database.
        DiseaseExtentClass adminUnitReviewResponse = null;
        if (review != null) {
            adminUnitReviewResponse = diseaseService.getDiseaseExtentClass(review);
            if (adminUnitReviewResponse == null) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }

        expertService.saveAdminUnitReview(expertId, diseaseGroupId, gaulCode, adminUnitReviewResponse);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean userIsSeegMember(PublicSiteUser user) {
        if (user == null) {
            return false;
        } else {
            Expert expert = getExpert(user.getId());
            return expert.isSeegMember();
        }
    }

    private Expert getExpert(int userId) {
        Expert expert = expertService.getExpertById(userId);
        if (expert == null) {
            throw new IllegalArgumentException("Logged in user does not have an associated expert.");
        } else {
            return expert;
        }
    }
}
