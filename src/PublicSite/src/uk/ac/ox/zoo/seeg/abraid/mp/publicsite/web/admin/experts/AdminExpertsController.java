package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.experts;

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
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

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

    private final ExpertService expertService;
    private final AbraidJsonObjectMapper json;
    private final AdminExpertsControllerValidator validator;
    private final AdminExpertsControllerHelper helper;

    @Autowired
    public AdminExpertsController(ExpertService expertService,
                                  AbraidJsonObjectMapper objectMapper,
                                  AdminExpertsControllerValidator adminExpertsControllerValidator,
                                  AdminExpertsControllerHelper adminExpertsHelper) {
        this.expertService = expertService;
        this.json = objectMapper;
        this.validator = adminExpertsControllerValidator;
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
                    return validator.validate(expert);
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


}
