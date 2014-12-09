package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Contains lookup data that is used when processing CSV data.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvLookupData {
    private AlertService alertService;
    private LocationService locationService;
    private DiseaseService diseaseService;

    private Map<String, Country> countryMap;
    private Map<String, DiseaseGroup> diseaseGroupMap;
    private List<Feed> manuallyUploadedFeeds;
    private List<Feed> goldStandardFeeds;

    public CsvLookupData(AlertService alertService, LocationService locationService, DiseaseService diseaseService) {
        this.alertService = alertService;
        this.locationService = locationService;
        this.diseaseService = diseaseService;
    }

    /**
     * Gets a list of countries, indexed by lowercase name.
     * @return A list of countries, indexed by lowercase name.
     */
    public Map<String, Country> getCountryMap() {
        if (countryMap == null) {
            List<Country> countries = locationService.getAllCountries();
            // We need the keys to be lowercase for case-insensitive comparisons (so Lambda.index cannot be used here)
            countryMap = new HashMap<>();
            for (Country country : countries) {
                countryMap.put(country.getName().toLowerCase(), country);
            }

        }
        return countryMap;
    }

    /**
     * Gets a list of disease groups, indexed by lowercase name.
     * @return A list of disease groups, indexed by lowercase name.
     */
    public Map<String, DiseaseGroup> getDiseaseGroupMap() {
        if (diseaseGroupMap == null) {
            List<DiseaseGroup> diseaseGroups = diseaseService.getAllDiseaseGroups();
            // We need the keys to be lowercase for case-insensitive comparisons (so Lambda.index cannot be used here)
            diseaseGroupMap = new HashMap<>();
            for (DiseaseGroup diseaseGroup : diseaseGroups) {
                diseaseGroupMap.put(diseaseGroup.getName().toLowerCase(), diseaseGroup);
            }
        }
        return diseaseGroupMap;
    }

    /**
     * Gets the feed to be associated with this manually uploaded data, identified by the unique feed name in CSV row.
     * If a feed with the given name has not been seen before, save it to the database.
     * @param feedName The name of the feed to fetch (and add to the database if necessary).
     * @return The feed to be associated with this manually uploaded datapoint.
     */
    public Feed getFeedForManuallyUploadedData(String feedName, boolean goldStandard) {
        Feed feed = getExistingFeedByName(feedName, goldStandard);
        if (feed == null) {
            feed = goldStandard ? addNewGoldStandardFeed(feedName) : addNewFeed(feedName);
        }
        return feed;
    }

    public Feed getExistingFeedByName(String feedName, boolean goldStandard) {
        if (goldStandard) {
            if (manuallyUploadedFeeds == null) {
                manuallyUploadedFeeds = alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL);
            }
            return selectUnique(manuallyUploadedFeeds, having(on(Feed.class).getName(), equalTo(feedName)));
        } else {
            if (goldStandardFeeds == null) {
                goldStandardFeeds = alertService.getFeedsByProvenanceName(ProvenanceNames.MANUAL_GOLD_STANDARD);
            }
            return selectUnique(goldStandardFeeds, having(on(Feed.class).getName(), equalTo(feedName)));
        }
    }

    private Feed addNewGoldStandardFeed(String feedName) {
        Feed feed = new Feed(feedName, alertService.getProvenanceByName(ProvenanceNames.MANUAL_GOLD_STANDARD));
        alertService.saveFeed(feed);
        goldStandardFeeds.add(feed);
        return feed;
    }

    private Feed addNewFeed(String feedName) {
        Feed feed = new Feed(feedName, alertService.getProvenanceByName(ProvenanceNames.MANUAL));
        alertService.saveFeed(feed);
        manuallyUploadedFeeds.add(feed);
        return feed;
    }

    /**
     * Clear the lookups whose data may be used in a SQL statement.
     * This avoids stale Hibernate objects across transactions.
     */
    public void clearLookups() {
        diseaseGroupMap = null;
        manuallyUploadedFeeds = null;
        goldStandardFeeds = null;
    }
}
