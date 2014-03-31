package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * A Jackson deserializer to correctly convert GeoJsonGeometryType enum values from the appropriate string.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonGeometryTypeDeserializer extends JsonDeserializer<GeoJsonGeometryType> {
    @Override
    public GeoJsonGeometryType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        String jsonValue = jsonParser.getText();
        for (GeoJsonGeometryType enumValue : GeoJsonGeometryType.values()) {
            if (enumValue.getGeoJsonName().equals(jsonValue)) {
                return enumValue;
            }
        }

        throw new IOException("No such value.");
    }
}
