package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates the fields associated with an Expert profile updates.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertUpdateValidator extends BaseExpertValidator {
    @Autowired
    public ExpertUpdateValidator(ExpertService expertService) {
        super(expertService);
    }

    /**
     * Validates an expert dto for the fields that can be update in the profile details page.
     * @param expert The expert to validate.
     * @return A list of validation failures.
     */
    public List<String> validate(JsonExpertDetails expert) {
        List<String> validationFailures = new ArrayList<>();

        checkName(expert.getName(), validationFailures);
        checkJobTitle(expert.getJobTitle(), validationFailures);
        checkJobTitle(expert.getInstitution(), validationFailures);
        checkVisibilityRequested(expert.getVisibilityRequested(), validationFailures);
        checkDiseaseInterests(expert.getDiseaseInterests(), validationFailures);

        return validationFailures;
    }

}
