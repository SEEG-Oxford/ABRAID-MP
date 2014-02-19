package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.Arrays;
import java.util.List;

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
    public void getByPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        List<Location> locations = Arrays.asList(new Location());
        when(locationDao.getByPoint(point)).thenReturn(locations);

        // Act
        List<Location> testLocations = locationService.getLocationsByPoint(point);

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
}
