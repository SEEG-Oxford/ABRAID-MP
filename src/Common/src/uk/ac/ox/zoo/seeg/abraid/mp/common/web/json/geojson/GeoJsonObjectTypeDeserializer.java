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
public class GeoJsonObjectTypeDeserializer extends JsonDeserializer<GeoJsonObjectType> {
    @Override
    public GeoJsonObjectType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        String jsonValue = jsonParser.getText();
        for (GeoJsonObjectType enumValue : GeoJsonObjectType.values()) {
            if (enumValue.getGeoJsonName().equals(jsonValue)) {
                return enumValue;
            }
        }

        throw new IOException(jsonValue + " is not defined in GeoJsonObjectType");
    }
}
