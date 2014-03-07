package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for GeoJsonNamedCrs.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonNamedCrsTest {
    @Test
    public void constructorForGeoJsonNamedCrsBindsParametersCorrectly() throws Exception {
        // Arrange
        GeoJsonNamedCrsProperties expectedProperties = new GeoJsonNamedCrsProperties("foo");

        // Act
        GeoJsonNamedCrs target = new GeoJsonNamedCrs(expectedProperties);

        // Assert
        assertThat(target.getType()).isEqualTo("name");
        assertThat(target.getProperties()).isSameAs(expectedProperties);
    }

    @Test
    public void createEPSG4326FactoryMethodCreatesCorrectGeoJsonNamedCrs() throws Exception {
        // Act
        GeoJsonNamedCrs target = GeoJsonNamedCrs.createEPSG4326();

        // Assert
        assertThat(((GeoJsonNamedCrsProperties)target.getProperties()).getName()).isEqualTo("urn:ogc:def:crs:EPSG::4326");
    }
}
