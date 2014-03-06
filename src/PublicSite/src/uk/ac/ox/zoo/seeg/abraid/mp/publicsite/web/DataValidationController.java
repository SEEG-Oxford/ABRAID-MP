package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.List;

/**
 * Controller for the expert data validation map page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class DataValidationController {

    private static final String GEOWIKI_BASE_URL = "/datavalidation";
    private final ExpertService expertService;
    private final CurrentUserService currentUserService;

    @Autowired
    public DataValidationController(ExpertService expertService, CurrentUserService currentUserService) {
        this.expertService = expertService;
        this.currentUserService = currentUserService;
    }

    /**
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = GEOWIKI_BASE_URL, method = RequestMethod.GET)
    public String showPage() {
        return "datavalidation";
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = GEOWIKI_BASE_URL + "/diseases/{diseaseId}/occurrences", method = RequestMethod.GET)
    @ResponseBody
    public GeoJsonFeatureCollection getDiseaseOccurrencesInNeedOfReviewForActiveUser(@PathVariable Integer diseaseId) {
        PublicSiteUser user = currentUserService.getCurrentUser();
        List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewed(user.getId(), diseaseId);
        return new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences);
    }
}
