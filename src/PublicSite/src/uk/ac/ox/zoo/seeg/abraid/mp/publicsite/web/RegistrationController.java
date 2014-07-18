package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonExpertBasic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertForRegistrationValidator;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.collection.IsIn.isIn;

/**
 * Controller for the expert registration process.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
@SessionAttributes(RegistrationController.EXPERT_SESSION_STATE_KEY)
public class RegistrationController {
    /** Session key for Expert object */
    public final static String EXPERT_SESSION_STATE_KEY = "expert";

    private static final String ALERTS_ATTRIBUTE_KEY = "alerts";
    private static final String CAPTCHA_ATTRIBUTE_KEY = "captcha";
    private static final String DISEASES_ATTRIBUTE_KEY = "diseases";
    private static final String JSON_EXPERT_ATTRIBUTE_KEY = "jsonExpert";

    private static final String ERROR_LOGGED_IN_USERS_CANNOT_CREATE_NEW_ACCOUNTS = "Logged in users cannot create new accounts";
    private static final String ERROR_INVALID_REGISTRATION_SESSION = "Invalid registration session";

    private final CurrentUserService currentUserService;
    private final ExpertService expertService;
    private final DiseaseService diseaseService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper json;
    private final ExpertForRegistrationValidator validator;

    @Autowired
    public RegistrationController(CurrentUserService currentUserService, ExpertService expertService,
                                  DiseaseService diseaseService, PasswordEncoder passwordEncoder,
                                  ObjectMapper geoJsonObjectMapper,
                                  ExpertForRegistrationValidator expertRegistrationValidator) {
        this.currentUserService = currentUserService;
        this.expertService = expertService;
        this.diseaseService = diseaseService;
        this.passwordEncoder = passwordEncoder;
        this.json = geoJsonObjectMapper;
        this.validator = expertRegistrationValidator;
    }

    @RequestMapping(value = "/register/account", method = RequestMethod.GET)
    public String getAccountPage(ModelMap modelMap, SessionStatus status) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            // Logged in user, stop registration session, redirect to home page
            status.setComplete();
            return "redirect:/";
        }

        List<String> validationFailures = new ArrayList<>();
        Expert expert;
        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            // Create an empty expert in the session state
            expert = new Expert();

            // SET EXPERT VISIBILITY FIELD
            // This is a temp workaround for a more generic overhaul of this system in an upcoming sprint.
            expert.setPubliclyVisible(false);

            modelMap.addAttribute(EXPERT_SESSION_STATE_KEY, expert);
        } else {
            expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);
            if (expert.getEmail() != null || expert.getPassword() != null) {
                validationFailures = validator.validateBasicFields(expert);
            }
        }

        modelMap.addAttribute(ALERTS_ATTRIBUTE_KEY, json.writeValueAsString(validationFailures));
        modelMap.addAttribute(CAPTCHA_ATTRIBUTE_KEY, validator.createValidationCaptcha());
        modelMap.addAttribute(JSON_EXPERT_ATTRIBUTE_KEY, json.writeValueAsString(new JsonExpertBasic(expert)));

        // Return registration form
        return "register/account";
    }


    @RequestMapping(value = "/register/account", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> submitAccountPage(
            ModelMap modelMap, SessionStatus status, @RequestBody JsonExpertBasic expertBasic, ServletRequest request)
            throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            status.setComplete();
            return new ResponseEntity<>(
                    Arrays.asList(ERROR_LOGGED_IN_USERS_CANNOT_CREATE_NEW_ACCOUNTS), HttpStatus.FORBIDDEN);
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            status.setComplete();
            return new ResponseEntity<>(
                    Arrays.asList(ERROR_INVALID_REGISTRATION_SESSION), HttpStatus.FORBIDDEN);
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        // Update expert
        updateExpert(expert, expertBasic);

        // Validate
        List<String> validationFailures = validator.validateBasicFields(expert);
        validationFailures.addAll(validator.validateTransientFields(expertBasic, request));

        if (validationFailures.size() > 0) {
            expert.setEmail(null);
            expert.setPassword(null);
            return new ResponseEntity<>(validationFailures, HttpStatus.BAD_REQUEST);
        }

        // Return registration form
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/register/details", method = RequestMethod.GET)
    public String getDetailsPage(final ModelMap modelMap, final SessionStatus status) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            // Logged in user, stop registration session, redirect to home page
            status.setComplete();
            return "redirect:/";
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            // Make sure page one has already been filled in
            return "redirect:/register/account";
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        if (validator.validateBasicFields(expert).size() > 0) {
            // Make sure that the data on the first first page was valid
            return "redirect:/register/account";
        }

        List<ValidatorDiseaseGroup> allValidatorDiseaseGroups = loadValidatorDiseaseGroups();
        modelMap.addAttribute(DISEASES_ATTRIBUTE_KEY, json.writeValueAsString(allValidatorDiseaseGroups));

        modelMap.addAttribute(JSON_EXPERT_ATTRIBUTE_KEY, json.writeValueAsString(new JsonExpertDetails(expert)));

        return "register/details";
    }

    @RequestMapping(value = "/register/details", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> submitDetailsPage(
            final ModelMap modelMap, final SessionStatus status, final @RequestBody JsonExpertDetails expertDetails)
            throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            status.setComplete();
            return new ResponseEntity<>(
                    Arrays.asList(ERROR_LOGGED_IN_USERS_CANNOT_CREATE_NEW_ACCOUNTS), HttpStatus.FORBIDDEN);
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            status.setComplete();
            return new ResponseEntity<>(
                    Arrays.asList(ERROR_INVALID_REGISTRATION_SESSION), HttpStatus.FORBIDDEN);
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        // Update expert
        updateExpert(expert, expertDetails);

        // Validate
        List<String> validationFailures = validator.validateDetailsFields(expert);
        if (validationFailures.size() > 0) {
            return new ResponseEntity<>(validationFailures, HttpStatus.BAD_REQUEST);
        }

        validationFailures = validator.validateBasicFields(expert);
        if (validationFailures.size() > 0) {
            // Email must have been snipped, so send back to page 1
            return new ResponseEntity<>(validationFailures, HttpStatus.CONFLICT);
        }

        // Hash password
        expert.setPassword(passwordEncoder.encode(expert.getPassword()));

        // Save to db
        expertService.saveExpert(expert);

        // Return successfully
        status.setComplete();
        return new ResponseEntity<>(HttpStatus.CREATED); // Could add success page
    }

    @RequestMapping(value = "/register/cancel")
    public String processCancel(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final SessionStatus status) {
        status.setComplete();
        return "redirect:/";
    }

    private List<ValidatorDiseaseGroup> loadValidatorDiseaseGroups() {
        List<ValidatorDiseaseGroup> allValidatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        convert(allValidatorDiseaseGroups, new Converter<ValidatorDiseaseGroup, JsonValidatorDiseaseGroup>() {
            @Override
            public JsonValidatorDiseaseGroup convert(ValidatorDiseaseGroup validatorDiseaseGroup) {
                return new JsonValidatorDiseaseGroup(validatorDiseaseGroup);
            }
        });
        return allValidatorDiseaseGroups;
    }

    private boolean checkIfUserLoggedIn() {
        return currentUserService.getCurrentUser() != null;
    }

    private static void updateExpert(Expert expert, JsonExpertBasic expertBasic) {
        expert.setEmail(expertBasic.getEmail());
        expert.setPassword(expertBasic.getPassword());
    }

    private void updateExpert(Expert expert, JsonExpertDetails expertDetails) {
        expert.setName(expertDetails.getName());
        expert.setPubliclyVisible(expertDetails.isPubliclyVisible());
        expert.setJobTitle(expertDetails.getJobTitle());
        expert.setInstitution(expertDetails.getInstitution());

        List<ValidatorDiseaseGroup> allValidatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        List<ValidatorDiseaseGroup> interests = filter(
                having(on(ValidatorDiseaseGroup.class).getId(), isIn(expertDetails.getDiseaseInterests())),
                allValidatorDiseaseGroups);

        expert.setValidatorDiseaseGroups(interests);
    }
}
