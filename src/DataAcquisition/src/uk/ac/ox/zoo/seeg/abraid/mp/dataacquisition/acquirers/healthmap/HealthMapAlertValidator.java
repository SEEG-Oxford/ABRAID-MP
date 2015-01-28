package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;

import java.util.List;

/**
 * Validates a HealthMapAlert.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertValidator {
    private static final String DISEASE_IDS_NOT_FOUND = "Missing disease IDs in HealthMap alert (alert ID %d)";
    private static final String DISEASE_ID_NOT_FOUND_WITHIN_LIST = "Missing disease ID within list in HealthMap alert" +
            " (alert ID %d)";
    private static final String DISEASES_NOT_FOUND = "Missing diseases in HealthMap alert (alert ID %d)";
    private static final String DISEASE_IDS_DO_NOT_MATCH_NAMES = "HealthMap alert has %d disease ID(s) but %d " +
            "disease name(s) (alert ID %d)";
    private static final String HAS_A_PLACE_CATEGORY_TO_IGNORE = "Ignoring HealthMap alert because it has place " +
            "category \"%s\" (alert ID %d)";
    private static final String DATE_NOT_FOUND = "Missing date in HealthMap alert (alert ID %d)";
    private static final String FEED_NOT_FOUND = "Missing feed in HealthMap alert (alert ID %d)";
    private static final String FEED_ID_NOT_FOUND = "Missing feed ID in HealthMap alert (alert ID %d)";

    private HealthMapAlert alert;
    private List<String> placeCategoriesToIgnore;

    public HealthMapAlertValidator(HealthMapAlert alert, List<String> placeCategoriesToIgnore) {
        this.alert = alert;
        this.placeCategoriesToIgnore = placeCategoriesToIgnore;
    }

    /**
     * Validate the alert.
     * @return An error message if invalid, or null if valid.
     */
    public String validate() {
        String errorMessage = validateDiseaseIdsMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateDiseaseIdMissingWithinList();
        errorMessage = (errorMessage != null) ? errorMessage : validateDiseasesMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateDiseaseIdsDoNotMatchNames();
        errorMessage = (errorMessage != null) ? errorMessage : validatePlaceCategoriesAreNotToBeIgnored();
        errorMessage = (errorMessage != null) ? errorMessage : validateDateMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateFeedMissing();
        errorMessage = (errorMessage != null) ? errorMessage : validateFeedIdMissing();
        return errorMessage;
    }

    private String validateDiseaseIdsMissing() {
        if (alert.getDiseaseIds().size() == 0) {
            return String.format(DISEASE_IDS_NOT_FOUND, alert.getAlertId());
        }
        return null;
    }

    private String validateDiseaseIdMissingWithinList() {
        if (alert.getDiseaseIds().contains(null)) {
            return String.format(DISEASE_ID_NOT_FOUND_WITHIN_LIST, alert.getAlertId());
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

    private String validatePlaceCategoriesAreNotToBeIgnored() {
        if (alert.getPlaceCategories() != null) {
            for (String category : alert.getPlaceCategories()) {
                if (StringUtils.hasText(category) && placeCategoriesToIgnore.contains(category.toLowerCase())) {
                    return String.format(HAS_A_PLACE_CATEGORY_TO_IGNORE, category, alert.getAlertId());
                }
            }
        }
        return null;
    }

    private String validateDateMissing() {
        if (alert.getDate() == null) {
            return String.format(DATE_NOT_FOUND, alert.getAlertId());
        }
        return null;
    }

    private String validateFeedMissing() {
        if (!StringUtils.hasText(alert.getFeed())) {
            return String.format(FEED_NOT_FOUND, alert.getAlertId());
        }
        return null;
    }

    private String validateFeedIdMissing() {
        if (alert.getFeedId() == null) {
            return String.format(FEED_ID_NOT_FOUND, alert.getAlertId());
        }
        return null;
    }
}
