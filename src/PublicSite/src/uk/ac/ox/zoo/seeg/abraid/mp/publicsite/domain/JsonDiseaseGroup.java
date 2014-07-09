package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Represents a disease group for use in the Public Site JavaScript.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseGroup {
    private Integer id;
    private Integer parentId;
    private String name;
    private String groupType;
    private String publicName;
    private String shortName;
    private String abbreviation;
    private Boolean isGlobal;
    private Integer validatorDiseaseGroupId;
    private Double weighting;
    private Integer modelRunMinNewOccurrences;
    private boolean automaticModelRuns;

    public JsonDiseaseGroup() {
    }

    public JsonDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.id = diseaseGroup.getId();
        if (diseaseGroup.getParentGroup() != null) {
            this.parentId = diseaseGroup.getParentGroup().getId();
        }
        this.name = diseaseGroup.getName();
        this.groupType = diseaseGroup.getGroupType().name();
        this.publicName = diseaseGroup.getPublicName();
        this.shortName = diseaseGroup.getShortName();
        this.abbreviation = diseaseGroup.getAbbreviation();
        this.isGlobal = diseaseGroup.isGlobal();
        if (diseaseGroup.getValidatorDiseaseGroup() != null) {
            this.validatorDiseaseGroupId = diseaseGroup.getValidatorDiseaseGroup().getId();
        }
        this.weighting = diseaseGroup.getWeighting();
        this.modelRunMinNewOccurrences = diseaseGroup.getModelRunMinNewOccurrences();
        this.automaticModelRuns = diseaseGroup.isAutomaticModelRunsEnabled();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public Integer getValidatorDiseaseGroupId() {
        return validatorDiseaseGroupId;
    }

    public void setValidatorDiseaseGroupId(Integer validatorDiseaseGroupId) {
        this.validatorDiseaseGroupId = validatorDiseaseGroupId;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public Integer getModelRunMinNewOccurrences() {
        return modelRunMinNewOccurrences;
    }

    public void setModelRunMinNewOccurrences(Integer modelRunMinNewOccurrences) {
        this.modelRunMinNewOccurrences = modelRunMinNewOccurrences;
    }

    public boolean isAutomaticModelRuns() {
        return automaticModelRuns;
    }

    public void setAutomaticModelRuns(boolean automaticModelRuns) {
        this.automaticModelRuns = automaticModelRuns;
    }
}
