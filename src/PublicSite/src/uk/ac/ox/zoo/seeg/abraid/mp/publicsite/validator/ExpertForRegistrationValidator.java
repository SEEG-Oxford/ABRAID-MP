package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonExpertBasic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates the fields associated with an Expert during registration.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertForRegistrationValidator {
    // Regex from knockout.validation codebase https://github.com/Knockout-Contrib/Knockout-Validation/blob/4a0f89e6abf468e9ee9dc0d31d7303a40480a807/Src/rules.js#L183
    private static final Pattern EMAIL_REGEX = Pattern.compile("^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$", Pattern.CASE_INSENSITIVE); ///CHECKSTYLE:SUPPRESS LineLengthCheck
    // Regex from http://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=^[^\\s]{6,128}$)((?=.*?\\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[^\\w\\d\\s])(?=.*?[a-z])|(?=.*?[^\\w\\d\\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[A-Z])(?=.*?[^\\w\\d\\s]))^.*$"); ///CHECKSTYLE:SUPPRESS LineLengthCheck

    private final ReCaptcha reCaptchaService;
    private final ExpertService expertService;

    @Autowired
    public ExpertForRegistrationValidator(ReCaptcha reCaptchaService, ExpertService expertService) {
        this.reCaptchaService = reCaptchaService;
        this.expertService = expertService;
    }

    public String createValidationCaptcha() {
        return reCaptchaService.createRecaptchaHtml(null, "clean", null);
    }

    public List<String> validateBasicFields(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // Check email
        checkEmail(expert, validationFailures);

        // Check password
        checkPassword(expert, validationFailures);

        return validationFailures;
    }

    public List<String> validateDetailsFields(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // name
        checkName(expert, validationFailures);

        // job
        checkJobTitle(expert, validationFailures);

        // institution
        checkInstitution(expert, validationFailures);

        return validationFailures;
    }

    public List<String> validateTransientFields(JsonExpertBasic expertBasic, ServletRequest request)  {
        List<String> validationFailures = new ArrayList<>();

        checkPasswordConfirmation(expertBasic, validationFailures);

        checkCaptcha(expertBasic.getCaptchaChallenge(), expertBasic.getCaptchaResponse(), request, validationFailures);

        return  validationFailures;
    }

    private void checkEmail(Expert expert, List<String> validationFailures) {
        validateString("Email address", expert.getEmail(), 320, validationFailures);

        if (!EMAIL_REGEX.matcher(expert.getEmail()).matches()) {
            validationFailures.add("Email address not valid.");
        }

        if (expertService.getExpertByEmail(expert.getEmail()) != null) {
            validationFailures.add("An account already exists for this email address.");
        }
    }

    private void checkPassword(Expert expert, List<String> validationFailures) {
        if (StringUtils.isEmpty(expert.getPassword())) {
            validationFailures.add("Password must be provided.");
        }

        if (!PASSWORD_REGEX.matcher(expert.getPassword()).matches()) {
            validationFailures.add("Password not sufficiently complex.");
        }
    }

    private void checkInstitution(Expert expert, List<String> validationFailures) {
        validateString("Institution", expert.getInstitution(), 100, validationFailures);
    }

    private void checkJobTitle(Expert expert, List<String> validationFailures) {
        validateString("Job title", expert.getJobTitle(), 100, validationFailures);
    }

    private void checkName(Expert expert, List<String> validationFailures) {
        validateString("Name", expert.getName(), 1000, validationFailures);
    }

    private void checkPasswordConfirmation(JsonExpertBasic expertBasic, List<String> validationFailures) {
        if (!expertBasic.getPassword().equals(expertBasic.getPasswordConfirmation())) {
            validationFailures.add("Passwords must match.");
        }
    }

    private void checkCaptcha(
            String challenge, String response, ServletRequest request, List<String> validationFailures) {
        ReCaptchaResponse result = reCaptchaService.checkAnswer(
                request.getRemoteAddr(),
                challenge,
                response
        );

        if (!result.isValid()) {
            validationFailures.add("Incorrect captcha.");
        }
    }

    private static void validateString(String name, String value, int maxLength, List<String> validationFailures) {
        if (StringUtils.isEmpty(value)) {
            validationFailures.add(name + " must be provided.");
        }

        if (value != null && value.length() > maxLength) {
            validationFailures.add(name + "must less than " + maxLength + " letters in length.");
        }
    }
}
