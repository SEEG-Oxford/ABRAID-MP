package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

/**
 * A Json DTO for covariate disease configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDisease {
    private int id;
    private String name;

    public JsonDisease(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public JsonDisease() {
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
}
