package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Represents a parent disease group for use in the Public Site JavaScript.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonParentDiseaseGroup {
    private Integer id;
    private String name;

    public JsonParentDiseaseGroup() {
    }

    public JsonParentDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.id = diseaseGroup.getId();
        this.name = diseaseGroup.getName();
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
