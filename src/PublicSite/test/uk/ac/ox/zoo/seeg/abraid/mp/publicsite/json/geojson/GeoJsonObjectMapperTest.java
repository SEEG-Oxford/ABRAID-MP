package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for GeoJsonObjectMapper.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonObjectMapperTest {
    @Test
    public void constructorForGeoJsonObjectMapperConfiguresJodaTimeSerialization() throws Exception {
        // Arrange
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        OutputStream stream = new ByteArrayOutputStream();
        DateTime jodaTime = new DateTime(0);

        // Act
        GeoJsonObjectMapper target = new GeoJsonObjectMapper();
        target.writeValue(stream, jodaTime);
        String result = stream.toString();

        // Assert
        assertThat(result).isEqualTo("\"" + ISODateTimeFormat.dateTime().print(jodaTime) + "\"");
    }
}
