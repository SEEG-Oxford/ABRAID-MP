package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonPointGeometry.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonPointGeometryTest {
    @Test
    public void constructorForGeoJsonPointGeometryBindsParametersCorrectly() throws Exception {
        // Arrange
        GeoJsonCrs expectedCrs = mock(GeoJsonCrs.class);
        List<Double> expectedBBox = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        double expectedLatitude = 1.0;
        double expectedLongitude = -1.0;

        // Act
        GeoJsonPointGeometry target = new GeoJsonPointGeometry(expectedLongitude, expectedLatitude, expectedCrs, expectedBBox);

        // Assert
        assertThat(target.getType()).isSameAs(GeoJsonGeometryType.POINT.getGeoJsonObjectType());
        assertThat(target.getCoordinates()).isEqualTo(new GeoJsonCoordinate(expectedLongitude, expectedLatitude));
        assertThat(target.getCrs()).isSameAs(expectedCrs);
        assertThat(target.getBBox()).isEqualTo(expectedBBox);
    }

    @Test
    public void constructorForGeoJsonPointGeometryAcceptsNullOptionalParameters() throws Exception {
        // Act
        GeoJsonPointGeometry target = new GeoJsonPointGeometry(1.0, -1.0, null, null);

        // Assert
        assertThat(target.getCrs()).isNull();
        assertThat(target.getBBox()).isNull();
    }
}
