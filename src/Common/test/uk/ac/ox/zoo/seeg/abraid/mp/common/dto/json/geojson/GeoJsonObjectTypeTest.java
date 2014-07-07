package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void serializationOfGeoJsonObjectTypePrintsName() throws Exception {
        for (GeoJsonObjectType enumValue : GeoJsonObjectType.values()) {
            // Arrange
            OutputStream stream = new ByteArrayOutputStream();
            GeoJsonObjectMapper target = new GeoJsonObjectMapper();

            // Act
            target.writeValue(stream, enumValue);
            String result = stream.toString();

            // Assert
            assertThat(result).isEqualTo("\"" + enumValue.getGeoJsonName() + "\"");
        }
    }
}
