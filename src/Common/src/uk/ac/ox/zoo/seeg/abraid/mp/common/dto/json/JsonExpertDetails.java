package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertDetails {
    private String name;
    private boolean isPubliclyVisible;
    private List<Integer> validatorDiseaseGroups;

    public JsonExpertDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPubliclyVisible() {
        return isPubliclyVisible;
    }

    public void setPubliclyVisible(boolean isPubliclyVisible) {
        this.isPubliclyVisible = isPubliclyVisible;
    }

    public List<Integer> getValidatorDiseaseGroups() {
        return validatorDiseaseGroups;
    }

    public void setValidatorDiseaseGroups(List<Integer> validatorDiseaseGroups) {
        this.validatorDiseaseGroups = validatorDiseaseGroups;
    }
}
