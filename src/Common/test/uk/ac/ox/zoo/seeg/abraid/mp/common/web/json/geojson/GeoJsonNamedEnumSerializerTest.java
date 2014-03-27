package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Tests for GeoJsonNamedEnumSerializer.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonNamedEnumSerializerTest {
    @Test
    public void serializeWritesCorrectStringToGenerator() throws Exception {
        // Arrange
        String expected = "foo";
        GeoJsonNamedEnum namedEnum = mock(GeoJsonNamedEnum.class);
        when(namedEnum.getGeoJsonName()).thenReturn(expected);
        GeoJsonNamedEnumSerializer target = new GeoJsonNamedEnumSerializer();
        JsonGenerator generator = mock(JsonGenerator.class);

        // Act
        target.serialize(namedEnum, generator, null);

        // Assert
        verify(namedEnum, times(1)).getGeoJsonName();
        verify(generator, times(1)).writeString(expected);
    }
}
