package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonFeature.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonFeatureTest {
    @Test
    public void constructorForGeoJsonFeatureBindsParametersCorrectly() throws Exception {
        // Arrange
        Integer expectedId = 1;
        GeoJsonGeometry expectedGeometry = mock(GeoJsonGeometry.class);
        Object expectedProperties = mock(Object.class);
        GeoJsonCrs expectedCrs = mock(GeoJsonCrs.class);
        List<Double> expectedBBox = Arrays.asList(1.0, 2.0, 3.0, 4.0);

        // Act
        GeoJsonFeature target = new GeoJsonFeature(expectedId, expectedGeometry, expectedProperties, expectedCrs, expectedBBox) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getId()).isSameAs(expectedId);
        assertThat(target.getGeometry()).isSameAs(expectedGeometry);
        assertThat(target.getProperties()).isSameAs(expectedProperties);
        assertThat(target.getCrs()).isSameAs(expectedCrs);
        assertThat(target.getBBox()).isEqualTo(expectedBBox);
        assertThat(target.getType()).isSameAs(GeoJsonObjectType.FEATURE);
    }

    @Test
    public void constructorForGeoJsonFeatureAcceptsNullOptionalParameters() throws Exception {
        // Act
        GeoJsonFeature target = new GeoJsonFeature(null, mock(GeoJsonGeometry.class), mock(Object.class), null, null) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getId()).isNull();
        assertThat(target.getCrs()).isNull();
        assertThat(target.getBBox()).isNull();
    }
}
