package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapLocation;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.geonames.GeoNamesWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.geonames.domain.GeoName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the HealthMapLocationConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationConverterTest {
    private static final String MAPPED_COUNTRY_NAME = "Argentina";
    private static final String UNMAPPED_COUNTRY_NAME = "Maldives";
    private static final int MAPPED_COUNTRY_ID = 4;
    private static final int UNMAPPED_COUNTRY_ID = 143;

    private HealthMapCountry mappedHealthMapCountry;
    private HealthMapCountry unMappedHealthMapCountry;

    private LocationService locationService;
    private HealthMapLookupData lookupData;
    private GeoNamesWebService geoNamesWebService;
    private HealthMapLocationConverter converter;

    @Before
    public void setUp() {
        locationService = mock(LocationService.class);
        lookupData = mock(HealthMapLookupData.class);
        geoNamesWebService = mock(GeoNamesWebService.class);
        converter = new HealthMapLocationConverter(locationService, lookupData, geoNamesWebService);

        setUpCountryMap();
        setUpGeoNamesMap();
    }

    private void setUpCountryMap() {
        Map<Integer, HealthMapCountry> countryMap = new HashMap<>();
        mappedHealthMapCountry = new HealthMapCountry(MAPPED_COUNTRY_ID, MAPPED_COUNTRY_NAME,
                new Country(12, MAPPED_COUNTRY_NAME));
        unMappedHealthMapCountry = new HealthMapCountry(UNMAPPED_COUNTRY_ID, UNMAPPED_COUNTRY_NAME);
        unMappedHealthMapCountry.setCountries(new HashSet<Country>());
        countryMap.put(mappedHealthMapCountry.getId(), mappedHealthMapCountry);
        countryMap.put(unMappedHealthMapCountry.getId(), unMappedHealthMapCountry);
        when(lookupData.getCountryMap()).thenReturn(countryMap);
    }

    private void setUpGeoNamesMap() {
        Map<String, LocationPrecision> geoNamesMap = new HashMap<>();
        geoNamesMap.put("PPL", LocationPrecision.PRECISE);
        when(lookupData.getGeoNamesMap()).thenReturn(geoNamesMap);
    }

    @Test
    public void convertInvalidLocation() {
        HealthMapLocation healthMapLocation = new HealthMapLocation();
        Location location = converter.convert(healthMapLocation);
        assertThat(location).isNull();
    }

    @Test
    public void convertNewLocationsWithKnownGeoNameId() {
        Integer geoNameId = 123;
        String featureCode = "PPL";

        HealthMapLocation healthMapLocation = createDefaultHealthMapCountryLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName geoName = new
                uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName(geoNameId, featureCode);
        when(locationService.getGeoNameById(geoNameId)).thenReturn(geoName);


        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getId()).isNull();
        assertThat(location.getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getHealthMapCountryId()).isEqualTo(MAPPED_COUNTRY_ID);
        assertThat(location.getGeom().getX()).isEqualTo(20);
        assertThat(location.getGeom().getY()).isEqualTo(10);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
    }

    @Test
    public void convertNewLocationWithGeoNameId() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapCountryLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getId()).isNull();
        assertThat(location.getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getHealthMapCountryId()).isEqualTo(MAPPED_COUNTRY_ID);
        assertThat(location.getGeom().getX()).isEqualTo(20);
        assertThat(location.getGeom().getY()).isEqualTo(10);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
    }

    @Test
    public void convertNewLocationWithoutGeoNameId() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapCountryLocation();

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getId()).isNull();
        assertThat(location.getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getHealthMapCountryId()).isEqualTo(MAPPED_COUNTRY_ID);
        assertThat(location.getGeom().getX()).isEqualTo(20);
        assertThat(location.getGeom().getY()).isEqualTo(10);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
    }

    @Test
    public void convertNewLocationWithoutAbraidCountry() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapCountryLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());
        healthMapLocation.setCountry(unMappedHealthMapCountry.getName());
        healthMapLocation.setCountryId(unMappedHealthMapCountry.getId().toString());

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        // Location is null because the HealthMap country is not of interest
        assertThat(location).isNull();
    }

    @Test
    public void addPrecisionToNewLocationWithGeoNameIdReturnedByDatabase() {
        // Arrange
        Integer geoNameId = 123;
        String featureCode = "PPL";
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName geoName = new
                uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName(geoNameId, featureCode);
        when(locationService.getGeoNameById(geoNameId)).thenReturn(geoName);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        verify(geoNamesWebService, never()).getById(geoNameId);
    }

    @Test
    public void addPrecisionToNewLocationWithGeoNameIdReturnedByWebService() {
        // Arrange
        Integer geoNameId = 123;
        String featureCode = "PPL";
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        GeoName geoNameDTO = new GeoName(geoNameId, featureCode);
        uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName geoName = new
                uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName(geoNameId, featureCode);

        when(locationService.getGeoNameById(geoNameId)).thenReturn(null);
        when(geoNamesWebService.getById(geoNameId)).thenReturn(geoNameDTO);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        verify(locationService).saveGeoName(eq(geoName));
    }

    @Test
    public void addPrecisionToNewLocationWithGeoNamesFeatureCodeNotInOurMapping() {
        // Arrange
        Integer geoNameId = 123;
        String featureCode = "ISL";
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        GeoName geoNameDTO = new GeoName(geoNameId, featureCode);
        uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName geoName = new
                uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName(geoNameId, featureCode);

        when(locationService.getGeoNameById(geoNameId)).thenReturn(null);
        when(geoNamesWebService.getById(geoNameId)).thenReturn(geoNameDTO);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
        verify(locationService).saveGeoName(eq(geoName));
    }

    @Test
    public void addPrecisionToNewLocationWithGeoNameNotFound() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        when(locationService.getGeoNameById(geoNameId)).thenReturn(null);
        when(geoNamesWebService.getById(geoNameId)).thenReturn(null);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
        verify(locationService, never()).saveGeoName(any(uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName.class));
    }

    @Test
    public void addPrecisionToNewLocationWithGeoNameReturnedByWebServiceButNoFeatureCode() {
        // Arrange
        Integer geoNameId = 123;
        String featureCode = "";
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        GeoName geoNameDTO = new GeoName(geoNameId, featureCode);
        when(locationService.getGeoNameById(geoNameId)).thenReturn(null);
        when(geoNamesWebService.getById(geoNameId)).thenReturn(geoNameDTO);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
        verify(locationService, never()).saveGeoName(any(uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName.class));
    }

    @Test
    public void addPrecisionToNewLocationWithoutGeoNameId() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isNull();
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
    }

    @Test
    public void addCountryPrecisionIfPlaceNameIsAHealthMapCountryName() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setPlaceName(UNMAPPED_COUNTRY_NAME);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getGeoNameId()).isNull();
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
    }

    @Test
    public void addPrecisionToNewLocationWithoutGeoNameIdOrPlaceBasicType() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setPlaceBasicType(null);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location).isNull();
    }

    private HealthMapLocation createDefaultHealthMapLocation() {
        HealthMapLocation location = new HealthMapLocation();
        location.setLongitude("20");
        location.setLatitude("10");
        location.setCountryId(mappedHealthMapCountry.getId().toString());
        location.setCountry(MAPPED_COUNTRY_NAME);
        location.setPlaceBasicType("l");
        location.setPlaceName("Test place name");
        return location;
    }

    private HealthMapLocation createDefaultHealthMapCountryLocation() {
        HealthMapLocation location = createDefaultHealthMapLocation();
        location.setPlaceBasicType("c");
        location.setPlaceName(MAPPED_COUNTRY_NAME);
        return location;
    }
}
