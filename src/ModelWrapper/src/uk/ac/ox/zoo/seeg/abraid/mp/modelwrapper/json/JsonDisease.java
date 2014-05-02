package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * A Json DTO for covariate disease configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDisease {
    private static final Logger LOGGER = Logger.getLogger(JsonDisease.class);
    private static final String LOG_NAME_NOT_SPECIFIED =
            "Configuration validation failure (disease): Name not specified.";

    private int id;
    private String name;

    public JsonDisease() {
    }

    public JsonDisease(int id, String name) {
        setId(id);
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        boolean valid = StringUtils.isNotEmpty(name);
        LOGGER.assertLog(!valid, LOG_NAME_NOT_SPECIFIED);

        return valid;
    }
}
