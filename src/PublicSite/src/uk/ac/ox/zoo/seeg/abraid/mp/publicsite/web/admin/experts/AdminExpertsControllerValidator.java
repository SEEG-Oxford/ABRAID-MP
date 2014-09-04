package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.experts;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertValidationRulesChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Validates the fields associated with an Expert profile updates.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminExpertsControllerValidator {
    private final ExpertValidationRulesChecker rules;

    @Autowired
    public AdminExpertsControllerValidator(ExpertValidationRulesChecker expertValidationRulesChecker) {
        this.rules = expertValidationRulesChecker;
    }

    /**
     * Validates an expert dto for the fields required for an admin updating an expert.
     * @param expert The expert to validate.
     * @return A list of validation failures.
     */
    public Collection<String> validate(JsonExpertFull expert) {
        List<String> validationFailures = new ArrayList<>();

        rules.checkId(expert.getId(), validationFailures);
        rules.checkVisibilityApproved(expert.getVisibilityApproved(), validationFailures);
        rules.checkWeighting(expert.getWeighting(), validationFailures);
        rules.checkIsAdministrator(expert.isAdministrator(), validationFailures);
        rules.checkIsSeegMember(expert.isSEEGMember(), validationFailures);

        return validationFailures;
    }
}
