package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

/**
 * JSON DTO for Experts (used in administering experts).
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertFull extends JsonExpertDetails {
    private Integer id;
    private String email;
    private Double weighting;
    private Boolean visibilityApproved;
    private DateTime createdDate;
    private DateTime updatedDate;
    private Boolean isAdministrator;
    private Boolean isSEEGMember;

    public JsonExpertFull(){
        super();
    }
    
    public JsonExpertFull(Expert expert) {
        super(expert);
        setId(expert.getId());
        setEmail(expert.getEmail());
        setWeighting(expert.getWeighting() == null ? 0.5 : expert.getWeighting()); // TMP
        setVisibilityApproved(false); //TMP
        setCreateDate(expert.getCreatedDate());
        setUpdatedDate(expert.getCreatedDate()); //TMP
        setAdministrator(expert.isAdministrator());
        setSEEGMember(false); // TMP

        setDiseaseInterests(null); // Don't serialize
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public Boolean getVisibilityApproved() {
        return visibilityApproved;
    }

    public void setVisibilityApproved(Boolean visibilityApproved) {
        this.visibilityApproved = visibilityApproved;
    }

    public Boolean isAdministrator() {
        return isAdministrator;
    }

    public void setAdministrator(Boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public Boolean isSEEGMember() {
        return isSEEGMember;
    }

    public void setSEEGMember(Boolean isSEEGMember) {
        this.isSEEGMember = isSEEGMember;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreateDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public DateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(DateTime createdDate) {
        this.updatedDate = createdDate;
    }
}
