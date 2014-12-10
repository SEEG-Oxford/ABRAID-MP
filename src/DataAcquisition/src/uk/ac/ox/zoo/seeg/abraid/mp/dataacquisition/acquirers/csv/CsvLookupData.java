package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

/**
 * Contains lookup data that is used when processing CSV data.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvLookupData {
    private static final Logger LOGGER = Logger.getLogger(CsvLookupData.class);
    private static final String SAVED_NEW_FEED = "Saved new feed \"%s\" under provenance \"%s\"";

    private AlertService alertService;
    private LocationService locationService;
    private DiseaseService diseaseService;

    private Map<String, Country> countryMap;
    private Map<String, DiseaseGroup> diseaseGroupMap;
    private Map<String, List<Feed>> provenanceFeedsMap;

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
            // We need the keys to be lowercase for case-insensitive comparisons (so Lambda.index cannot be used here)
            countryMap = new HashMap<>();
            for (Country country : locationService.getAllCountries()) {
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
            // We need the keys to be lowercase for case-insensitive comparisons (so Lambda.index cannot be used here)
            diseaseGroupMap = new HashMap<>();
            for (DiseaseGroup diseaseGroup : diseaseService.getAllDiseaseGroups()) {
                diseaseGroupMap.put(diseaseGroup.getName().toLowerCase(), diseaseGroup);
            }
        }
        return diseaseGroupMap;
    }

    /**
     * Gets the feed to be associated with this manually uploaded data, identified by the unique feed name in CSV row.
     * If a feed with the given name has not been seen before, save it to the database.
     * @param feedName The name of the feed to fetch (and add to the database if necessary).
     * @param isGoldStandard Whether the occurrence is a "gold standard" data point,
     *                       and should be saved against that provenance.
     * @return The feed to be associated with this manually uploaded datapoint.
     */
    public Feed getFeedForManuallyUploadedData(String feedName, boolean isGoldStandard) {
        initialiseProvenanceFeedsMap();
        String provenanceName = isGoldStandard ? ProvenanceNames.MANUAL_GOLD_STANDARD : ProvenanceNames.MANUAL;
        Feed feed = getExistingFeed(provenanceName, feedName);
        if (feed == null) {
            feed = addNewFeed(provenanceName, feedName);
        }
        return feed;
    }

    private void initialiseProvenanceFeedsMap() {
        if (provenanceFeedsMap == null) {
            provenanceFeedsMap = new HashMap<>();
        }
    }

    private Feed getExistingFeed(String provenanceName, String feedName) {
        if (!provenanceFeedsMap.containsKey(provenanceName)) {
            provenanceFeedsMap.put(provenanceName, new ArrayList<>(alertService.getFeedsByProvenanceName(provenanceName)));
        }
        return selectUnique(provenanceFeedsMap.get(provenanceName),
                            having(on(Feed.class).getName(), equalToIgnoringCase(feedName)));
    }

    private Feed addNewFeed(String provenanceName, String feedName) {
        Feed feed = new Feed(feedName, alertService.getProvenanceByName(provenanceName));
        alertService.saveFeed(feed);
        provenanceFeedsMap.get(provenanceName).add(feed);
        LOGGER.warn(String.format(SAVED_NEW_FEED, feedName, provenanceName));
        return feed;
    }

    /**
     * Clear the lookups whose data may be used in a SQL statement.
     * This avoids stale Hibernate objects across transactions.
     */
    public void clearLookups() {
        diseaseGroupMap = null;
        provenanceFeedsMap = null;
    }
}
