package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the fields associated with an Expert profile updates.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertUpdateValidator {
    private final ExpertValidationRulesChecker rules;

    @Autowired
    public ExpertUpdateValidator(ExpertValidationRulesChecker expertValidationRulesChecker) {
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
}
