package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.GeoNamesWebService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain.GeoName;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain.HealthMapLocation;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the HealthMapLocationConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocationConverterTest {
    public static final String MAPPED_COUNTRY_NAME = "Argentina";
    public static final String UNMAPPED_COUNTRY_NAME = "Maldives";

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
        Map<Long, HealthMapCountry> countryMap = new HashMap<>();
        mappedHealthMapCountry = new HealthMapCountry(4L, MAPPED_COUNTRY_NAME, new Country(12, MAPPED_COUNTRY_NAME));
        unMappedHealthMapCountry = new HealthMapCountry(143L, UNMAPPED_COUNTRY_NAME);
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
    public void convertLocationAlreadyExistsWithGeoNameId() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());
        Location existingLocation = new Location();
        when(locationService.getLocationByGeoNameId(123)).thenReturn(existingLocation);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location).isSameAs(existingLocation);
    }

    @Test
    public void convertLocationAlreadyExistsWithoutGeoNameId() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        Point point = GeometryUtils.createPoint(20, 10);
        Location existingLocation = new Location();
        List<Location> existingLocations = Arrays.asList(existingLocation);
        when(locationService.getLocationsByPointAndPrecision(point, LocationPrecision.COUNTRY))
                .thenReturn(existingLocations);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location).isSameAs(existingLocation);
    }

    @Test
    public void convertNewLocationWithgeoNameId() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());
        when(locationService.getLocationByGeoNameId(123)).thenReturn(null);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getId()).isNull();
        assertThat(location.getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getHealthMapCountry()).isNotNull();
        assertThat(location.getHealthMapCountry().getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getGeom().getX()).isEqualTo(20);
        assertThat(location.getGeom().getY()).isEqualTo(10);
    }

    @Test
    public void convertNewLocationWithoutgeoNameId() {
        // Arrange
        Point point = GeometryUtils.createPoint(20, 10);
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        when(locationService.getLocationsByPointAndPrecision(point, LocationPrecision.COUNTRY))
                .thenReturn(new ArrayList<Location>());

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        assertThat(location.getId()).isNull();
        assertThat(location.getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getHealthMapCountry()).isNotNull();
        assertThat(location.getHealthMapCountry().getName()).isEqualTo(MAPPED_COUNTRY_NAME);
        assertThat(location.getGeom().getX()).isEqualTo(20);
        assertThat(location.getGeom().getY()).isEqualTo(10);
    }

    @Test
    public void convertNewLocationWithoutAbraidCountry() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());
        healthMapLocation.setCountry(unMappedHealthMapCountry.getName());
        healthMapLocation.setCountryId(unMappedHealthMapCountry.getId().toString());
        when(locationService.getLocationByGeoNameId(123)).thenReturn(null);

        // Act
        Location location = converter.convert(healthMapLocation);

        // Assert
        // Location is null because the HealthMap country is not of interest
        assertThat(location).isNull();
    }

    @Test
    public void addPrecisionWithGeoNameId() {
        // Arrange
        Integer geoNameId = 123;
        String featureCode = "PPL";
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        Location location = new Location();
        GeoName geoName = new GeoName(geoNameId, featureCode);
        when(geoNamesWebService.getById(geoNameId)).thenReturn(geoName);

        // Act
        converter.addPrecision(healthMapLocation, location);

        // Assert
//        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
//        assertThat(location.getGeoNamesFeatureCode()).isEqualTo(featureCode);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
    }

    @Test
    public void addPrecisionWithGeoNamesFeatureCodeNotInOurMapping() {
        // Arrange
        Integer geoNameId = 123;
        String featureCode = "ISL";
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        Location location = new Location();
        GeoName geoName = new GeoName(geoNameId, featureCode);
        when(geoNamesWebService.getById(geoNameId)).thenReturn(geoName);

        // Act
        converter.addPrecision(healthMapLocation, location);

        // Assert
//        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
//        assertThat(location.getGeoNamesFeatureCode()).isEqualTo(featureCode);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
    }

    @Test
    public void addPrecisionWithGeoNameNotFound() {
        // Arrange
        Integer geoNameId = 123;
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setGeoNameId(geoNameId.toString());

        Location location = new Location();
        when(geoNamesWebService.getById(geoNameId)).thenReturn(null);

        // Act
        converter.addPrecision(healthMapLocation, location);

        // Assert
//        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
//        assertThat(location.getGeoNamesFeatureCode()).isNull();
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
    }

    @Test
    public void addPrecisionWithoutgeoNameId() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        Location location = new Location();

        // Act
        converter.addPrecision(healthMapLocation, location);

        // Assert
//        assertThat(location.getGeoNameId()).isNull();
//        assertThat(location.getGeoNamesFeatureCode()).isNull();
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
    }

    @Test
    public void addPrecisionWithoutgeoNameIdOrPlaceBasicType() {
        // Arrange
        HealthMapLocation healthMapLocation = createDefaultHealthMapLocation();
        healthMapLocation.setPlaceBasicType(null);
        Location location = new Location();

        // Act
        converter.addPrecision(healthMapLocation, location);

        // Assert
//        assertThat(location.getGeoNameId()).isNull();
//        assertThat(location.getGeoNamesFeatureCode()).isNull();
        assertThat(location.getPrecision()).isNull();
    }

    private HealthMapLocation createDefaultHealthMapLocation() {
        HealthMapLocation location = new HealthMapLocation();
        location.setLongitude("20");
        location.setLatitude("10");
        location.setCountryId(mappedHealthMapCountry.getId().toString());
        location.setCountry(MAPPED_COUNTRY_NAME);
        location.setPlaceBasicType("c");
        location.setPlaceName(MAPPED_COUNTRY_NAME);
        return location;
    }
}
