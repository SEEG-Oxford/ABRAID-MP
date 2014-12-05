package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapDiseaseKey;

import java.util.*;

/**
 * Converts a HealthMap alert into an ABRAID disease occurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertConverter {
    private static final Logger LOGGER = Logger.getLogger(HealthMapAlertConverter.class);
    private static final String ALERT_ID_NOT_FOUND = "Could not extract alert ID from link \"%s\"";
    private static final String URL_INVALID =
            "Invalid URL (%s) from HealthMap alert (ID %d) was not saved on the new alert";
    private static final String DISEASE_NOT_OF_INTEREST_MESSAGE =
            "Disease occurrence not of interest (HealthMap disease %s, alert ID %d)";
    private static final String FOUND_NEW_FEED = "Found new HealthMap feed \"%s\" - adding it to the database";
    private static final String NEW_DISEASE_NAME = "NEW FROM HEALTHMAP: %s";
    private static final String FOUND_NEW_DISEASE_SUBJECT = "New HealthMap disease discovered: %s";
    private static final String FOUND_NEW_DISEASE_TEMPLATE = "newDiseaseEmail.ftl";
    private static final String FOUND_NEW_DISEASE_TEMPLATE_DISEASE_KEY = "disease";
    private static final String FOUND_NEW_DISEASE_TEMPLATE_GROUP_KEY = "cluster";


    private final AlertService alertService;
    private final DiseaseService diseaseService;
    private final EmailService emailService;
    private final HealthMapLookupData lookupData;

    private UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});

    public HealthMapAlertConverter(AlertService alertService, DiseaseService diseaseService,
                                   EmailService emailService, HealthMapLookupData lookupData) {
        this.alertService = alertService;
        this.diseaseService = diseaseService;
        this.emailService = emailService;
        this.lookupData = lookupData;
    }

    /**
     * Converts a HealthMap alert into a list of ABRAID disease occurrences.
     * @param healthMapAlert The HealthMap alert to convert.
     * @param location The ABRAID location (already converted from HealthMap).
     * @return A list of disease occurrences. This is empty if the alert should not be converted.
     */
    public List<DiseaseOccurrence> convert(HealthMapAlert healthMapAlert, Location location) {
        List<DiseaseOccurrence> occurrences = new ArrayList<>();

        if (validate(healthMapAlert)) {
            Alert alert = retrieveAlert(healthMapAlert);
            List<DiseaseGroup> diseaseGroups = retrieveDiseaseGroups(healthMapAlert);

            for (DiseaseGroup diseaseGroup : diseaseGroups) {
                DiseaseOccurrence occurrence = new DiseaseOccurrence();
                occurrence.setDiseaseGroup(diseaseGroup);
                occurrence.setOccurrenceDate(healthMapAlert.getDate());
                occurrence.setAlert(alert);
                occurrence.setLocation(location);
                occurrences.add(occurrence);
            }
        }

        return occurrences;
    }

    private boolean validate(HealthMapAlert healthMapAlert) {
        String validationMessage = new HealthMapAlertValidator(healthMapAlert).validate();
        if (validationMessage != null) {
            LOGGER.warn(validationMessage);
            return false;
        }
        return true;
    }

    private Alert retrieveAlert(HealthMapAlert healthMapAlert) {
        Alert alert = null;

        // Try to find an alert with the given ID (if specified)
        Integer alertId = healthMapAlert.getAlertId();
        if (alertId != null) {
            alert = alertService.getAlertByHealthMapAlertId(alertId);
        } else {
            LOGGER.warn(String.format(ALERT_ID_NOT_FOUND, healthMapAlert.getLink()));
        }

        if (alert == null) {
            // Alert doesn't exist, so create it
            alert = createAlert(healthMapAlert, alertId);
        }
        return alert;
    }

    private Alert createAlert(HealthMapAlert healthMapAlert, Integer alertId) {
        Alert alert = new Alert();
        alert.setFeed(retrieveFeed(healthMapAlert));
        alert.setTitle(healthMapAlert.getSummary());
        alert.setPublicationDate(healthMapAlert.getDate());
        setUrl(alert, healthMapAlert);
        alert.setSummary(healthMapAlert.getDescription());
        alert.setHealthMapAlertId(alertId);
        return alert;
    }

    private void setUrl(Alert alert, HealthMapAlert healthMapAlert) {
        String url = healthMapAlert.getOriginalUrl();
        if (urlValidator.isValid(url)) {
            alert.setUrl(url);
        } else {
            LOGGER.warn(String.format(URL_INVALID, url, healthMapAlert.getAlertId()));
        }
    }

    private Feed retrieveFeed(HealthMapAlert healthMapAlert) {
        Feed feed = lookupData.getFeedMap().get(healthMapAlert.getFeedId());
        if (feed != null) {
            renameFeedIfRequired(feed, healthMapAlert);
            changeFeedLanguageIfRequired(feed, healthMapAlert);
        } else {
            // If the feed ID does not exist in the database, automatically add a new feed
            feed = createAndSaveFeed(healthMapAlert);
            LOGGER.warn(String.format(FOUND_NEW_FEED, healthMapAlert.getFeed()));
        }
        return feed;
    }

    private Feed createAndSaveFeed(HealthMapAlert healthMapAlert) {
        Provenance provenance = lookupData.getHealthMapProvenance();
        Feed feed = new Feed();
        feed.setProvenance(provenance);
        feed.setName(healthMapAlert.getFeed());
        // The feed is given the default weighting for HealthMap feeds
        feed.setWeighting(provenance.getDefaultFeedWeighting());
        feed.setHealthMapFeedId(healthMapAlert.getFeedId());
        if (StringUtils.hasText(healthMapAlert.getFeedLanguage())) {
            feed.setLanguage(healthMapAlert.getFeedLanguage());
        }

        // Save the feed now rather than implicitly with the new alert, so that it's saved even if we don't end up
        // saving the disease occurrence
        alertService.saveFeed(feed);

        // Add the new feed to the cached map
        lookupData.getFeedMap().put(feed.getHealthMapFeedId(), feed);
        return feed;
    }

    private List<DiseaseGroup> retrieveDiseaseGroups(HealthMapAlert healthMapAlert) {
        List<DiseaseGroup> diseaseGroups = new ArrayList<>();
        List<Integer> diseaseIds = healthMapAlert.getDiseaseIds();
        List<String> diseaseNames = healthMapAlert.getDiseases();
        Set<String> subDiseaseNames = healthMapAlert.getSplitComment();
        if (subDiseaseNames.size() == 0) {
            // If there are no sub-diseases, add an empty one for lookup purposes
            subDiseaseNames.add(null);
        }

        // Iterate per disease per sub-disease
        for (int i = 0; i < diseaseIds.size(); i++) {
            int diseaseId = diseaseIds.get(i);
            String diseaseName = diseaseNames.get(i);

            for (String subDiseaseName : subDiseaseNames) {
                DiseaseGroup diseaseGroup = retrieveDiseaseGroup(healthMapAlert, diseaseId, diseaseName,
                        subDiseaseName);
                if (diseaseGroup != null) {
                    diseaseGroups.add(diseaseGroup);
                }
            }
        }

        return diseaseGroups;
    }

    private DiseaseGroup retrieveDiseaseGroup(HealthMapAlert healthMapAlert, int diseaseId, String diseaseName,
                                              String subDiseaseName) {
        HealthMapDiseaseKey healthMapDiseaseKey = new HealthMapDiseaseKey(diseaseId, subDiseaseName);
        HealthMapDisease healthMapDisease = retrieveHealthMapDisease(healthMapDiseaseKey);
        if (healthMapDisease != null) {
            renameHealthMapDiseaseIfRequired(healthMapDisease, diseaseName);
        } else {
            // HealthMap disease does not exist in database - create it and notify system administrator
            healthMapDisease = createAndSaveHealthMapDisease(healthMapDiseaseKey, diseaseName);
            notifyAboutNewHealthMapDisease(healthMapDisease);
        }

        if (healthMapDisease.getDiseaseGroup() == null) {
            // HealthMap disease is not linked to a disease group, which means that it is not of interest
            LOGGER.warn(String.format(DISEASE_NOT_OF_INTEREST_MESSAGE, healthMapDisease.getNamesForDisplay(),
                    healthMapAlert.getAlertId()));
        }

        return healthMapDisease.getDiseaseGroup();
    }

    private HealthMapDisease retrieveHealthMapDisease(HealthMapDiseaseKey key) {
        return lookupData.getDiseaseMap().get(key);
    }

    private void renameHealthMapDiseaseIfRequired(HealthMapDisease disease, String diseaseName) {
        if (!disease.getName().equals(diseaseName)) {
            disease.setName(diseaseName);
            diseaseService.saveHealthMapDisease(disease);
        }
    }

    private HealthMapDisease createAndSaveHealthMapDisease(HealthMapDiseaseKey key, String diseaseName) {
        HealthMapDisease healthMapDisease = new HealthMapDisease();
        healthMapDisease.setHealthMapDiseaseId(key.getDiseaseId());
        healthMapDisease.setName(diseaseName);
        healthMapDisease.setSubName(key.getSubName());

        // The new HealthMapDisease must be linked to a DiseaseGroup in order to be of interest. So create a
        // dummy DiseaseGroup with the same name as the HealthMap disease, of type CLUSTER.
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName(String.format(NEW_DISEASE_NAME, healthMapDisease.getNamesForDisplay()));
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);
        healthMapDisease.setDiseaseGroup(diseaseGroup);

        // This saves both the new HealthMap disease and the new disease cluster
        diseaseService.saveHealthMapDisease(healthMapDisease);

        // Add the new HealthMap disease to the cached map
        lookupData.getDiseaseMap().put(key, healthMapDisease);

        return healthMapDisease;
    }

    private void notifyAboutNewHealthMapDisease(HealthMapDisease healthMapDisease) {
        final String healthMapDiseaseNamesForDisplay = healthMapDisease.getNamesForDisplay();
        final String diseaseGroupName = healthMapDisease.getDiseaseGroup().getName();

        final String subject = String.format(FOUND_NEW_DISEASE_SUBJECT, healthMapDiseaseNamesForDisplay);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put(FOUND_NEW_DISEASE_TEMPLATE_DISEASE_KEY, healthMapDiseaseNamesForDisplay);
        templateData.put(FOUND_NEW_DISEASE_TEMPLATE_GROUP_KEY, diseaseGroupName);

        emailService.sendEmailInBackground(subject, FOUND_NEW_DISEASE_TEMPLATE, templateData);

        LOGGER.warn(subject);
    }

    private void renameFeedIfRequired(Feed feed, HealthMapAlert healthMapAlert) {
        String feedName = healthMapAlert.getFeed();
        if (!feed.getName().equals(feedName)) {
            feed.setName(feedName);
            alertService.saveFeed(feed);
        }
    }

    private void changeFeedLanguageIfRequired(Feed feed, HealthMapAlert healthMapAlert) {
        String feedLanguage = healthMapAlert.getFeedLanguage();
        if (ObjectUtils.nullSafeEquals(feedLanguage, "")) {
            feedLanguage = null;
        }
        if (!ObjectUtils.nullSafeEquals(feed.getLanguage(), feedLanguage)) {
            feed.setLanguage(feedLanguage);
            alertService.saveFeed(feed);
        }
    }
}
