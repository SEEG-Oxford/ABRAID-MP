package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the QCLookupData class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCLookupDataTest {
    private GeometryService geometryService = mock(GeometryService.class);

    @Test
    public void getAdminUnits() {
        // Arrange
        List<AdminUnitQC> expectedAdminUnits = new ArrayList<>();
        when(geometryService.getAllAdminUnitQCs()).thenReturn(expectedAdminUnits);

        // Act
        QCLookupData lookupData = new QCLookupData(geometryService);
        List<AdminUnitQC> actualAdminUnits = lookupData.getAdminUnits();

        // Assert
        assertThat(actualAdminUnits).isEqualTo(expectedAdminUnits);
    }

    @Test
    public void getAdminUnitsMap() {
        // Arrange
        List<AdminUnitQC> expectedAdminUnits = Arrays.asList(
                new AdminUnitQC(1, '1', "a", 0, 0, 0),
                new AdminUnitQC(3, '1', "b", 0, 0, 0),
                new AdminUnitQC(10, '1', "c", 0, 0, 0)
        );
        when(geometryService.getAllAdminUnitQCs()).thenReturn(expectedAdminUnits);

        // Act
        QCLookupData lookupData = new QCLookupData(geometryService);
        Map<Integer, AdminUnitQC> actualAdminUnits = lookupData.getAdminUnitsMap();

        // Assert
        assertThat(actualAdminUnits.get(1).getName()).isEqualTo("a");
        assertThat(actualAdminUnits.get(3).getName()).isEqualTo("b");
        assertThat(actualAdminUnits.get(10).getName()).isEqualTo("c");
    }

    @Test
    public void getCountryGeometryMap() {
        // Arrange
        List<Country> expectedCountries = new ArrayList<>(getCountries());
        when(geometryService.getAllCountries()).thenReturn(expectedCountries);

        // Act
        QCLookupData lookupData = new QCLookupData(geometryService);
        Map<Integer, MultiPolygon> actualCountries = lookupData.getCountryGeometryMap();

        // Assert
        assertThat(actualCountries).hasSize(3);
        assertThat(actualCountries.get(1)).isNotNull();
        assertThat(actualCountries.get(2)).isNotNull();
        assertThat(actualCountries.get(3)).isNotNull();
        assertThat(actualCountries.get(4)).isNull();
        assertThat(actualCountries.get(1).equals(GeometryUtils.createMultiPolygon(getTriangle()))).isTrue();
        assertThat(actualCountries.get(3).equals(GeometryUtils.createMultiPolygon(getFivePointedPolygon()))).isTrue();
    }

    @Test
    public void getCountryMap() {
        // Arrange
        List<Country> expectedCountries = new ArrayList<>(getCountries());
        when(geometryService.getAllCountries()).thenReturn(expectedCountries);

        // Act
        QCLookupData lookupData = new QCLookupData(geometryService);
        Map<Integer, Country> actualCountries = lookupData.getCountryMap();

        // Assert
        assertThat(actualCountries).hasSize(3);
        assertThat(actualCountries.get(1)).isNotNull();
        assertThat(actualCountries.get(2)).isNotNull();
        assertThat(actualCountries.get(3)).isNotNull();
        assertThat(actualCountries.get(4)).isNull();
        assertThat(actualCountries.get(1).getGeom().equals(GeometryUtils.createMultiPolygon(getTriangle()))).isTrue();
        assertThat(actualCountries.get(3).getGeom().equals(GeometryUtils.createMultiPolygon(getFivePointedPolygon()))).isTrue();
    }

    @Test
    public void getLandSeaBorders() {
        // Arrange
        MultiPolygon expectedMultiPolygon = GeometryUtils.createMultiPolygon(getTriangle(), getSquare());
        when(geometryService.getAllLandSeaBorders()).thenReturn(getLandSeaBorderList());

        // Act
        QCLookupData lookupData = new QCLookupData(geometryService);
        MultiPolygon actualMultiPolygon = lookupData.getLandSeaBorders();

        // Assert
        assertThat(actualMultiPolygon.equalsExact(expectedMultiPolygon));
    }

    @Test
    public void getHealthMapCountryGeometryMap() {
        // Arrange
        when(geometryService.getAllHealthMapCountries()).thenReturn(getHealthMapCountries());
        MultiPolygon expectedGeometry1 = GeometryUtils.createMultiPolygon(getTriangle());
        MultiPolygon expectedGeometry2 = GeometryUtils.createMultiPolygon(getSquare(), getFivePointedPolygon());

        // Act
        QCLookupData lookupData = new QCLookupData(geometryService);
        Map<Integer, MultiPolygon> healthMapCountryGeometryMap = lookupData.getHealthMapCountryGeometryMap();

        // Assert
        assertThat(healthMapCountryGeometryMap).hasSize(2);
        assertThat(healthMapCountryGeometryMap.get(1)).isNotNull();
        assertThat(healthMapCountryGeometryMap.get(2)).isNull();
        assertThat(healthMapCountryGeometryMap.get(3)).isNotNull();
        assertThat(healthMapCountryGeometryMap.get(1).equals(expectedGeometry1)).isTrue();
        assertThat(healthMapCountryGeometryMap.get(3).equals(expectedGeometry2)).isTrue();
    }

    private List<HealthMapCountry> getHealthMapCountries() {
        Country country1 = new Country(1, "Triangle", GeometryUtils.createMultiPolygon(getTriangle()));
        Country country2 = new Country(2, "Square", GeometryUtils.createMultiPolygon(getSquare()));
        Country country3 = new Country(3, "Five-pointed", GeometryUtils.createMultiPolygon(getFivePointedPolygon()));
        return Arrays.asList(
                new HealthMapCountry(1, "HealthMap country 1", country1),
                new HealthMapCountry(2, "HealthMap country 2"),
                new HealthMapCountry(3, "HealthMap country 3", country2, country3));
    }

    private List<Country> getCountries() {
        Country country1 = new Country(1, "Triangle", GeometryUtils.createMultiPolygon(getTriangle()));
        Country country2 = new Country(2, "Square", GeometryUtils.createMultiPolygon(getSquare()));
        Country country3 = new Country(3, "Five-pointed", GeometryUtils.createMultiPolygon(getFivePointedPolygon()));
        return Arrays.asList(country1, country2, country3);
    }

    private List<LandSeaBorder> getLandSeaBorderList() {
        return Arrays.asList(
                new LandSeaBorder(GeometryUtils.createMultiPolygon(getTriangle())),
                new LandSeaBorder(GeometryUtils.createMultiPolygon(getSquare()))
        );
    }

    private Polygon getTriangle() {
        return GeometryUtils.createPolygon(1, 1, 3, 2, 2, 3, 1, 1);
    }

    private Polygon getSquare() {
        return GeometryUtils.createPolygon(10, 10, 10, 20, 20, 20, 20, 10, 10, 10);
    }

    private Polygon getFivePointedPolygon() {
        return GeometryUtils.createPolygon(3, 4, 5, 11, 12, 8, 9, 5, 5, 6, 3, 4);
    }
}
