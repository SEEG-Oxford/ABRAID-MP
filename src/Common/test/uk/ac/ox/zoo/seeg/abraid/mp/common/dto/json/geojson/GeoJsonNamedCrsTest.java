package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(target.getProperties().getName()).isEqualTo("urn:ogc:def:crs:EPSG::4326");
    }
}
