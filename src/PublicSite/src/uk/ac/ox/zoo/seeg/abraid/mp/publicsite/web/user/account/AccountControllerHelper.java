package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.PasswordResetRequest;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.collection.IsIn.isIn;

/**
 * Helper for the AccountController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
public class AccountControllerHelper {
    private static final String FAIL_NO_EXPERT_MATCH = "No matching expert found to update (%s).";
    private static final String FAIL_NO_REQUEST_MATCH = "No matching password reset request found (%s).";
    private static final String EXPERT_VISIBILITY_EMAIL_DATA_KEY = "expert";
    private static final String VISIBILITY_EMAIL_SUBJECT = "Updated user requiring visibility sign off";
    private static final String VISIBILITY_EMAIL_TEMPLATE = "account/updatedUserEmail.ftl";
    private static final String PASSWORD_RESET_EMAIL_SUBJECT = "ABRAID-MP Password Reset";
    private static final String PASSWORD_RESET_EMAIL_TEMPLATE = "account/passwordResetEmail.ftl";
    private static final String ID_RESET_EMAIL_DATA_KEY = "id";
    private static final String KEY_RESET_EMAIL_DATA_KEY = "key";
    private static final String URL_RESET_EMAIL_DATA_KEY = "url";
    private final ExpertService expertService;
    private final DiseaseService diseaseService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountControllerHelper(ExpertService expertService, DiseaseService diseaseService,
                                   EmailService emailService, PasswordEncoder passwordEncoder) {
        this.expertService = expertService;
        this.diseaseService = diseaseService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Updates the database entry for an expert.
     * @param id The expert to update.
     * @param expertDto The data to overwrite.
     * @throws ValidationException Thrown if an id matching expert cannot be found.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processExpertProfileUpdateAsTransaction(int id, JsonExpertDetails expertDto)
            throws ValidationException {
        // Start of transaction
        Expert expert = expertService.getExpertById(id);
        if (expert == null) {
            // Roll back
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_EXPERT_MATCH, id)));
        } else {
            boolean resetVisibility =
                    !expert.getName().equals(expertDto.getName()) ||
                    !expert.getJobTitle().equals(expertDto.getJobTitle()) ||
                    !expert.getInstitution().equals(expertDto.getInstitution()) ||
                    expert.getVisibilityRequested() != expertDto.getVisibilityRequested();

            List<ValidatorDiseaseGroup> allValidatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();
            List<ValidatorDiseaseGroup> interests = filter(
                    having(on(ValidatorDiseaseGroup.class).getId(), isIn(expertDto.getDiseaseInterests())),
                    allValidatorDiseaseGroups);

            expert.setValidatorDiseaseGroups(interests);
            expert.setName(expertDto.getName());
            expert.setJobTitle(expertDto.getJobTitle());
            expert.setInstitution(expertDto.getInstitution());
            expert.setVisibilityRequested(expertDto.getVisibilityRequested());

            if (resetVisibility) {
                expert.setVisibilityApproved(false);
                expert.setUpdatedDate(DateTime.now());
            }

            expertService.saveExpert(expert);

            if (resetVisibility) {
                sendVisibilityEmailToAdmin(expert);
            }
        }
        // End of transaction
    }

    /**
     * Updates the database entry for an expert's password.
     * @param id The expert to update.
     * @param password The new password.
     * @throws ValidationException Thrown if an id matching expert cannot be found.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processExpertPasswordChangeAsTransaction(int id, String password) throws ValidationException {
        // Start of transaction
        Expert expert = expertService.getExpertById(id);
        if (expert == null) {
            // Roll back
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_EXPERT_MATCH, id)));
        } else {
            String passwordHash = passwordEncoder.encode(password);
            expert.setPassword(passwordHash);
            expertService.saveExpert(expert);
        }
        // End of transaction
    }

    private void sendVisibilityEmailToAdmin(Expert expert) {
        if (expert.getVisibilityRequested()) {
            Map<String, Object> data = new HashMap<>();
            data.put(EXPERT_VISIBILITY_EMAIL_DATA_KEY, expert);
            emailService.sendEmailInBackground(VISIBILITY_EMAIL_SUBJECT, VISIBILITY_EMAIL_TEMPLATE, data);
        }
    }

    /**
     * Updates the database to issue a new password reset request.
     * @param email The email address of the expert to issue a password reset request for.
     * @param url The base url of servlet to use in the "reset email".
     * @throws ValidationException Thrown if an email matching expert cannot be found.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processExpertPasswordResetRequestAsTransaction(String email, String url) throws ValidationException {
        // Start of transaction
        Expert expert = expertService.getExpertByEmail(email);
        if (expert == null) {
            // Roll back
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_EXPERT_MATCH, email)));
        } else {
            String key = PasswordResetRequest.createPasswordResetRequestKey();
            Integer id = expertService.createAndSavePasswordResetRequest(email, key);
            HashMap<String, Object> data = new HashMap<>();
            data.put(KEY_RESET_EMAIL_DATA_KEY, key);
            data.put(ID_RESET_EMAIL_DATA_KEY, id);
            data.put(URL_RESET_EMAIL_DATA_KEY, url);
            emailService.sendEmailInBackground(
                    email, PASSWORD_RESET_EMAIL_SUBJECT, PASSWORD_RESET_EMAIL_TEMPLATE, data);
        }
        // End of transaction
    }

    /**
     * Updates the database change expert's password using the password reset system.
     * @param password The new password.
     * @param id The id of the password reset request.
     * @throws ValidationException Thrown if an id matching password reset request cannot be found.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processExpertPasswordResetAsTransaction(String password, Integer id)
            throws ValidationException {
        // Start of transaction
        PasswordResetRequest passwordResetRequest = expertService.getPasswordResetRequest(id);
        if (passwordResetRequest == null) {
            // Roll back
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_REQUEST_MATCH, id)));
        } else {
            String passwordHash = passwordEncoder.encode(password);
            Expert expert = passwordResetRequest.getExpert();
            expert.setPassword(passwordHash);
            expertService.saveExpert(expert);
            expertService.deletePasswordResetRequest(passwordResetRequest);
        }
        // End of transaction
    }
}
