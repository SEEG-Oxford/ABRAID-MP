package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;

/**
 * Validates a HealthMapAlert.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertValidator {
    private static final String DISEASE_IDS_NOT_FOUND = "Missing disease IDs in HealthMap alert (alert ID %d)";
    private static final String DISEASES_NOT_FOUND = "Missing diseases in HealthMap alert (alert ID %d)";
    private static final String DISEASE_IDS_DO_NOT_MATCH_NAMES = "HealthMap alert has %d disease ID(s) but %d " +
            "disease name(s) (alert ID %d)";
    private static final String MULTIPLE_DISEASES_WITH_COMMENT = "HealthMap alert has %d disease IDs and comment " +
            "field \"%s\"; this is not allowed because the comment cannot refer to multiple diseases (alert ID %d)";

    private HealthMapAlert alert;

    public HealthMapAlertValidator(HealthMapAlert alert) {
        this.alert = alert;
    }

    /**
     * Validate the alert.
     * @return An error message if invalid, or null if valid.
     */
    public String validate() {
        String errorMessage = validateDiseaseIdsMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateDiseasesMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateDiseaseIdsDoNotMatchNames();
        errorMessage = (errorMessage != null) ? errorMessage : validateMultipleDiseasesWithComment();
        return errorMessage;
    }

    private String validateDiseaseIdsMissing() {
        if (alert.getDiseaseIds().size() == 0) {
            return String.format(DISEASE_IDS_NOT_FOUND, alert.getAlertId());
        }
        return null;
    }

    private String validateDiseasesMissing() {
        if (alert.getDiseases().size() == 0) {
            return String.format(DISEASES_NOT_FOUND, alert.getAlertId());
        }
        return null;
    }

    private String validateDiseaseIdsDoNotMatchNames() {
        int diseaseIdsSize = alert.getDiseaseIds().size();
        int diseasesSize = alert.getDiseases().size();
        if (diseaseIdsSize != diseasesSize) {
            return String.format(DISEASE_IDS_DO_NOT_MATCH_NAMES, diseaseIdsSize, diseasesSize, alert.getAlertId());
        }
        return null;
    }

    private String validateMultipleDiseasesWithComment() {
        int diseaseIdsSize = alert.getDiseaseIds().size();
        if (diseaseIdsSize > 1 && StringUtils.hasText(alert.getComment())) {
            return String.format(MULTIPLE_DISEASES_WITH_COMMENT, diseaseIdsSize, alert.getComment(),
                    alert.getAlertId());
        }
        return null;
    }
}
