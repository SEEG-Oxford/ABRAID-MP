package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertDetails {
    private String name;
    private boolean isPubliclyVisible;
    private List<Integer> validatorDiseaseGroups;
    private String job;
    private String institution;

    public JsonExpertDetails() {
    }

    public JsonExpertDetails(Expert expert) {
        this.setName(expert.getName());
        this.setPubliclyVisible(expert.isPubliclyVisible());
        this.setValidatorDiseaseGroups(extract(expert.getValidatorDiseaseGroups(), on(Expert.class).getId()));
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

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
