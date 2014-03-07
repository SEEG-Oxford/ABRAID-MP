package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonCrs.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonCrsTest {
    @Test
    public void constructorForGeoJsonCrsBindsParametersCorrectly() throws Exception {
        // Arrange
        String expectedType = "foo";
        Object expectedProperties = mock(Object.class);

        // Act
        GeoJsonCrs target = new GeoJsonCrs(expectedType, expectedProperties) {
            // Create anonymous subclass of abstract class to act as testing proxy
        };

        // Assert
        assertThat(target.getType()).isSameAs(expectedType);
        assertThat(target.getProperties()).isSameAs(expectedProperties);
    }
}
