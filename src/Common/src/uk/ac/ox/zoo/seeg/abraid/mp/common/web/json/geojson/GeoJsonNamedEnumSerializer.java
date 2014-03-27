package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * A Jackson serializer to correctly convert enum values to the appropriate string.
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonNamedEnumSerializer extends JsonSerializer<GeoJsonNamedEnum> {
    @Override
    public void serialize(GeoJsonNamedEnum value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(value.getGeoJsonName());
    }
}
