package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

/**
 * A JSON DTO used identify a HealthMap subdisease.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonHealthMapSubDisease extends JsonHealthMapDisease {
    private JsonNamedEntry parent;

    public JsonHealthMapSubDisease() {
    }

    public JsonHealthMapSubDisease(
            Integer id, String name, JsonNamedEntry abraidDisease, JsonNamedEntry parent) {
        super(id, name, abraidDisease);
        setParent(parent);
    }

    public JsonNamedEntry getParent() {
        return parent;
    }

    public void setParent(JsonNamedEntry parent) {
        this.parent = parent;
    }
}
