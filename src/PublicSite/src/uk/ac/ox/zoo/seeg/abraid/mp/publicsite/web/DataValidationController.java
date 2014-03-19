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
    private final ExpertService expertService;
    private final CurrentUserService currentUserService;

    @Autowired
    public DataValidationController(ExpertService expertService, CurrentUserService currentUserService) {
        this.expertService = expertService;
        this.currentUserService = currentUserService;
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
        List<DiseaseOccurrence> occurrences = null;

        try {
            occurrences = expertService.getDiseaseOccurrencesYetToBeReviewed(user.getId(), diseaseId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection>(
                new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences), HttpStatus.OK);
    }

    /**
     * Saves the expert's review to the database.
     * @param diseaseId The id of the disease.
     * @param occurrenceId The id of the disease occurrence being reviewed.
     * @return A HTTP status code response entity.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(
            value = GEOWIKI_BASE_URL + "/diseases/{diseaseId}/occurrences/{occurrenceId}/validate",
            method = RequestMethod.POST)
    public ResponseEntity submitReview(@PathVariable Integer diseaseId, @PathVariable Integer occurrenceId) {
        PublicSiteUser currentUser = currentUserService.getCurrentUser();
        try {
            currentUser.getId();
//            expertService.submitDiseaseOccurrenceReview(currentUser.getId(), occurrenceId, review);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
