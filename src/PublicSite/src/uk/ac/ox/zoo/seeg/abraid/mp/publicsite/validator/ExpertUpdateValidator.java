package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertBasic;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates the fields associated with an Expert during registration.
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertUpdateValidator extends BaseExpertValidator {
    @Autowired
    public ExpertUpdateValidator(ExpertService expertService) {
        super(expertService);
    }

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
