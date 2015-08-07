package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    @Before
    public void setUp() {
        locationDao = mock(LocationDao.class);
        geoNamesLocationPrecisionDao = mock(GeoNamesLocationPrecisionDao.class);
        geoNameDao = mock(GeoNameDao.class);
        adminUnitDiseaseExtentClassDao = mock(AdminUnitDiseaseExtentClassDao.class);
        locationService = new LocationServiceImpl(locationDao, geoNamesLocationPrecisionDao, geoNameDao, adminUnitDiseaseExtentClassDao);
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

    @Test
    public void getAdminUnitDiseaseExtentClassesCallsDaoForNonCountry() {
        // Arrange
        Location location = mock(Location.class);
        when(location.getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(location.getAdminUnitGlobalGaulCode()).thenReturn(123);
        AdminUnitDiseaseExtentClass expected = mock(AdminUnitDiseaseExtentClass.class);
        when(adminUnitDiseaseExtentClassDao.getDiseaseExtentClassByGaulCode(87, true, 123)).thenReturn(expected);

        // Act
        List<AdminUnitDiseaseExtentClass> result = locationService.getAdminUnitDiseaseExtentClassesForLocation(87, true, location);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isSameAs(expected);
    }

    @Test
    public void getAdminUnitDiseaseExtentClassesCallsDaoForCountrySplit() {
        Location location = mock(Location.class);
        when(location.getPrecision()).thenReturn(LocationPrecision.COUNTRY);
        when(location.getCountryGaulCode()).thenReturn(321);
        when(location.getAdminUnitGlobalGaulCode()).thenReturn(123);
        List<AdminUnitDiseaseExtentClass> expected = Arrays.asList(mock(AdminUnitDiseaseExtentClass.class));
        when(adminUnitDiseaseExtentClassDao.getAllAdminUnitDiseaseExtentClassesByCountryGaulCode(87, true, 321)).thenReturn(expected);

        // Act
        List<AdminUnitDiseaseExtentClass> result = locationService.getAdminUnitDiseaseExtentClassesForLocation(87, true, location);

        // Assert
        assertThat(result).isSameAs(expected);
    }

    @Test
    public void getAdminUnitDiseaseExtentClassesCallsDaoForCountryNotSplit() {
        Location location = mock(Location.class);
        when(location.getPrecision()).thenReturn(LocationPrecision.COUNTRY);
        when(location.getCountryGaulCode()).thenReturn(321);
        when(location.getAdminUnitGlobalGaulCode()).thenReturn(123);
        AdminUnitDiseaseExtentClass expected = mock(AdminUnitDiseaseExtentClass.class);
        when(adminUnitDiseaseExtentClassDao.getDiseaseExtentClassByGaulCode(87, true, 123)).thenReturn(expected);
        when(adminUnitDiseaseExtentClassDao.getAllAdminUnitDiseaseExtentClassesByCountryGaulCode(87, true, 321)).thenReturn(new ArrayList<AdminUnitDiseaseExtentClass>());

        // Act
        List<AdminUnitDiseaseExtentClass> result = locationService.getAdminUnitDiseaseExtentClassesForLocation(87, true, location);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isSameAs(expected);
    }

    @Test
    public void saveLocationCallsDao() {
        Location location = mock(Location.class);

        // Act
        locationService.saveLocation(location);

        // Assert
        verify(locationDao).save(location);
    }
}
