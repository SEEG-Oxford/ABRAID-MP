package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the LocationDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LocationDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private LocationDao locationDao;
    @Autowired
    private CountryDao countryDao;

    @Test
    public void saveAndReloadCountryLocation() {
        // Arrange
        String countryName = "Botswana";
        String placeName = "Botswana";
        Country country = countryDao.getByName(countryName);
        double x = -22.34284;
        double y = -24.6871;
        Point point = GeometryUtils.createPoint(x, y);

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.COUNTRY);
        location.setCountry(country);

        // Act
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Assert
        location = locationDao.getById(id);
        assertThat(location).isNotNull();
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
        assertThat(location.getCountry()).isNotNull();
        assertThat(location.getCountry().getName()).isEqualTo(countryName);
        assertThat(location.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadAdmin1LocationByGeoNamesId() {
        // Arrange
        String countryName = "UK of Great Britain and Northern Ireland";
        String placeName = "England";
        String admin1 = "England";
        Country country = countryDao.getByName(countryName);
        double x = 52.88496;
        double y = -1.97703;
        Point point = GeometryUtils.createPoint(x, y);
        int geoNamesId = 6269131;

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.ADMIN1);
        location.setAdmin1(admin1);
        location.setCountry(country);
        location.setGeoNamesId(geoNamesId);

        // Act
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Assert
        location = locationDao.getByGeoNamesId(geoNamesId);
        assertThat(location).isNotNull();
        assertThat(location.getId()).isEqualTo(id);
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
        assertThat(location.getCountry()).isNotNull();
        assertThat(location.getAdmin1()).isEqualTo(admin1);
        assertThat(location.getAdmin2()).isNull();
        assertThat(location.getCountry().getName()).isEqualTo(countryName);
        assertThat(location.getCreatedDate()).isNotNull();
        assertThat(location.getGeoNamesId()).isEqualTo(geoNamesId);
    }

    @Test
    public void saveAndReloadAdmin2Location() {
        // Arrange
        String countryName = "UK of Great Britain and Northern Ireland";
        String placeName = "Oxfordshire";
        String admin1 = "England";
        String admin2 = "Oxfordshire";
        Country country = countryDao.getByName(countryName);
        double x = 51.81394;
        double y = -1.29479;
        Point point = GeometryUtils.createPoint(x, y);

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.ADMIN2);
        location.setAdmin1(admin1);
        location.setAdmin2(admin2);
        location.setCountry(country);

        // Act
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Assert
        location = locationDao.getById(id);
        assertThat(location).isNotNull();
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN2);
        assertThat(location.getCountry()).isNotNull();
        assertThat(location.getAdmin1()).isEqualTo(admin1);
        assertThat(location.getAdmin2()).isEqualTo(admin2);
        assertThat(location.getCountry().getName()).isEqualTo(countryName);
        assertThat(location.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadPreciseLocationByPoint() {
        // Arrange
        String countryName = "UK of Great Britain and Northern Ireland";
        String placeName = "Oxford";
        String admin1 = "England";
        String admin2 = "Oxfordshire";
        Country country = countryDao.getByName(countryName);
        double x = 51.75042;
        double y = -1.24759;
        Point point = GeometryUtils.createPoint(x, y);

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.PRECISE);
        location.setAdmin1(admin1);
        location.setAdmin2(admin2);
        location.setCountry(country);

        // Act
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Assert
        List<Location> locations = locationDao.getByPoint(point);
        assertThat(locations).hasSize(1);
        location = locations.get(0);
        assertThat(location).isNotNull();
        assertThat(location.getId()).isEqualTo(id);
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        assertThat(location.getCountry()).isNotNull();
        assertThat(location.getAdmin1()).isEqualTo(admin1);
        assertThat(location.getAdmin2()).isEqualTo(admin2);
        assertThat(location.getCountry().getName()).isEqualTo(countryName);
        assertThat(location.getCreatedDate()).isNotNull();
    }

    @Test
    public void loadNonExistentLocationById() {
        Location location = locationDao.getById(-1);
        assertThat(location).isNull();
    }

    @Test
    public void loadNonExistentLocationByPoint() {
        Point point = GeometryUtils.createPoint(-70, 60);
        List<Location> locations = locationDao.getByPoint(point);
        assertThat(locations).hasSize(0);
    }

    @Test
    public void getAllLocations() {
        List<Location> locations = locationDao.getAll();
        assertThat(locations).hasSize(0);
    }
}
