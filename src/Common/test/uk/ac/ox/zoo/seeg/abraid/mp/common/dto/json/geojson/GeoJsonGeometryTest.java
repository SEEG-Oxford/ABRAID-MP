package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonGeometry.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonGeometryTest {
    @Test
    public void constructorForGeoJsonGeometryBindsParametersCorrectly() throws Exception {
        // Arrange
        GeoJsonGeometryType expectedType = GeoJsonGeometryType.POINT;
        GeoJsonCrs expectedCrs = mock(GeoJsonCrs.class);
        List<Double> expectedCoordinates = Arrays.asList(1.0, 2.0);
        List<Double> expectedBBox = Arrays.asList(1.0, 2.0, 3.0, 4.0);

        // Act
        GeoJsonGeometry target = new GeoJsonGeometry(expectedType, expectedCoordinates, expectedCrs, expectedBBox) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getType()).isSameAs(expectedType.getGeoJsonObjectType());
        assertThat(target.getCoordinates()).isEqualTo(expectedCoordinates);
        assertThat(target.getCrs()).isSameAs(expectedCrs);
        assertThat(target.getBBox()).isEqualTo(expectedBBox);
    }

    @Test
    public void constructorForGeoJsonGeometryAcceptsNullOptionalParameters() throws Exception {
        // Act
        GeoJsonGeometry target = new GeoJsonGeometry(GeoJsonGeometryType.POINT, Arrays.asList(1.0, 2.0), null, null) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getCrs()).isNull();
        assertThat(target.getBBox()).isNull();
    }
}
