package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonFeatureCollection.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonFeatureCollectionTest {
    @Test
    public void constructorForGeoJsonFeatureCollectionBindsParametersCorrectly() throws Exception {
        // Arrange
        List<GeoJsonFeature> expectedFeatures = Arrays.asList(mock(GeoJsonFeature.class), mock(GeoJsonFeature.class));
        GeoJsonCrs expectedCrs = mock(GeoJsonCrs.class);
        List<Double> expectedBBox = Arrays.asList(1.0, 2.0, 3.0, 4.0);

        // Act
        GeoJsonFeatureCollection target = new GeoJsonFeatureCollection(expectedFeatures, expectedCrs, expectedBBox) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getFeatures()).isEqualTo(expectedFeatures);
        assertThat(target.getCrs()).isSameAs(expectedCrs);
        assertThat(target.getBBox()).isEqualTo(expectedBBox);
        assertThat(target.getType()).isSameAs(GeoJsonObjectType.FEATURE_COLLECTION);

        assertThat(target.getFeatures().getClass().getCanonicalName())
                .isEqualTo(Collections.unmodifiableList(expectedFeatures).getClass().getCanonicalName());
    }

    @Test
    public void constructorForGeoJsonFeatureCollectionAcceptsNullOptionalParameters() throws Exception {
        // Act
        GeoJsonFeatureCollection target = new GeoJsonFeatureCollection(
                Arrays.asList(mock(GeoJsonFeature.class), mock(GeoJsonFeature.class)), null, null) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getCrs()).isNull();
        assertThat(target.getBBox()).isNull();
    }
}
