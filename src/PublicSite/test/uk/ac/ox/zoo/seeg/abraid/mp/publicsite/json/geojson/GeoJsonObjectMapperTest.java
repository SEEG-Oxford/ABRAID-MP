package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for GeoJsonObjectMapper.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonObjectMapperTest {
    @Test
    public void constructorForGeoJsonObjectMapperConfiguresJodaTimeSerialization() throws Exception {
        // Arrange
        OutputStream stream = new ByteArrayOutputStream();
        DateTime jodaTime = DateTime.now();

        // Act
        GeoJsonObjectMapper target = new GeoJsonObjectMapper();
        target.writeValue(stream, jodaTime);
        String result = stream.toString();

        // Assert
        assertThat(result).isEqualTo(jodaTime.toString("\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\""));
    }
}
