package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Jackson ObjectMapper configured for covariate configuration DTOs.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateObjectMapper extends ObjectMapper {
    public CovariateObjectMapper() {
        super();
        this.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }
}
