package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
@Controller
@SessionAttributes(RegistrationController.EXPERT_SESSION_STATE_KEY)
public class RegistrationController {
    public final static String EXPERT_SESSION_STATE_KEY = "expert";

    private final String EMAIL_REGEX = "/^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$/i";

    private final CurrentUserService currentUserService;
    private final ExpertService expertService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(CurrentUserService currentUserService, ExpertService expertService, PasswordEncoder passwordEncoder) {
        this.currentUserService = currentUserService;
        this.expertService = expertService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/register/account", method = RequestMethod.GET)
    public String getAccountPage(final ModelMap modelMap, final SessionStatus status) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            // Logged in user, stop registration session, redirect to home page
            status.setComplete();
            return "redirect:/";
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            // Create an empty expert in the session state
            modelMap.addAttribute(EXPERT_SESSION_STATE_KEY, new Expert());
            modelMap.addAttribute("initialAlerts", "[]");
        } else {
            Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);
            List<String> validationFailures = validateExpertBasic(expert);
            modelMap.addAttribute("initialAlerts", buildValidationString(validationFailures));
        }

        // Return registration form
        return "register/account";
    }

    @RequestMapping(value = "/register/account", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> submitAccountPage(final ModelMap modelMap, final SessionStatus status, final String email, final String password, final String passwordConfirmation) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            status.setComplete();
            return new ResponseEntity<>("Logged in users cannot create new accounts", HttpStatus.FORBIDDEN);
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            status.setComplete();
            return new ResponseEntity<>("Invalid registration session", HttpStatus.FORBIDDEN);
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        // Update expert
        expert.setEmail(email);
        expert.setPassword(password);

        // Validate
        List<String> validationFailures = validateExpertBasicWithValidityChecks(expert, password, passwordConfirmation);
        if (validationFailures.size() > 0) {
            expert.setEmail(null);
            expert.setPassword(null);
            return new ResponseEntity<String>(buildValidationString(validationFailures), HttpStatus.BAD_REQUEST);
        }

        // Return registration form
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
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

        if (validateExpertBasic(expert) != null) {
            // Make sure that the data on the first first page was valid
            return "redirect:/register/account";
        }

        // Note: will also need to deliver a bootstrapped list of known validator diseases groups

        return "register/details";
    }

    @RequestMapping(value = "/register/details", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> submitDetailsPage(final ModelMap modelMap, final SessionStatus status, final @RequestBody JsonExpertDetails expertDetails) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            status.setComplete();
            return new ResponseEntity<>("Logged in users cannot create new accounts", HttpStatus.FORBIDDEN);
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            status.setComplete();
            return new ResponseEntity<>("Invalid registration session", HttpStatus.FORBIDDEN);
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        // Update expert
        expert.setName(expertDetails.getName());
        expert.setPubliclyVisible(expertDetails.isPubliclyVisible());
        expert.setValidatorDiseaseGroups(null);

        // Validate
        List<String> validationFailures = validateExpertDetails(expert);
        if (validationFailures.size() > 0) {
            return new ResponseEntity<>(buildValidationString(validationFailures), HttpStatus.BAD_REQUEST);
        }

        validationFailures = validateExpertBasic(expert);
        if (validationFailures.size() > 0) {
            // Email must have been snipped, so send back to page 1
            return new ResponseEntity<>(buildValidationString(validationFailures), HttpStatus.CONFLICT);
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

    private boolean checkIfUserLoggedIn() {
        return currentUserService.getCurrentUser() != null;
    }

    private List<String> validateExpertBasicWithValidityChecks(Expert expert, String passwordConfirmation, String captcha)  {
        List<String> validationFailures = validateExpertBasic(expert);

        if (!expert.getPassword().equals(passwordConfirmation)) {
            validationFailures.add("Passwords must match");
        }

        validationFailures.addAll(validateCaptcha(captcha));

        return  validationFailures;

    }

    private List<String> validateExpertBasic(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // Check email
        if (StringUtils.isEmpty(expert.getEmail()) || !expert.getEmail().matches(EMAIL_REGEX)) {
            validationFailures.add("Email address not valid");
        }

        // check password (complexity?) (note, when TGHN will have to check vs key)


        return validationFailures;
    }

    private List<String> validateCaptcha(String captcha) {
        List<String> validationFailures = new ArrayList<>();

        return validationFailures;
    }

    private List<String> validateExpertDetails(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // Check name (empty)
        // Check diseases (exist)

        // job
        // institution

        return validationFailures;
    }

    private String buildValidationString(List<String> validationFailures) throws JsonProcessingException {
        if (validationFailures.size() > 0) {
            ObjectMapper jsonMapper = new ObjectMapper();
            return jsonMapper.writeValueAsString(validationFailures);
        }

        return "[]";
    }
}
