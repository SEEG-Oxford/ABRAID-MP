package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Validates whether or not the model can be run for a disease group.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupForModelRunValidator {
    private static final int DENGUE_DISEASE_GROUP_ID = 87;

    private static final String MISSING_MESSAGE = "%s is missing";
    private static final String DENGUE_ONLY_MESSAGE = "the model can currently only be run for dengue";

    private static final String PUBLIC_NAME = "the public name";
    private static final String SHORT_NAME = "the short name";
    private static final String ABBREVIATION_NAME = "the abbreviation";
    private static final String GLOBAL_TROPICAL_NAME = "global/tropical";
    private static final String VALIDATOR_DISEASE_GROUP_NAME = "the Data Validator disease group";

    private DiseaseGroup diseaseGroup;

    public DiseaseGroupForModelRunValidator(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    /**
     * Validate the disease group.
     * @return An error message if invalid, or null if valid.
     */
    public String validate() {
        String errorMessage = validateIsDengue();
        errorMessage = (errorMessage != null) ? errorMessage : validatePublicNameMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateShortNameMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateAbbreviationMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateIsGlobalMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateValidatorDiseaseGroupMissing();
        return errorMessage;
    }

    private String validatePublicNameMissing() {
        if (!StringUtils.hasText(diseaseGroup.getPublicName())) {
            return String.format(MISSING_MESSAGE, PUBLIC_NAME);
        }
        return null;
    }

    private String validateShortNameMissing() {
        if (!StringUtils.hasText(diseaseGroup.getShortName())) {
            return String.format(MISSING_MESSAGE, SHORT_NAME);
        }
        return null;
    }

    private String validateAbbreviationMissing() {
        if (!StringUtils.hasText(diseaseGroup.getAbbreviation())) {
            return String.format(MISSING_MESSAGE, ABBREVIATION_NAME);
        }
        return null;
    }

    private String validateIsGlobalMissing() {
        if (diseaseGroup.isGlobal() == null) {
            return String.format(MISSING_MESSAGE, GLOBAL_TROPICAL_NAME);
        }
        return null;
    }

    private String validateValidatorDiseaseGroupMissing() {
        if (diseaseGroup.getValidatorDiseaseGroup() == null) {
            return String.format(MISSING_MESSAGE, VALIDATOR_DISEASE_GROUP_NAME);
        }
        return null;
    }

    private String validateIsDengue() {
        if (diseaseGroup.getId() == null || diseaseGroup.getId() != DENGUE_DISEASE_GROUP_ID) {
            return DENGUE_ONLY_MESSAGE;
        }
        return null;
    }
}
