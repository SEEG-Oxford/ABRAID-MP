package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
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
        String countryName = "Mauritius";
        String placeName = "Point d'Esny";
        Country country = countryDao.getByName(countryName);

        Location location = new Location();
        location.setCountry(country);
        location.setPlaceName(placeName);
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Reloads the same entity and verifies its properties
        location = locationDao.getById(id);
        assertThat(location).isNotNull();
        assertThat(location.getCountry()).isNotNull();
        assertThat(location.getCountry().getName()).isEqualTo(countryName);
    }

    // TODO: Test for Admin 1 not yet added because this area of the schema is very likely to change

    @Test
    public void saveAndReloadPreciseLocation() {
        double oxfordX = -1.24759;
        double oxfordY = 51.75042;
        Point point = GeometryUtils.createPoint(oxfordX, oxfordY);

        Location location = new Location();
        location.setGeom(point);
        locationDao.save(location);
        Integer id = location.getId();
        flushAndClear();

        // Reloads the same entity and verifies its properties
        location = locationDao.getById(id);
        assertThat(location).isNotNull();
        assertThat(location.getGeom()).isNotNull();
        assertThat(location.getGeom().getX()).isEqualTo(oxfordX);
        assertThat(location.getGeom().getY()).isEqualTo(oxfordY);
    }

    @Test
    public void loadNonExistentLocation() {
        Location location = locationDao.getById(-1);
        assertThat(location).isNull();
    }

    @Test
    public void getAllLocations() {
        List<Location> locations = locationDao.getAll();
        assertThat(locations).hasSize(0);
    }
}
