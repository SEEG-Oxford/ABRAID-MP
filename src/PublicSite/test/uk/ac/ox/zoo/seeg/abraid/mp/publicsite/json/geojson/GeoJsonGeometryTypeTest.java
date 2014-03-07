package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for GeoJsonGeometryType.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonGeometryTypeTest {
    @Test
    public void mappingOfGeoJsonGeometryTypeToNamesIsCorrect() throws Exception {
        assertThat(GeoJsonGeometryType.POINT.getGeoJsonName()).isEqualTo(GeoJsonObjectType.POINT.getGeoJsonName());
    }
    @Test
    public void mappingOfGeoJsonGeometryTypeToGeoJsonObjectTypeIsCorrect() throws Exception {
        assertThat(GeoJsonGeometryType.POINT.getGeoJsonObjectType()).isEqualTo(GeoJsonObjectType.POINT);
    }
}
