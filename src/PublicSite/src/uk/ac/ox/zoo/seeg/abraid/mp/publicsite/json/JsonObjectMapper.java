package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * A custom Jackson object mapper to ensure the JSON produced is GeoJSON compliant.
 * Copyright (c) 2014 University of Oxford
 */
public final class JsonObjectMapper extends ObjectMapper {
    public JsonObjectMapper() {
        super();
        this.registerModule(new JodaModule());
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
