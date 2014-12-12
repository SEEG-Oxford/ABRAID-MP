package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;

/**
 * Contains lookup data that is used when processing data from HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLookupData {
    private AlertService alertService;
    private LocationService locationService;
    private DiseaseService diseaseService;

    private Map<Integer, HealthMapCountry> countryMap;
    private Map<Integer, HealthMapDisease> diseaseMap;
    private Map<String, HealthMapSubDisease> subDiseaseMap;
    private Map<Integer, Feed> feedMap;
    private Map<String, LocationPrecision> geoNamesMap;
    private Provenance healthMapProvenance;

    public HealthMapLookupData(AlertService alertService, LocationService locationService,
                               DiseaseService diseaseService) {
        this.alertService = alertService;
        this.locationService = locationService;
        this.diseaseService = diseaseService;
    }

    /**
     * Gets a list of HealthMap countries, indexed by HealthMap country ID.
     * @return A list of HealthMap countries, indexed by HealthMap country ID.
     */
    public Map<Integer, HealthMapCountry> getCountryMap() {
        if (countryMap == null) {
            List<HealthMapCountry> countries = locationService.getAllHealthMapCountries();
            countryMap = index(countries, on(HealthMapCountry.class).getId());
        }
        return countryMap;
    }

    /**
     * Gets a list of HealthMap diseases, indexed by HealthMap disease ID.
     * @return A list of HealthMap diseases, indexed by HealthMap disease ID.
     */
    public Map<Integer, HealthMapDisease> getDiseaseMap() {
        if (diseaseMap == null) {
            List<HealthMapDisease> diseases = diseaseService.getAllHealthMapDiseases();
            diseaseMap = index(diseases, on(HealthMapDisease.class).getId());
        }
        return diseaseMap;
    }

    /**
     * Gets a list of HealthMap sub-diseases, indexed by sub-disease name.
     * @return A list of HealthMap sub-diseases, indexed by sub-disease name.
     */
    public Map<String, HealthMapSubDisease> getSubDiseaseMap() {
        if (subDiseaseMap == null) {
            List<HealthMapSubDisease> subDiseases = diseaseService.getAllHealthMapSubDiseases();
            subDiseaseMap = index(subDiseases, on(HealthMapSubDisease.class).getName());
        }
        return subDiseaseMap;
    }

    /**
     * Gets a list of HealthMap feeds, indexed by HealthMap feed ID.
     * @return A list of HealthMap feeds, indexed by HealthMap feed ID.
     */
    public Map<Integer, Feed> getFeedMap() {
        if (feedMap == null) {
            List<Feed> feeds = alertService.getFeedsByProvenanceName(ProvenanceNames.HEALTHMAP);
            feedMap = index(feeds, on(Feed.class).getHealthMapFeedId());
        }
        return feedMap;
    }

    /**
     * Gets a mapping between GeoNames feature codes and location precisions.
     * @return A mapping between GeoNames feature codes and location precisions.
     */
    public Map<String, LocationPrecision> getGeoNamesMap() {
        if (geoNamesMap == null) {
            geoNamesMap = locationService.getGeoNamesLocationPrecisionMappings();
        }
        return geoNamesMap;
    }

    /**
     * Gets the HealthMap provenance.
     * @return The HealthMap provenance.
     */
    public Provenance getHealthMapProvenance() {
        if (healthMapProvenance == null) {
            healthMapProvenance = alertService.getProvenanceByName(ProvenanceNames.HEALTHMAP);
        }
        return healthMapProvenance;
    }

    /**
     * Clear the lookups whose data may be used in a SQL statement.
     * This avoids stale Hibernate objects across transactions.
     */
    public void clearLookups() {
        diseaseMap = null;
        feedMap = null;
        healthMapProvenance = null;
    }
}
