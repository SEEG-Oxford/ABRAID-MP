package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;

import java.io.IOException;

/**
 * Parses JSON into the specified Java object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonParser {
    private AbraidJsonObjectMapper mapper;

    public JsonParser() {
        this(null);
    }

    public JsonParser(DateTimeFormatter dateTimeFormatter) {
        mapper = new AbraidJsonObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Register a module that parses Joda time types, with a custom deserializer for DateTime types that is aware
        // of the supplied date-time formatter (if any)
        JodaModule jodaModule = new JodaModule();
        if (dateTimeFormatter != null) {
            jodaModule.addDeserializer(DateTime.class,
                    JodaCustomDateTimeDeserializer.forType(DateTime.class, dateTimeFormatter));
        }

        mapper.registerModule(jodaModule);
    }

    /**
     * Parses JSON into the specified data structure.
     * @param json The JSON text.
     * @param responseClass The response class, i.e. the top-level class that the JSON should be parsed into.
     * @param <T> The response class.
     * @return The parsed JSON.
     * @throws JsonParserException If the JSON could not be parsed.
     */

    public <T> T parse(String json, Class<T> responseClass) throws JsonParserException {
        try {
            return mapper.readValue(json, responseClass);
        } catch (IOException e) {
            throw new JsonParserException(e.getMessage(), e);
        }
    }

    /**
     * Parses JSON into the data structure that is wrapped inside the specified TypeReference.
     * This wrapping allows generics to be passed in.
     * @param json The JSON text.
     * @param responseClass The response class, i.e. the top-level class that the JSON should be parsed into, wrapped
     * inside a TypeReference.
     * @param <T> The response class.
     * @return The parsed JSON.
     * @throws JsonParserException If the JSON could not be parsed.
     */
    public <T> T parse(String json, TypeReference<T> responseClass) throws JsonParserException {
        try {
            return mapper.readValue(json, responseClass);
        } catch (IOException e) {
            throw new JsonParserException(e.getMessage(), e);
        }
    }
}
