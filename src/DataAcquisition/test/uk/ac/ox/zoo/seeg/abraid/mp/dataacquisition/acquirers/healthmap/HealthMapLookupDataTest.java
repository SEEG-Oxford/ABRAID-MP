package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.AlertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the HealthMapLookupData class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLookupDataTest {
    @Test
    public void getCountryMap() {
        // Arrange
        AlertService alertService = mock(AlertService.class);
        LocationService locationService = mock(LocationService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);

        Country country1 = new Country(1, "Test country 1");
        Country country2 = new Country(2, "Test country 2");
        HealthMapCountry healthMapCountry1 = new HealthMapCountry(1, "Test HealthMap country 1", country1);
        HealthMapCountry healthMapCountry2 = new HealthMapCountry(2, "Test HealthMap country 2", country2);

        List<HealthMapCountry> countries = Arrays.asList(healthMapCountry1, healthMapCountry2);
        when(locationService.getAllHealthMapCountries()).thenReturn(countries);

        Map<Integer, HealthMapCountry> expectedCountryMap = new HashMap<>();
        expectedCountryMap.put(1, healthMapCountry1);
        expectedCountryMap.put(2, healthMapCountry2);

        // Act
        HealthMapLookupData lookupData = new HealthMapLookupData(alertService, locationService, diseaseService);
        Map<Integer, HealthMapCountry> actualCountryMap = lookupData.getCountryMap();

        // Assert
        assertThat(actualCountryMap).isEqualTo(expectedCountryMap);
    }

    @Test
    public void getDiseaseMap() {
        // Arrange
        AlertService alertService = mock(AlertService.class);
        LocationService locationService = mock(LocationService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);

        DiseaseGroup disease1 = new DiseaseGroup(1, null, "Test disease 1", DiseaseGroupType.CLUSTER);
        DiseaseGroup disease2 = new DiseaseGroup(2, null, "Test disease 2", DiseaseGroupType.CLUSTER);
        HealthMapDisease healthMapDisease1 = new HealthMapDisease(1, "Test HealthMap disease 1", disease1);
        HealthMapDisease healthMapDisease2 = new HealthMapDisease(2, "Test HealthMap disease 2", disease2);

        List<HealthMapDisease> diseases = Arrays.asList(healthMapDisease1, healthMapDisease2);
        when(diseaseService.getAllHealthMapDiseases()).thenReturn(diseases);

        Map<Integer, HealthMapDisease> expectedDiseaseMap = new HashMap<>();
        expectedDiseaseMap.put(1, healthMapDisease1);
        expectedDiseaseMap.put(2, healthMapDisease2);

        // Act
        HealthMapLookupData lookupData = new HealthMapLookupData(alertService, locationService, diseaseService);
        Map<Integer, HealthMapDisease> actualDiseaseMap = lookupData.getDiseaseMap();

        // Assert
        assertThat(actualDiseaseMap).isEqualTo(expectedDiseaseMap);
    }

    @Test
    public void getFeedMap() {
        // Arrange
        AlertService alertService = mock(AlertService.class);
        LocationService locationService = mock(LocationService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);

        Provenance provenance = new Provenance(ProvenanceNames.HEALTHMAP);
        Feed feed1 = new Feed(10, "Test feed 1", provenance, null, 1, 1);
        Feed feed2 = new Feed(20, "Test feed 2", provenance, "de", 1, 2);

        List<Feed> feeds = Arrays.asList(feed1, feed2);
        when(alertService.getFeedsByProvenanceName(ProvenanceNames.HEALTHMAP)).thenReturn(feeds);

        Map<Integer, Feed> expectedFeedMap = new HashMap<>();
        expectedFeedMap.put(1, feed1);
        expectedFeedMap.put(2, feed2);

        // Act
        HealthMapLookupData lookupData = new HealthMapLookupData(alertService, locationService, diseaseService);
        Map<Integer, Feed> actualFeedMap = lookupData.getFeedMap();

        // Assert
        assertThat(actualFeedMap).isEqualTo(expectedFeedMap);
    }

    @Test
    public void getGeoNamesMap() {
        // Arrange
        AlertService alertService = mock(AlertService.class);
        LocationService locationService = mock(LocationService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);

        Map<String, LocationPrecision> expectedGeoNamesMap = new HashMap<>();
        expectedGeoNamesMap.put("ADM1", LocationPrecision.ADMIN1);
        expectedGeoNamesMap.put("PCLI", LocationPrecision.COUNTRY);

        when(locationService.getGeoNamesLocationPrecisionMappings()).thenReturn(expectedGeoNamesMap);

        // Act
        HealthMapLookupData lookupData = new HealthMapLookupData(alertService, locationService, diseaseService);
        Map<String, LocationPrecision> actualGeoNamesMap = lookupData.getGeoNamesMap();

        // Assert
        assertThat(actualGeoNamesMap).isEqualTo(expectedGeoNamesMap);
    }

    @Test
    public void getHealthMapProvenance() {
        // Arrange
        AlertService alertService = mock(AlertService.class);
        LocationService locationService = mock(LocationService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);

        Provenance expectedProvenance = new Provenance();
        when(alertService.getProvenanceByName(ProvenanceNames.HEALTHMAP)).thenReturn(expectedProvenance);

        // Act
        HealthMapLookupData lookupData = new HealthMapLookupData(alertService, locationService, diseaseService);
        Provenance actualProvenance = lookupData.getHealthMapProvenance();

        // Assert
        assertThat(actualProvenance).isEqualTo(expectedProvenance);
    }
}
