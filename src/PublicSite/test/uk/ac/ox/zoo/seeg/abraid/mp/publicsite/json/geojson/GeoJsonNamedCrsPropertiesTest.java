package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for GeoJsonNamedCrsProperties.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonNamedCrsPropertiesTest {
    @Test
    public void constructorForGeoJsonNamedCrsPropertiesBindsParametersCorrectly() throws Exception {
        // Arrange
        String expectedName = "foo";

        // Act
        GeoJsonNamedCrsProperties target = new GeoJsonNamedCrsProperties(expectedName);

        // Assert
        assertThat(target.getName()).isSameAs(expectedName);
    }
}
