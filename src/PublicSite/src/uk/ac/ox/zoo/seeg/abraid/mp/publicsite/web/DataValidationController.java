package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

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
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.support.ResponseView;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController {
    /** Base URL for the geowiki. */
    public static final String GEOWIKI_BASE_URL = "/datavalidation";
    /** Display name for the default disease to display to an anonymous user, corresponding to disease in static json.*/
    private static final String DEFAULT_VALIDATOR_DISEASE_GROUP_NAME = "dengue";
    private static final int DEFAULT_DISEASE_OCCURRENCE_COUNT = 10;
    private final CurrentUserService currentUserService;
    private final DiseaseService diseaseService;
    private final ExpertService expertService;
    private final LocationService locationService;

    @Autowired
    public DataValidationController(CurrentUserService currentUserService, DiseaseService diseaseService,
                                    ExpertService expertService, LocationService locationService) {
        this.currentUserService = currentUserService;
        this.diseaseService = diseaseService;
        this.expertService = expertService;
        this.locationService = locationService;
    }

    /**
     * Return the data validation page, in which the iframe (containing all page content) sits.
     * @return The ftl page name.
     */
    @RequestMapping(value = GEOWIKI_BASE_URL, method = RequestMethod.GET)
    public String showTab() {
        return "datavalidation";
    }

    /**
     * Return the view to display within the iframe, and provide the currently logged in user's disease interests.
     * @param model The model map.
     * @return The ftl page name.
     */
    @RequestMapping(value = GEOWIKI_BASE_URL + "/content", method = RequestMethod.GET)
    public String showPage(Model model) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        boolean userLoggedIn = (user != null);
        Integer diseaseOccurrenceReviewCount = 0;
        if (userLoggedIn) {
            int expertId = user.getId();
            diseaseOccurrenceReviewCount = expertService.getDiseaseOccurrenceReviewCount(expertId).intValue();
            List<ValidatorDiseaseGroup> diseaseInterests = expertService.getDiseaseInterests(expertId);
            model.addAttribute("diseaseInterests", sortByDisplayName(diseaseInterests));
            model.addAttribute("allOtherDiseases",
                    sortByDisplayName(getAllValidatorDiseaseGroupsExcludingDiseaseInterests(diseaseInterests)));
            model.addAttribute("validatorDiseaseGroupMap", diseaseService.getValidatorDiseaseGroupMap());
        } else {
            model.addAttribute("defaultValidatorDiseaseGroupName", DEFAULT_VALIDATOR_DISEASE_GROUP_NAME);
        }
        model.addAttribute("userLoggedIn", userLoggedIn);
        model.addAttribute("reviewCount", diseaseOccurrenceReviewCount);
        return "datavalidationcontent";
    }

    private List<ValidatorDiseaseGroup> getAllValidatorDiseaseGroupsExcludingDiseaseInterests(
            List<ValidatorDiseaseGroup> diseaseInterests) {
        List<ValidatorDiseaseGroup> list = diseaseService.getAllValidatorDiseaseGroups();
        list.removeAll(diseaseInterests);
        return list;
    }

    private List<ValidatorDiseaseGroup> sortByDisplayName(List<ValidatorDiseaseGroup> list) {
        Collections.sort(list, new Comparator<ValidatorDiseaseGroup>() {
            @Override
            public int compare(ValidatorDiseaseGroup o1, ValidatorDiseaseGroup o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return list;
    }

    /**
     * Returns the disease occurrence points in need of review by the current user for a given disease id.
     * @param validatorDiseaseGroupId The id of the validator disease group to return occurrence points for.
     * @return A GeoJSON DTO containing the occurrence points.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{validatorDiseaseGroupId}/occurrences",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> getDiseaseOccurrencesForReviewByCurrentUser(
            @PathVariable Integer validatorDiseaseGroupId) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        List<DiseaseOccurrence> occurrences;

        try {
            occurrences = expertService.getDiseaseOccurrencesYetToBeReviewed(user.getId(), validatorDiseaseGroupId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences), HttpStatus.OK);
    }

    @Secured({ "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseGroupId}/extent",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseView(DisplayJsonView.class)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> getDiseaseExtentForDiseaseGroup(
            @PathVariable Integer diseaseGroupId) {
        if (isDiseaseGroupGlobal(diseaseGroupId)) {
            List<GlobalAdminUnit> globalAdminUnits = locationService.getAllGlobalAdminUnits();
            Map<GlobalAdminUnit, DiseaseExtentClass> adminUnitDiseaseExtentClassMap =
                    diseaseService.getAdminUnitDiseaseExtentClassMap(globalAdminUnits, diseaseGroupId);
        } else {
            List<TropicalAdminUnit> tropicalAdminUnits = locationService.getAllTropicalAdminUnits();
        }
    }

    private boolean isDiseaseGroupGlobal(Integer diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        return diseaseGroup.isGlobal();
    }

    /**
     * Saves the expert's review to the database.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @param occurrenceId The id of the disease occurrence being reviewed.
     * @param review The string submitted by the expert, should only be YES, UNSURE or NO.
     * @return A HTTP status code response entity.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{validatorDiseaseGroupId}/occurrences/{occurrenceId}/validate",
            method = RequestMethod.POST)
    public ResponseEntity submitReview(@PathVariable Integer validatorDiseaseGroupId,
                                       @PathVariable Integer occurrenceId,
                                       String review) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        Integer expertId = user.getId();
        String expertEmail = user.getUsername();

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
                        validatorDiseaseGroupId) &&
                (!expertService.doesDiseaseOccurrenceReviewExist(expertId, occurrenceId));

        if (validInputParameters) {
            expertService.saveDiseaseOccurrenceReview(expertEmail, occurrenceId, diseaseOccurrenceReviewResponse);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
