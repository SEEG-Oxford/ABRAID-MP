package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /**
     * Determines if the configuration object is valid.
     * @return The validity.
     */
    @JsonIgnore
    public boolean isValid() {
        return
                checkNameHasValue();
    }

    private boolean checkNameHasValue() {
        boolean valid = StringUtils.isNotEmpty(name);
        LOGGER.assertLog(valid, LOG_NAME_NOT_SPECIFIED);
        return valid;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonDisease that = (JsonDisease) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    ///COVERAGE:ON
    ///CHECKSTYLE:ON
}