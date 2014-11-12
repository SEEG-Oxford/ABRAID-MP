package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
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
    private static final String FAIL_NO_ID_MATCH = "No matching expert found to update (%s).";
    private static final String EMAIL_DATA_KEY = "expert";
    private static final String EMAIL_SUBJECT = "Updated user requiring visibility sign off";
    private static final String EMAIL_TEMPLATE = "account/updatedUserEmail.ftl";
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
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_ID_MATCH, id)));
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
                emailAdmin(expert);
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
            throw new ValidationException(Arrays.asList(String.format(FAIL_NO_ID_MATCH, id)));
        } else {
            String passwordHash = passwordEncoder.encode(password);
            expert.setPassword(passwordHash);
            expertService.saveExpert(expert);
        }
        // End of transaction
    }

    private void emailAdmin(Expert expert) {
        if (expert.getVisibilityRequested()) {
            Map<String, Object> data = new HashMap<>();
            data.put(EMAIL_DATA_KEY, expert);
            emailService.sendEmailInBackground(EMAIL_SUBJECT, EMAIL_TEMPLATE, data);
        }
    }
}
