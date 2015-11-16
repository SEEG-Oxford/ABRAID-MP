package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

/**
 * A base JSON dto to represent any object with an ID and name.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonNamedEntry {
    private Integer id;
    private String name;

    public JsonNamedEntry() {
    }

    public JsonNamedEntry(Integer id, String name) {
        setId(id);
        setName(name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
