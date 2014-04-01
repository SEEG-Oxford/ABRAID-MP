package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;

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

    private Map<Long, HealthMapCountry> countryMap;
    private Map<Long, HealthMapDisease> diseaseMap;
    private Map<Long, Feed> feedMap;
    private Map<String, LocationPrecision> geoNamesMap;
    private Provenance healthMapProvenance;

    public HealthMapLookupData(AlertService alertService, LocationService locationService,
                               DiseaseService diseaseService) {
        this.alertService = alertService;
        this.locationService = locationService;
        this.diseaseService = diseaseService;
    }

    /**
     * Gets a list of HealthMap countries, indexed by HealthMap country name.
     * @return A list of HealthMap countries, indexed by HealthMap country name.
     */
    public Map<Long, HealthMapCountry> getCountryMap() {
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
    public Map<Long, HealthMapDisease> getDiseaseMap() {
        if (diseaseMap == null) {
            List<HealthMapDisease> diseases = diseaseService.getAllHealthMapDiseases();
            diseaseMap = index(diseases, on(HealthMapDisease.class).getId());
        }
        return diseaseMap;
    }

    /**
     * Gets a list of HealthMap feeds, indexed by HealthMap feed ID.
     * @return A list of HealthMap feeds, indexed by HealthMap feed ID.
     */
    public Map<Long, Feed> getFeedMap() {
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
     * Clear the lookups. This avoids stale Hibernate objects across transactions.
     */
    public void clearLookups() {
        countryMap = null;
        diseaseMap = null;
        feedMap = null;
        geoNamesMap = null;
        healthMapProvenance = null;
    }
}
