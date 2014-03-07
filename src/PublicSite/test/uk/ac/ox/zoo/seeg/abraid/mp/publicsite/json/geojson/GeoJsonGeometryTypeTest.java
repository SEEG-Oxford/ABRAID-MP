package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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

    @Test
    public void serializationOfGeoJsonGeometryTypePrintsName() throws Exception {
        for(GeoJsonGeometryType enumValue : GeoJsonGeometryType.values()) {
            // Arrange
            OutputStream stream = new ByteArrayOutputStream();
            GeoJsonObjectMapper target = new GeoJsonObjectMapper();

            // Act
            target.writeValue(stream, enumValue);
            String result = stream.toString();

            // Assert
            assertThat(result).isEqualTo("\""+enumValue.getGeoJsonName()+"\"");
        }
    }

}
