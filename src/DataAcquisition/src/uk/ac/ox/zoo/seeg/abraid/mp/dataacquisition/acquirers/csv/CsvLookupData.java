package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceNames;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Feed uploadedFeed;

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
     * Gets the feed to be associated with uploaded data.
     * @return The feed to be associated with uploaded data.
     */
    public Feed getFeedForUploadedData() {
        if (uploadedFeed == null) {
            List<Feed> feeds = alertService.getFeedsByProvenanceName(ProvenanceNames.UPLOADED);
            if (feeds.size() == 1) {
                uploadedFeed = feeds.get(0);
            } else {
                throw new RuntimeException(
                        String.format("There are %d feeds associated with the Uploaded provenance (expected 1)",
                        feeds.size()));
            }
        }
        return uploadedFeed;
    }

    /**
     * Clear the lookups whose data may be used in a SQL statement.
     * This avoids stale Hibernate objects across transactions.
     */
    public void clearLookups() {
        diseaseGroupMap = null;
    }
}
