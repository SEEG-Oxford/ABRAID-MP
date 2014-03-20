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
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReviewResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController {
    /** Base URL for the geowiki. */
    public static final String GEOWIKI_BASE_URL = "/datavalidation";
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
     * Return the view to display.
     * @param model The model map.
     * @return The ftl page name.
     */
    @RequestMapping(value = GEOWIKI_BASE_URL, method = RequestMethod.GET)
    public String showPage(Model model) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        Set<DiseaseGroup> diseaseInterests = new HashSet<>();
        if (user != null) {
            diseaseInterests = expertService.getDiseaseInterests(user.getId());
        }
        model.addAttribute("diseaseInterests", diseaseInterests);
        return "datavalidation";
    }

    /**
     * Returns the disease occurrence points in need of review by the current user for a given disease id.
     * @param diseaseId The id of the disease to return occurrence points for.
     * @return A GeoJSON DTO containing the occurrence points.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseId}/occurrences",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> getDiseaseOccurrencesForReviewByCurrentUser(
            @PathVariable Integer diseaseId) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        List<DiseaseOccurrence> occurrences;

        try {
            occurrences = expertService.getDiseaseOccurrencesYetToBeReviewed(user.getId(), diseaseId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences), HttpStatus.OK);
    }

    /**
     * Saves the expert's review to the database.
     * @param diseaseId The id of the disease.
     * @param occurrenceId The id of the disease occurrence being reviewed.
     * @param review The string submitted by the expert, should only be YES, UNSURE or NO.
     * @return A HTTP status code response entity.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseId}/occurrences/{occurrenceId}/validate",
            method = RequestMethod.POST)
    public ResponseEntity submitReview(@PathVariable Integer diseaseId, @PathVariable Integer occurrenceId,
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

        boolean validInputParameters = diseaseService.doesDiseaseOccurrenceMatchDiseaseGroup(occurrenceId, diseaseId) &&
                (expertService.isDiseaseGroupInExpertsDiseaseInterests(diseaseId, expertId)) &&
                (!expertService.doesDiseaseOccurrenceReviewExist(expertId, occurrenceId));

        if (validInputParameters) {
            expertService.saveDiseaseOccurrenceReview(expertEmail, occurrenceId, diseaseOccurrenceReviewResponse);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
