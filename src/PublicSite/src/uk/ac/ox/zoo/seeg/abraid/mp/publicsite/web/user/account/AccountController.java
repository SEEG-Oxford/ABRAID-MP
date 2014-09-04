package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Controller for expert profile editing.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AccountController extends AbstractController {
    private static final Logger LOGGER = Logger.getLogger(AccountController.class);
    private static final String LOG_USER_UPDATED = "User updated (%s)";
    private static final String LOG_PASSWORD_CHANGED = "Password updated (%s)";

    private static final String DISEASES_ATTRIBUTE_KEY = "diseases";
    private static final String JSON_EXPERT_ATTRIBUTE_KEY = "jsonExpert";

    private final CurrentUserService currentUserService;
    private final ExpertService expertService;
    private final DiseaseService diseaseService;
    private final ObjectMapper json;
    private final AccountControllerValidator validator;
    private final AccountControllerHelper helper;

    @Autowired
    public AccountController(CurrentUserService currentUserService,
                             ExpertService expertService,
                             DiseaseService diseaseService,
                             ObjectMapper geoJsonObjectMapper,
                             AccountControllerValidator adminControllerValidator,
                             AccountControllerHelper accountControllerTransactionHelper) {
        this.currentUserService = currentUserService;
        this.expertService = expertService;
        this.diseaseService = diseaseService;
        this.json = geoJsonObjectMapper;
        this.validator = adminControllerValidator;
        this.helper = accountControllerTransactionHelper;
    }

    /**
     * Loads the account editing page.
     * @param modelMap The templating model.
     * @return The template to render.
     * @throws JsonProcessingException Thrown if issue generating json for bootstrapped variables.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(value = "/account/edit", method = RequestMethod.GET)
    public String getAccountEditPage(ModelMap modelMap) throws JsonProcessingException {
        JsonExpertDetails expert = loadExpertDTO();
        List<JsonValidatorDiseaseGroup> allValidatorDiseaseGroups = loadValidatorDiseaseGroups();

        modelMap.addAttribute(DISEASES_ATTRIBUTE_KEY, json.writeValueAsString(allValidatorDiseaseGroups));
        modelMap.addAttribute(JSON_EXPERT_ATTRIBUTE_KEY, json.writeValueAsString(expert));

        return "account/edit";
    }

    /**
     * Receives the user input from the account editing page and responds accordingly.
     * @param expert The user input from the second account registration page.
     * @return A failure status with an array of response messages or a success status.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(value = "/account/edit", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> submitAccountEditPage(@RequestBody JsonExpertDetails expert) {
        int expertId = currentUserService.getCurrentUser().getId();

        // Validate dto
        Collection<String> validationFailures = validator.validate(expert);
        if (!validationFailures.isEmpty()) {
            return new ResponseEntity<>(validationFailures, HttpStatus.BAD_REQUEST);
        }

        // Update & save expert
        try {
            helper.processExpertProfileUpdateAsTransaction(expertId, expert);
            LOGGER.info(String.format(LOG_USER_UPDATED, expertId));
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getValidationMessages(), HttpStatus.BAD_REQUEST);
        }

        // Return successfully
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Could add success page
    }

    /**
     * Loads the password change page.
     * @return the template for the password page.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(value = "/account/password", method = RequestMethod.GET)
    public String getChangePasswordPage() {
        return "account/password";
    }

    /**
     * Receives the user input from the password change page and responds accordingly.
     * @param oldPassword The user input for their existing password.
     * @param newPassword The user input for their new password.
     * @param confirmPassword The user input for their new password (confirmation).
     * @return A failure status with an array of response messages or a success status.
     */
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @RequestMapping(value = "/account/password",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> submitChangePasswordPage(
            String oldPassword, String newPassword, String confirmPassword) {
        int expertId = currentUserService.getCurrentUser().getId();

        // Validate inputs
        Collection<String> validationFailures =
                validator.validatePasswordChange(oldPassword, newPassword, confirmPassword, expertId);

        if (!validationFailures.isEmpty()) {
            return new ResponseEntity<>(validationFailures, HttpStatus.BAD_REQUEST);
        }

        // Update & save expert
        try {
            helper.processExpertPasswordChangeAsTransaction(expertId, newPassword);
            LOGGER.info(String.format(LOG_PASSWORD_CHANGED, expertId));
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getValidationMessages(), HttpStatus.BAD_REQUEST);
        }

        // Return successfully
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Could add success page
    }

    private JsonExpertDetails loadExpertDTO() {
        return new JsonExpertDetails(loadExpert());
    }

    private Expert loadExpert() {
        return expertService.getExpertById(currentUserService.getCurrentUser().getId());
    }

    private List<JsonValidatorDiseaseGroup> loadValidatorDiseaseGroups() {
        List<ValidatorDiseaseGroup> allValidatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
        return convert(allValidatorDiseaseGroups, new Converter<ValidatorDiseaseGroup, JsonValidatorDiseaseGroup>() {
            @Override
            public JsonValidatorDiseaseGroup convert(ValidatorDiseaseGroup validatorDiseaseGroup) {
                return new JsonValidatorDiseaseGroup(validatorDiseaseGroup);
            }
        });
    }
}
