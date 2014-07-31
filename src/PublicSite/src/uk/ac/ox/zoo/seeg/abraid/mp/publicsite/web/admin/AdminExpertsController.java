package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;

/**
 * Controller for the experts page of system administration.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AdminExpertsController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AdminExpertsController.class);
    private static final String LOG_VALIDATION_FAILURE =
            "Expert administration form encountered %s validation failure.";
    private static final String LOG_INVALID_EXPERT_ID =
            "Expert administration form encountered invalid expert id: %s.";

    private static final String FAIL_NO_ID_FIELD =
            "One or more experts contain no 'id' field.";
    private static final String FAIL_NO_VISIBILITY_APPROVED_FIELD =
            "One or more experts contain no 'visibilityApproved' field.";
    private static final String FAIL_NO_WEIGHTING_FIELD =
            "One or more experts contain no 'weighting' field.";
    private static final String FAIL_INVALID_WEIGHTING_FIELD =
            "One or more experts contain an invalid (NaN or Inf) 'weighting' field.";
    private static final String FAIL_NO_ADMINISTRATOR_FIELD =
            "One or more experts contain no 'administrator' field.";
    private static final String FAIL_NO_SEEG_FIELD =
            "One or more experts contain no 'seegmember' field.";

    private ExpertService expertService;
    private GeoJsonObjectMapper json;
    private AdminExpertsHelper helper;

    @Autowired
    public AdminExpertsController(ExpertService expertService, GeoJsonObjectMapper geoJsonObjectMapper,
                                  AdminExpertsHelper adminExpertsHelper) {
        this.expertService = expertService;
        this.json = geoJsonObjectMapper;
        this.helper = adminExpertsHelper;
    }

    /**
     * Returns the initial view to display.
     * @param model The model.
     * @return The ftl page name.
     * @throws com.fasterxml.jackson.core.JsonProcessingException if there is an error during JSON serialization
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admin/experts", method = RequestMethod.GET)
    public String showPage(Model model) throws JsonProcessingException {
        List<Expert> allExperts = expertService.getAllExperts();
        List<JsonExpertFull> allExpertsDto = convert(allExperts, new Converter<Expert, JsonExpertFull>() {
            @Override
            public JsonExpertFull convert(Expert expert) {
                return new JsonExpertFull(expert);
            }
        });

        model.addAttribute("experts", json.writeValueAsString(allExpertsDto));
        return "admin/experts";
    }

    /**
     * Receives the user input from the expert administration page and responds accordingly.
     * @param experts The user input from the expert administration page.
     * @return A failure status with an array of response messages or a success status.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/admin/experts", method = RequestMethod.POST,
         consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Collection<String>> submitPage(@RequestBody Collection<JsonExpertFull> experts) {
        Collection<String> allMessages = flatten(convert(experts,
            new Converter<JsonExpertFull, Collection<String>>() {
                @Override
                public Collection<String> convert(JsonExpertFull expert) {
                    return validateExpert(expert);
                }
            })
        );

        if (!allMessages.isEmpty()) {
            LOGGER.warn(String.format(LOG_VALIDATION_FAILURE, allMessages.size()));
            return new ResponseEntity<>(selectDistinct(allMessages), HttpStatus.BAD_REQUEST);
        } else {
            try {
                helper.processExpertsAsTransaction(experts);
            } catch (ValidationException e) {
                LOGGER.warn(String.format(LOG_INVALID_EXPERT_ID, e.getValidationMessages()));
                return new ResponseEntity<>(e.getValidationMessages(), HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    private Collection<String> validateExpert(JsonExpertFull expert) {
        Collection<String> expertMessages = new ArrayList<>();

        if (expert.getId() == null) {
            expertMessages.add(FAIL_NO_ID_FIELD);
        }

        if (expert.getVisibilityApproved() == null) {
            expertMessages.add(FAIL_NO_VISIBILITY_APPROVED_FIELD);
        }

        if (expert.getWeighting() == null) {
            expertMessages.add(FAIL_NO_WEIGHTING_FIELD);
        } else if (Double.isNaN(expert.getWeighting()) || Double.isInfinite(expert.getWeighting())) {
            expertMessages.add(FAIL_INVALID_WEIGHTING_FIELD);
        }

        if (expert.isAdministrator() == null) {
            expertMessages.add(FAIL_NO_ADMINISTRATOR_FIELD);
        }

        if (expert.isSEEGMember() == null) {
            expertMessages.add(FAIL_NO_SEEG_FIELD);
        }

        return expertMessages;
    }

}
