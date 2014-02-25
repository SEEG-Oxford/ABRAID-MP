package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

/**
 * Parses JSON into the specified Java object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonParser {
    private ObjectMapper mapper;

    public JsonParser() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonParser(ObjectMapperConfigurer configurer) {
        this();
        configurer.configure(mapper);
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
