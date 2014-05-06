package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

/**
 * The JSON DTO used identify a disease when trigger model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelDisease {
    private int id;
    private boolean isTropical;
    private String name;
    private String abbreviation;

    public JsonModelDisease() {
    }

    public JsonModelDisease(int id, boolean isTropical, String name, String abbreviation) {
        setTropical(isTropical);
        setId(id);
        setName(name);
        setAbbreviation(abbreviation);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTropical() {
        return isTropical;
    }

    public void setTropical(boolean isTropical) {
        this.isTropical = isTropical;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @JsonIgnore
    public boolean isValid() {
        return !StringUtils.isEmpty(name) && !StringUtils.isEmpty(abbreviation);
    }

}
