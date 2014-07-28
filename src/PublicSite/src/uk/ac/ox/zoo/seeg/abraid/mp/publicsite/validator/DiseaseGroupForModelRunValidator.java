package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtent;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Validates whether or not the model can be run for a disease group.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupForModelRunValidator {
    private static final String MISSING_MESSAGE = "%s is missing";
    private static final String PARAMETER_MISSING_MESSAGE = "the disease extent parameter %s is missing";
    private static final String DISEASE_EXTENT_PARAMETERS_MISSING_MESSAGE = "the disease extent parameters are missing";

    private static final String PUBLIC_NAME = "the public name";
    private static final String SHORT_NAME = "the short name";
    private static final String ABBREVIATION = "the abbreviation";
    private static final String GLOBAL_TROPICAL = "global/tropical";
    private static final String VALIDATOR_DISEASE_GROUP = "the Data Validator disease group";
    private static final String MAX_MONTHS_AGO = "'maximum months ago'";
    private static final String MIN_VALIDATION_WEIGHTING = "'minimum validation weighting'";
    private static final String MIN_OCCURRENCES_FOR_PRESENCE = "'minimum occurrences for presence'";
    private static final String MIN_OCCURRENCES_FOR_POSSIBLE_PRESENCE = "'minimum occurrences for possible presence'";
    private static final String MAX_MONTHS_AGO_FOR_HIGHER_SCORE = "'maximum months ago for higher occurrence score'";
    private static final String LOWER_SCORE = "'lower occurrence score'";
    private static final String HIGHER_SCORE = "'higher occurrence score'";

    private DiseaseGroup diseaseGroup;

    public DiseaseGroupForModelRunValidator(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    /**
     * Validate the disease group.
     * @return An error message if invalid, or null if valid.
     */
    public String validate() {
        String errorMessage = validatePublicNameMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateShortNameMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateAbbreviationMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateIsGlobalMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateValidatorDiseaseGroupMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateDiseaseExtentParametersMissing();
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
            return String.format(MISSING_MESSAGE, ABBREVIATION);
        }
        return null;
    }

    private String validateIsGlobalMissing() {
        if (diseaseGroup.isGlobal() == null) {
            return String.format(MISSING_MESSAGE, GLOBAL_TROPICAL);
        }
        return null;
    }

    private String validateValidatorDiseaseGroupMissing() {
        if (diseaseGroup.getValidatorDiseaseGroup() == null) {
            return String.format(MISSING_MESSAGE, VALIDATOR_DISEASE_GROUP);
        }
        return null;
    }

    private String validateDiseaseExtentParametersMissing() {
        DiseaseExtent parameters = diseaseGroup.getDiseaseExtentParameters();
        if (parameters == null) {
            return DISEASE_EXTENT_PARAMETERS_MISSING_MESSAGE;
        } else {
            if (parameters.getMaximumMonthsAgo() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, MAX_MONTHS_AGO);
            }
            if (parameters.getMinimumValidationWeighting() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, MIN_VALIDATION_WEIGHTING);
            }
            if (parameters.getMinimumOccurrencesForPresence() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, MIN_OCCURRENCES_FOR_PRESENCE);
            }
            if (parameters.getMinimumOccurrencesForPossiblePresence() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, MIN_OCCURRENCES_FOR_POSSIBLE_PRESENCE);
            }
            if (parameters.getMaximumMonthsAgoForHigherOccurrenceScore() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, MAX_MONTHS_AGO_FOR_HIGHER_SCORE);
            }
            if (parameters.getLowerOccurrenceScore() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, LOWER_SCORE);
            }
            if (parameters.getHigherOccurrenceScore() == null) {
                return String.format(PARAMETER_MISSING_MESSAGE, HIGHER_SCORE);
            }
        }
        return null;
    }
}
