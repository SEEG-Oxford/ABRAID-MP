package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for GeoJsonObjectType.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonObjectTypeTest {
    @Test
    public void mappingOfGeoJsonObjectTypeToNamesIsCorrect() throws Exception {
        assertThat(GeoJsonObjectType.FEATURE.getGeoJsonName()).isEqualTo("Feature");
        assertThat(GeoJsonObjectType.FEATURE_COLLECTION.getGeoJsonName()).isEqualTo("FeatureCollection");
        assertThat(GeoJsonObjectType.POINT.getGeoJsonName()).isEqualTo("Point");
    }
}
