package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

/**
 * A JSON DTO used identify a HealthMap disease.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonHealthMapDisease extends JsonNamedEntry {
    private JsonNamedEntry abraidDisease;

    public JsonHealthMapDisease() {
    }

    public JsonHealthMapDisease(Integer id, String name, JsonNamedEntry abraidDisease) {
        super(id, name);
        setAbraidDisease(abraidDisease);
    }

    public JsonNamedEntry getAbraidDisease() {
        return abraidDisease;
    }

    public void setAbraidDisease(JsonNamedEntry abraidDisease) {
        this.abraidDisease = abraidDisease;
    }
}
