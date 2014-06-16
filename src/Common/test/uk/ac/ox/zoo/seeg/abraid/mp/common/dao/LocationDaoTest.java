package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the LocationDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LocationDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private LocationDao locationDao;

    @Test
    public void saveAndReloadCountryLocation() {
        // Arrange
        int healthMapCountryId = 1;
        String placeName = "Botswana";
        double x = -22.34284;
        double y = -24.6871;
        Point point = GeometryUtils.createPoint(x, y);

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.COUNTRY);
        location.setHealthMapCountryId(healthMapCountryId);

        // Act
        locationDao.save(location);

        // Assert
        assertThat(location.getCreatedDate()).isNotNull();
        Integer id = location.getId();
        flushAndClear();

        location = locationDao.getById(id);
        assertThat(location).isNotNull();
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
        assertThat(location.getHealthMapCountryId()).isEqualTo(healthMapCountryId);
        assertThat(location.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadAdmin1LocationByGeoNameId() {
        // Arrange
        int healthMapCountryId = 2;
        String placeName = "England";
        double x = 52.88496;
        double y = -1.97703;
        Point point = GeometryUtils.createPoint(x, y);
        int geoNameId = 6269131;

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.ADMIN1);
        location.setHealthMapCountryId(healthMapCountryId);
        location.setGeoNameId(geoNameId);

        // Act
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Assert
        location = locationDao.getByGeoNameId(geoNameId);
        assertThat(location).isNotNull();
        assertThat(location.getId()).isEqualTo(id);
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
        assertThat(location.getHealthMapCountryId()).isEqualTo(healthMapCountryId);
        assertThat(location.getCreatedDate()).isNotNull();
        assertThat(location.getGeoNameId()).isEqualTo(geoNameId);
    }

    @Test
    public void saveAndReloadAdmin2Location() {
        // Arrange
        String placeName = "Oxfordshire";
        double x = 51.81394;
        double y = -1.29479;
        Point point = GeometryUtils.createPoint(x, y);
        int adminUnitQCGaulCode = 29863;
        int adminUnitGlobalGaulCode = 3;
        int adminUnitTropicalGaulCode = 4;
        int countryGaulCode = 5;

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.ADMIN2);
        location.setAdminUnitQCGaulCode(adminUnitQCGaulCode);
        location.setAdminUnitGlobalGaulCode(adminUnitGlobalGaulCode);
        location.setAdminUnitTropicalGaulCode(adminUnitTropicalGaulCode);
        location.setHasPassedQc(true);

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
        assertThat(location.getHealthMapCountryId()).isNull();
        assertThat(location.getCreatedDate()).isNotNull();
        assertThat(location.getAdminUnitQCGaulCode()).isEqualTo(adminUnitQCGaulCode);
        assertThat(location.getAdminUnitGlobalGaulCode()).isEqualTo(adminUnitGlobalGaulCode);
        assertThat(location.getAdminUnitTropicalGaulCode()).isEqualTo(adminUnitTropicalGaulCode);
        assertThat(location.hasPassedQc()).isTrue();
    }

    @Test
    public void saveAndReloadPreciseLocationByPoint() {
        // Arrange
        int healthMapCountryId = 3;
        String placeName = "Oxford";
        double x = 51.75042;
        double y = -1.24759;
        Point point = GeometryUtils.createPoint(x, y);
        LocationPrecision precision = LocationPrecision.PRECISE;

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(precision);
        location.setHealthMapCountryId(healthMapCountryId);

        // Act
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Assert
        List<Location> locations = locationDao.getByPointAndPrecision(point, precision);
        assertThat(locations).hasSize(1);
        location = locations.get(0);
        assertThat(location).isNotNull();
        assertThat(location.getId()).isEqualTo(id);
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(x);
        assertThat(location.getGeom().getY()).isEqualTo(y);
        assertThat(location.getName()).isEqualTo(placeName);
        assertThat(location.getPrecision()).isEqualTo(precision);
        assertThat(location.getHealthMapCountryId()).isEqualTo(healthMapCountryId);
        assertThat(location.getCreatedDate()).isNotNull();
        assertThat(location.hasPassedQc()).isFalse();
    }

    @Test
    public void loadNonExistentLocationById() {
        Location location = locationDao.getById(-1);
        assertThat(location).isNull();
    }

    @Test
    public void loadNonExistentLocationByPointAndPrecision() {
        Point point = GeometryUtils.createPoint(-70, 60);
        LocationPrecision precision = LocationPrecision.COUNTRY;
        List<Location> locations = locationDao.getByPointAndPrecision(point, precision);
        assertThat(locations).hasSize(0);
    }

    @Test
    public void getAllLocations() {
        List<Location> locations = locationDao.getAll();
        assertThat(locations).hasSize(54);
    }
}
