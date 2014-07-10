package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
@Controller
@SessionAttributes(RegistrationController.EXPERT_SESSION_STATE_KEY)
public class RegistrationController {
    public final static String EXPERT_SESSION_STATE_KEY = "expert";
    public static final List<String> ERROR_LOGGED_IN_USERS_CANNOT_CREATE_NEW_ACCOUNTS = Arrays.asList("Logged in users cannot create new accounts");
    public static final List<String> ERROR_INVALID_REGISTRATION_SESSION = Arrays.asList("Invalid registration session");

    // Regex from knockout.validation codebase https://github.com/Knockout-Contrib/Knockout-Validation/blob/4a0f89e6abf468e9ee9dc0d31d7303a40480a807/Src/rules.js#L183
    private static final Pattern EMAIL_REGEX = Pattern.compile("^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$", Pattern.CASE_INSENSITIVE); ///CHECKSTYLE:SUPPRESS LineLengthCheck

    private final CurrentUserService currentUserService;
    private final ExpertService expertService;
    private final PasswordEncoder passwordEncoder;
    private final ReCaptcha reCaptchaService;

    @Autowired
    public RegistrationController(CurrentUserService currentUserService, ExpertService expertService, PasswordEncoder passwordEncoder, ReCaptcha reCaptchaService) {
        this.currentUserService = currentUserService;
        this.expertService = expertService;
        this.passwordEncoder = passwordEncoder;
        this.reCaptchaService = reCaptchaService;
    }

    @RequestMapping(value = "/register/account", method = RequestMethod.GET)
    public String getAccountPage(final ModelMap modelMap, final SessionStatus status) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            // Logged in user, stop registration session, redirect to home page
            status.setComplete();
            return "redirect:/";
        }

        List<String> validationFailures = new ArrayList<>();
        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            // Create an empty expert in the session state
            modelMap.addAttribute(EXPERT_SESSION_STATE_KEY, new Expert());
            modelMap.addAttribute("initialAlerts", "[]");
        } else {
            Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);
            if (expert.getEmail() != null || expert.getPassword() != null) {
                validationFailures = validateExpertBasic(expert);
            }
        }

        modelMap.addAttribute("initialAlerts", buildValidationString(validationFailures));

        String html = reCaptchaService.createRecaptchaHtml(null, "clean", null);
        modelMap.addAttribute("captcha", html);

        // Return registration form
        return "register/account";
    }

    @RequestMapping(value = "/register/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> submitAccountPage(final ServletRequest request, final ModelMap modelMap, final SessionStatus status, final String email, final String password, final String passwordConfirmation, final String captchaChallenge, final String captchaResponse) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            status.setComplete();
            return new ResponseEntity<>(ERROR_LOGGED_IN_USERS_CANNOT_CREATE_NEW_ACCOUNTS, HttpStatus.FORBIDDEN);
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            status.setComplete();
            return new ResponseEntity<>(ERROR_INVALID_REGISTRATION_SESSION, HttpStatus.FORBIDDEN);
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        // Update expert
        expert.setEmail(email);
        expert.setPassword(password);

        // Validate
        List<String> validationFailures = validateExpertBasicWithValidityChecks(expert, passwordConfirmation, request, captchaChallenge, captchaResponse);
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

        if (validateExpertBasic(expert).size() > 0) {
            // Make sure that the data on the first first page was valid
            return "redirect:/register/account";
        }

        // Note: will also need to deliver a bootstrapped list of known validator diseases groups

        return "register/details";
    }

    @RequestMapping(value = "/register/details", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> submitDetailsPage(final ModelMap modelMap, final SessionStatus status, final @RequestBody JsonExpertDetails expertDetails) throws JsonProcessingException {
        if (checkIfUserLoggedIn()) {
            status.setComplete();
            return new ResponseEntity<>(ERROR_LOGGED_IN_USERS_CANNOT_CREATE_NEW_ACCOUNTS, HttpStatus.FORBIDDEN);
        }

        if (!modelMap.containsAttribute(EXPERT_SESSION_STATE_KEY)) {
            status.setComplete();
            return new ResponseEntity<>(ERROR_INVALID_REGISTRATION_SESSION, HttpStatus.FORBIDDEN);
        }

        Expert expert = (Expert) modelMap.get(EXPERT_SESSION_STATE_KEY);

        // Update expert
        expert.setName(expertDetails.getName());
        expert.setPubliclyVisible(expertDetails.isPubliclyVisible());
        expert.setValidatorDiseaseGroups(null);

        // Validate
        List<String> validationFailures = validateExpertDetails(expert);
        if (validationFailures.size() > 0) {
            return new ResponseEntity<>(validationFailures, HttpStatus.BAD_REQUEST);
        }

        validationFailures = validateExpertBasic(expert);
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

    private boolean checkIfUserLoggedIn() {
        return currentUserService.getCurrentUser() != null;
    }

    private List<String> validateExpertBasicWithValidityChecks(Expert expert, String passwordConfirmation, ServletRequest request, String captchaChallenge, String captchaResponse)  {
        List<String> validationFailures = validateExpertBasic(expert);

        if (!expert.getPassword().equals(passwordConfirmation)) {
            validationFailures.add("Passwords must match");
        }

        validationFailures.addAll(validateCaptcha(request, captchaChallenge, captchaResponse));

        return  validationFailures;

    }

    private List<String> validateExpertBasic(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // Check email
        if (StringUtils.isEmpty(expert.getEmail()) || !EMAIL_REGEX.matcher(expert.getEmail()).matches()) {
            validationFailures.add("Email address not valid.");
        }

        // check password (complexity?) (note, when TGHN will have to check vs key)


        return validationFailures;
    }

    private List<String> validateCaptcha(ServletRequest request, String challenge, String response) {
        List<String> validationFailures = new ArrayList<>();

        ReCaptchaResponse result = reCaptchaService.checkAnswer(
                request.getRemoteAddr(),
                challenge,
                response
        );

        if (!result.isValid()) {
            validationFailures.add("Incorrect captcha.");
        }

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
