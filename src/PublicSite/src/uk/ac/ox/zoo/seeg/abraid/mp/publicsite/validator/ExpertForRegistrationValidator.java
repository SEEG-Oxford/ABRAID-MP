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
    // Regex from knockout.validation codebase https://github.com/Knockout-Contrib/Knockout-Validation/blob/4a0f89e6abf468e9ee9dc0d31d7303a40480a807/Src/rules.js#L183 ///CHECKSTYLE:SUPPRESS LineLengthCheck
    private static final Pattern EMAIL_REGEX = Pattern.compile("^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$", Pattern.CASE_INSENSITIVE); ///CHECKSTYLE:SUPPRESS LineLengthCheck
    // Regex from http://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity ///CHECKSTYLE:SUPPRESS LineLengthCheck
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=^[^\\s]{6,128}$)((?=.*?\\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[^\\w\\d\\s])(?=.*?[a-z])|(?=.*?[^\\w\\d\\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[A-Z])(?=.*?[^\\w\\d\\s]))^.*$"); ///CHECKSTYLE:SUPPRESS LineLengthCheck

    private static final int MAX_NAME_LENGTH = 1000;
    private static final int MAX_JOB_TITLE_LENGTH = 100;
    private static final int MAX_INSTITUTION_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 320;

    private static final String RECAPTCHA_THEME = "clean";

    private static final String NAME_FIELD_NAME = "Name";
    private static final String EMAIL_ADDRESS_FIELD_NAME = "Email address";
    private static final String INSTITUTION_FIELD_NAME = "Institution";
    private static final String JOB_TITLE_FIELD_NAME = "Job title";
    private static final String CAPTCHA_FIELD_NAME = "Captcha";
    private static final String PASSWORD_FIELD_NAME = "Password";

    private static final String FAILURE_STRING_LENGTH = "%s must less than %s letters in length.";
    private static final String FAILURE_STRING_MISSING = "%s must be provided.";
    private static final String FAILURE_INCORRECT_VALUE = "%s incorrect.";
    private static final String FAILURE_MUST_MATCH = "%s pair must match.";
    private static final String FAILURE_INSUFFICIENT_COMPLEXITY = "%s not sufficiently complex.";
    private static final String FAILURE_INVALID_VALUE = "%s not valid.";
    private static final String FAILURE_ALREADY_EXISTS = "%s already has an associated account.";

    private final ReCaptcha reCaptchaService;
    private final ExpertService expertService;

    @Autowired
    public ExpertForRegistrationValidator(ReCaptcha reCaptchaService, ExpertService expertService) {
        this.reCaptchaService = reCaptchaService;
        this.expertService = expertService;
    }

    /**
     * Generates the HTML for a validation captcha.
     * @return the HTML for a validation captcha.
     */
    public String createValidationCaptcha() {
        return reCaptchaService.createRecaptchaHtml(null, RECAPTCHA_THEME, null);
    }

    /**
     * Validates the parts of Expert provided on the account registration page.
     * @param expert Expert to validate.
     * @return Any failure messages.
     */
    public List<String> validateBasicFields(Expert expert) {
        List<String> validationFailures = new ArrayList<>();

        // Check email
        checkEmail(expert, validationFailures);

        // Check password
        checkPassword(expert, validationFailures);

        return validationFailures;
    }

    /**
     * Validates the parts of Expert provided on the account registration details page.
     * @param expert Expert to validate.
     * @return Any failure messages.
     */
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

    /**
     * Validates the transient fields provided on the account registration page that are not part of the Expert entity.
     * @param expertBasic The expert dto to validate.
     * @param request The HTTP request that to be validated against.
     * @return Any failure messages.
     */
    public List<String> validateTransientFields(JsonExpertBasic expertBasic, ServletRequest request)  {
        List<String> validationFailures = new ArrayList<>();

        checkPasswordConfirmation(expertBasic, validationFailures);

        checkCaptcha(expertBasic.getCaptchaChallenge(), expertBasic.getCaptchaResponse(), request, validationFailures);

        return  validationFailures;
    }

    private void checkEmail(Expert expert, List<String> validationFailures) {
        validateString(EMAIL_ADDRESS_FIELD_NAME, expert.getEmail(), MAX_EMAIL_LENGTH, validationFailures);

        if (!EMAIL_REGEX.matcher(expert.getEmail()).matches()) {
            validationFailures.add(String.format(FAILURE_INVALID_VALUE, EMAIL_ADDRESS_FIELD_NAME));
        }

        if (expertService.getExpertByEmail(expert.getEmail()) != null) {
            validationFailures.add(String.format(FAILURE_ALREADY_EXISTS, EMAIL_ADDRESS_FIELD_NAME));
        }
    }

    private void checkPassword(Expert expert, List<String> validationFailures) {
        if (StringUtils.isEmpty(expert.getPassword())) {
            validationFailures.add(String.format(FAILURE_STRING_MISSING, PASSWORD_FIELD_NAME));
        }

        if (!PASSWORD_REGEX.matcher(expert.getPassword()).matches()) {
            validationFailures.add(String.format(FAILURE_INSUFFICIENT_COMPLEXITY, PASSWORD_FIELD_NAME));
        }
    }

    private void checkInstitution(Expert expert, List<String> validationFailures) {
        validateString(INSTITUTION_FIELD_NAME, expert.getInstitution(), MAX_INSTITUTION_LENGTH, validationFailures);
    }

    private void checkJobTitle(Expert expert, List<String> validationFailures) {
        validateString(JOB_TITLE_FIELD_NAME, expert.getJobTitle(), MAX_JOB_TITLE_LENGTH, validationFailures);
    }

    private void checkName(Expert expert, List<String> validationFailures) {
        validateString(NAME_FIELD_NAME, expert.getName(), MAX_NAME_LENGTH, validationFailures);
    }

    private void checkPasswordConfirmation(JsonExpertBasic expertBasic, List<String> validationFailures) {
        if (!expertBasic.getPassword().equals(expertBasic.getPasswordConfirmation())) {
            validationFailures.add(String.format(FAILURE_MUST_MATCH, PASSWORD_FIELD_NAME));
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
            validationFailures.add(String.format(FAILURE_INCORRECT_VALUE, CAPTCHA_FIELD_NAME));
        }
    }

    private static void validateString(String name, String value, int maxLength, List<String> validationFailures) {
        if (StringUtils.isEmpty(value)) {
            validationFailures.add(String.format(FAILURE_STRING_MISSING, name));
        }

        if (value != null && value.length() > maxLength) {
            validationFailures.add(String.format(FAILURE_STRING_LENGTH, name, maxLength));
        }
    }
}
