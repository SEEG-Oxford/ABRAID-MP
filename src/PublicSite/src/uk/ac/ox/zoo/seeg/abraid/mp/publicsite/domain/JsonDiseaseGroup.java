package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Represents a disease group for use in the Public Site JavaScript.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseGroup {
    private Integer id;
    private String name;
    private String publicName;
    private String shortName;
    private String abbreviation;
    private String groupType;
    private Boolean isGlobal;
    private JsonDiseaseGroup parentDiseaseGroup;
    private JsonValidatorDiseaseGroup validatorDiseaseGroup;
    private Double weighting;
    private Integer minNewOccurrencesTrigger;
    private boolean automaticModelRuns;

    public JsonDiseaseGroup() {
    }

    public JsonDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.id = diseaseGroup.getId();
        this.name = diseaseGroup.getName();
        this.publicName = diseaseGroup.getPublicName();
        this.shortName = diseaseGroup.getShortName();
        this.abbreviation = diseaseGroup.getAbbreviation();
        this.groupType = diseaseGroup.getGroupType().name();
        this.isGlobal = diseaseGroup.isGlobal();
        if (diseaseGroup.getParentGroup() != null) {
            this.parentDiseaseGroup = new JsonDiseaseGroup(diseaseGroup.getParentGroup());
        }
        if (diseaseGroup.getValidatorDiseaseGroup() != null) {
            this.validatorDiseaseGroup = new JsonValidatorDiseaseGroup(diseaseGroup.getValidatorDiseaseGroup());
        }
        this.weighting = diseaseGroup.getWeighting();
        this.minNewOccurrencesTrigger = diseaseGroup.getMinNewOccurrencesTrigger();
        this.automaticModelRuns = diseaseGroup.isAutomaticModelRunsEnabled();
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

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Boolean getIsGlobal() {
        return isGlobal;
    }

    public void setIsGlobal(Boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public JsonValidatorDiseaseGroup getValidatorDiseaseGroup() {
        return validatorDiseaseGroup;
    }

    public void setValidatorDiseaseGroup(JsonValidatorDiseaseGroup validatorDiseaseGroup) {
        this.validatorDiseaseGroup = validatorDiseaseGroup;
    }

    public JsonDiseaseGroup getParentDiseaseGroup() {
        return parentDiseaseGroup;
    }

    public void setParentDiseaseGroup(JsonDiseaseGroup parentDiseaseGroup) {
        this.parentDiseaseGroup = parentDiseaseGroup;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public Integer getMinNewOccurrencesTrigger() {
        return minNewOccurrencesTrigger;
    }

    public void setMinNewOccurrencesTrigger(Integer minNewOccurrencesTrigger) {
        this.minNewOccurrencesTrigger = minNewOccurrencesTrigger;
    }

    public boolean isAutomaticModelRuns() {
        return automaticModelRuns;
    }

    public void setAutomaticModelRuns(boolean automaticModelRuns) {
        this.automaticModelRuns = automaticModelRuns;
    }
}
