package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

/**
 * Represents a validator disease group for use in the Public Site JavaScript.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonValidatorDiseaseGroup {
    private Integer id;
    private String name;

    public JsonValidatorDiseaseGroup() {
    }

    public JsonValidatorDiseaseGroup(ValidatorDiseaseGroup validatorDiseaseGroup) {
        this.id = validatorDiseaseGroup.getId();
        this.name = validatorDiseaseGroup.getName();
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
