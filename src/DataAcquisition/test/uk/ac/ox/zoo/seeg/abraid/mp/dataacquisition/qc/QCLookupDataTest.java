package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapLookupData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the QCLookupData class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCLookupDataTest {
    private LocationService locationService = mock(LocationService.class);
    private HealthMapLookupData healthMapLookupData = mock(HealthMapLookupData.class);

    @Test
    public void getAdminUnits() {
        // Arrange
        List<AdminUnitQC> expectedAdminUnits = new ArrayList<>();
        when(locationService.getAllAdminUnitQCs()).thenReturn(expectedAdminUnits);

        // Act
        QCLookupData lookupData = new QCLookupData(locationService, healthMapLookupData);
        List<AdminUnitQC> actualAdminUnits = lookupData.getAdminUnits();

        // Assert
        assertThat(actualAdminUnits).isEqualTo(expectedAdminUnits);
    }

    @Test
    public void getLandSeaBorders() {
        // Arrange
        MultiPolygon expectedMultiPolygon = GeometryUtils.createMultiPolygon(getTriangle(), getSquare());
        when(locationService.getAllLandSeaBorders()).thenReturn(getLandSeaBorderList());

        // Act
        QCLookupData lookupData = new QCLookupData(locationService, healthMapLookupData);
        MultiPolygon actualMultiPolygon = lookupData.getLandSeaBorders();

        // Assert
        assertThat(actualMultiPolygon.equalsExact(expectedMultiPolygon));
    }

    @Test
    public void getHealthMapCountryGeometryMap() {
        // Arrange
        when(healthMapLookupData.getCountryMap()).thenReturn(getCountryMap());
        MultiPolygon expectedGeometry1 = GeometryUtils.createMultiPolygon(getTriangle());
        MultiPolygon expectedGeometry2 = GeometryUtils.createMultiPolygon(getSquare(), getFivePointedPolygon());

        // Act
        QCLookupData lookupData = new QCLookupData(locationService, healthMapLookupData);
        Map<Long, MultiPolygon> healthMapCountryGeometryMap = lookupData.getHealthMapCountryGeometryMap();

        // Assert
        assertThat(healthMapCountryGeometryMap).hasSize(2);
        assertThat(healthMapCountryGeometryMap.get(1L)).isNotNull();
        assertThat(healthMapCountryGeometryMap.get(2L)).isNull();
        assertThat(healthMapCountryGeometryMap.get(3L)).isNotNull();
        assertThat(healthMapCountryGeometryMap.get(1L).equals(expectedGeometry1)).isTrue();
        assertThat(healthMapCountryGeometryMap.get(3L).equals(expectedGeometry2)).isTrue();
    }

    private Map<Long, HealthMapCountry> getCountryMap() {
        Country country1 = new Country(1, "Triangle", GeometryUtils.createMultiPolygon(getTriangle()));
        Country country2 = new Country(2, "Square", GeometryUtils.createMultiPolygon(getSquare()));
        Country country3 = new Country(3, "Five-pointed", GeometryUtils.createMultiPolygon(getFivePointedPolygon()));
        List<HealthMapCountry> healthMapCountries = Arrays.asList(
                new HealthMapCountry(1L, "HealthMap country 1", country1),
                new HealthMapCountry(2L, "HealthMap country 2"),
                new HealthMapCountry(3L, "HealthMap country 3", country2, country3));
        return index(healthMapCountries, on(HealthMapCountry.class).getId());
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
