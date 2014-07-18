package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * A DTO for the parts of uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert provided on the account details page.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertDetails {
    private String name;
    private boolean isPubliclyVisible;
    private List<Integer> diseaseInterests;
    private String jobTitle;
    private String institution;

    public JsonExpertDetails() {
    }

    public JsonExpertDetails(Expert expert) {
        this.setName(expert.getName());
        this.setPubliclyVisible(expert.isPubliclyVisible());
        this.setJobTitle(expert.getJobTitle());
        this.setInstitution(expert.getInstitution());
        this.setDiseaseInterests(extract(expert.getValidatorDiseaseGroups(), on(ValidatorDiseaseGroup.class).getId()));
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

    public List<Integer> getDiseaseInterests() {
        return diseaseInterests;
    }

    public void setDiseaseInterests(List<Integer> diseaseInterests) {
        this.diseaseInterests = diseaseInterests;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
