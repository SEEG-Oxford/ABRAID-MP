package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertValidationRulesChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the fields associated with an Expert profile updates & password changes.
 * Copyright (c) 2014 University of Oxford
 */
public class AccountControllerValidator {
    private final ExpertValidationRulesChecker rules;

    @Autowired
    public AccountControllerValidator(ExpertValidationRulesChecker expertValidationRulesChecker) {
        this.rules = expertValidationRulesChecker;
    }

    /**
     * Validates an expert dto for the fields that can be updated in the profile details page.
     * @param expert The expert to validate.
     * @return A list of validation failures.
     */
    public List<String> validate(JsonExpertDetails expert) {
        List<String> validationFailures = new ArrayList<>();

        rules.checkName(expert.getName(), validationFailures);
        rules.checkJobTitle(expert.getJobTitle(), validationFailures);
        rules.checkInstitution(expert.getInstitution(), validationFailures);
        rules.checkVisibilityRequested(expert.getVisibilityRequested(), validationFailures);
        rules.checkDiseaseInterests(expert.getDiseaseInterests(), validationFailures);

        return validationFailures;
    }

    /**
     * Validates an expert's password change request.
     * @param oldPassword The current password.
     * @param newPassword The new password.
     * @param confirmPassword The new password (confirmation).
     * @param expertId The id of the expert.
     * @return A list of validation failures.
     */
    public List<String> validatePasswordChange(String oldPassword, String newPassword,
                                               String confirmPassword, int expertId) {
        List<String> validationFailures = new ArrayList<>();

        rules.checkCurrentPassword(oldPassword, expertId, validationFailures);
        rules.checkPasswordConfirmation(newPassword, confirmPassword, validationFailures);
        rules.checkPassword(newPassword, validationFailures);

        return validationFailures;
    }
}
