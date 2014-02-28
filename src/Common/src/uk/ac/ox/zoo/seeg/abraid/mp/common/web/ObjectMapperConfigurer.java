package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Interface allowing a JSON object mapper to be configured.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ObjectMapperConfigurer {
    /**
     * Configure the JSON object mapper.
     * @param mapper The object mapper.
     */
    void configure(ObjectMapper mapper);
}
