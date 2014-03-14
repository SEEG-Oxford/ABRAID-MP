package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class LocationServiceTest extends AbstractSpringUnitTests {
    @Autowired
    private LocationService locationService;
    
    @Test
    public void getByGeoNamesId() {
        // Arrange
        int geoNamesId = 1000;
        Location location = new Location();
        when(locationDao.getByGeoNamesId(geoNamesId)).thenReturn(location);

        // Act
        Location testLocation = locationService.getLocationByGeoNamesId(geoNamesId);

        // Assert
        assertThat(testLocation).isSameAs(location);
    }

    @Test
    public void getByPointAndPrecision() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        LocationPrecision precision = LocationPrecision.ADMIN1;
        List<Location> locations = Arrays.asList(new Location());
        when(locationDao.getByPointAndPrecision(point, precision)).thenReturn(locations);

        // Act
        List<Location> testLocations = locationService.getLocationsByPointAndPrecision(point, precision);

        // Assert
        assertThat(testLocations).isSameAs(locations);
    }

    @Test
    public void getAllCountries() {
        // Arrange
        List<Country> countries = Arrays.asList(new Country());
        when(countryDao.getAll()).thenReturn(countries);

        // Act
        List<Country> testCountries = locationService.getAllCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getAllHealthMapCountries() {
        // Arrange
        List<HealthMapCountry> countries = Arrays.asList(new HealthMapCountry());
        when(healthMapCountryDao.getAll()).thenReturn(countries);

        // Act
        List<HealthMapCountry> testCountries = locationService.getAllHealthMapCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getGeoNamesLocationPrecisionMappings() {
        // Arrange
        List<GeoNamesLocationPrecision> precisionList = new ArrayList<>();
        precisionList.add(new GeoNamesLocationPrecision("ADM1", LocationPrecision.ADMIN1));
        precisionList.add(new GeoNamesLocationPrecision("ADM2", LocationPrecision.ADMIN2));
        precisionList.add(new GeoNamesLocationPrecision("PPL", LocationPrecision.PRECISE));
        precisionList.add(new GeoNamesLocationPrecision("PCLI", LocationPrecision.COUNTRY));

        Map<String, LocationPrecision> precisionMap = new HashMap<>();
        precisionMap.put("ADM2", LocationPrecision.ADMIN2);
        precisionMap.put("PPL", LocationPrecision.PRECISE);
        precisionMap.put("ADM1", LocationPrecision.ADMIN1);
        precisionMap.put("PCLI", LocationPrecision.COUNTRY);

        when(geoNamesLocationPrecisionDao.getAll()).thenReturn(precisionList);

        // Act
        Map<String, LocationPrecision> testPrecisionMap = locationService.getGeoNamesLocationPrecisionMappings();

        // Assert
        assertThat(testPrecisionMap).isEqualTo(precisionMap);
    }
}
