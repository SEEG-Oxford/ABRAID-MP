package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for LocationServiceImpl.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LocationServiceTest {
    private LocationService locationService;
    private LocationDao locationDao;
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;
    private GeoNameDao geoNameDao;

    @Before
    public void setUp() {
        locationDao = mock(LocationDao.class);
        geoNamesLocationPrecisionDao = mock(GeoNamesLocationPrecisionDao.class);
        geoNameDao = mock(GeoNameDao.class);
        locationService = new LocationServiceImpl(locationDao, geoNamesLocationPrecisionDao, geoNameDao);
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

    @Test
    public void getGeoNameById() {
        // Arrange
        GeoName expectation = mock(GeoName.class);
        when(geoNameDao.getById(123)).thenReturn(expectation);

        // Act
        GeoName actual = locationService.getGeoNameById(123);

        // Assert
        assertThat(actual).isEqualTo(expectation);
    }

    @Test
    public void saveGeoName() {
        // Arrange
        GeoName expectation = mock(GeoName.class);

        // Act
        locationService.saveGeoName(expectation);

        // Assert
        verify(geoNameDao).save(expectation);
    }
}
