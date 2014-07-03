package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonObject.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonObjectTest {
    @Test
    public void constructorForGeoJsonObjectBindsParametersCorrectly() throws Exception {
        // Arrange
        GeoJsonObjectType expectedType = GeoJsonObjectType.FEATURE;
        GeoJsonCrs expectedCrs = mock(GeoJsonCrs.class);
        List<Double> expectedBBox = Arrays.asList(1.0, 2.0, 3.0, 4.0);

        // Act
        GeoJsonObject target = new GeoJsonObject(expectedType, expectedCrs, expectedBBox) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getType()).isSameAs(expectedType);
        assertThat(target.getCrs()).isSameAs(expectedCrs);
        assertThat(target.getBBox()).isEqualTo(expectedBBox);

        assertThat(target.getBBox().getClass().getCanonicalName())
                .isEqualTo(Collections.unmodifiableList(expectedBBox).getClass().getCanonicalName());
    }

    @Test
    public void constructorForGeoJsonObjectAcceptsNullOptionalParameters() throws Exception {
        // Act
        GeoJsonObject target = new GeoJsonObject(GeoJsonObjectType.FEATURE, null, null) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getCrs()).isNull();
        assertThat(target.getBBox()).isNull();
    }
}
