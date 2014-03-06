package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by zool1112 on 06/03/14.
 */
public class  GeoJsonNamedEnumSerializer extends JsonSerializer<GeoJsonNamedEnum> {
    @Override
    public void serialize(GeoJsonNamedEnum value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(value.getGeoJsonName());
    }
}
