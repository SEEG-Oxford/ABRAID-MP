package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.ModellingJsonView;

/**
 * The JSON DTO used identify a disease when triggering model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelDisease {
    private int id;
    private boolean isGlobal;
    private String name;
    private String abbreviation;

    public JsonModelDisease() {
    }

    public JsonModelDisease(int id, boolean isGlobal, String name, String abbreviation) {
        setGlobal(isGlobal);
        setId(id);
        setName(name);
        setAbbreviation(abbreviation);
    }

    public JsonModelDisease(DiseaseGroup diseaseGroup) {
        this(diseaseGroup.getId(), diseaseGroup.isGlobal(), diseaseGroup.getName(), diseaseGroup.getAbbreviation());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    @JsonView(ModellingJsonView.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonView(ModellingJsonView.class)
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
