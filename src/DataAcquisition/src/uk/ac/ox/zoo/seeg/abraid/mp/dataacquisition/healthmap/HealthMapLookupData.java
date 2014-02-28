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
    private Map<String, HealthMapDisease> diseaseMap;
    private Map<String, Feed> feedMap;
    private Map<String, LocationPrecision> geoNamesMap;

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
     * Gets a list of HealthMap diseases, indexed by HealthMap disease name.
     * @return A list of HealthMap diseases, indexed by HealthMap disease name.
     */
    public Map<String, HealthMapDisease> getDiseaseMap() {
        if (diseaseMap == null) {
            List<HealthMapDisease> diseases = diseaseService.getAllHealthMapDiseases();
            diseaseMap = index(diseases, on(HealthMapDisease.class).getName());
        }
        return diseaseMap;
    }

    /**
     * Gets a list of HealthMap feeds, indexed by HealthMap feed name.
     * @return A list of HealthMap feeds, indexed by HealthMap feed name.
     */
    public Map<String, Feed> getFeedMap() {
        if (feedMap == null) {
            List<Feed> feeds = alertService.getFeedsByProvenanceName(ProvenanceNames.HEALTHMAP);
            feedMap = index(feeds, on(Feed.class).getName());
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
}
