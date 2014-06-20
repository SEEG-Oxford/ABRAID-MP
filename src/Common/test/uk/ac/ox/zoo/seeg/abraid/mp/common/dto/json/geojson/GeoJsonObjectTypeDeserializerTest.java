package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import com.fasterxml.jackson.core.JsonParser;
import org.junit.Test;

import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GeoJsonObjectTypeDeserializer.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonObjectTypeDeserializerTest {
    @Test
    public void deserializeReturnsValueForCorrectStrings() throws Exception {
        // Arrange
        GeoJsonObjectTypeDeserializer target = new GeoJsonObjectTypeDeserializer();
        JsonParser mockParser = mock(JsonParser.class);

        for (GeoJsonObjectType expectation : GeoJsonObjectType.values()) {
            when(mockParser.getText()).thenReturn(expectation.getGeoJsonName());

            // Act
            GeoJsonObjectType result = target.deserialize(mockParser, null);

            // Assert
            assertThat(result).isEqualTo(expectation);
        }
    }

    @Test
    public void deserializeThrowsForInvalidString() throws Exception {
        // Arrange
        GeoJsonObjectTypeDeserializer target = new GeoJsonObjectTypeDeserializer();
        JsonParser mockParser = mock(JsonParser.class);
        when(mockParser.getText()).thenReturn("foo");

        // Act
        catchException(target).deserialize(mockParser, null);

        // Assert
        assertThat(caughtException()).isInstanceOf(IOException.class);
    }
}
