package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Represents a disease as defined by HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class HealthMapDisease {
    // The disease ID from HealthMap.
    @Id
    private Long id;

    // The disease name.
    @Column
    private String name;

    // Whether or not the disease is of interest. If so, disease alerts will be retrieved from HealthMap.
    @Column
    private boolean isOfInterest;

    // The corresponding disease group as defined by SEEG.
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "diseaseGroup")
    private DiseaseGroup diseaseGroup;

    // The database row creation date.
    @Column
    private Date createdDate;

    public HealthMapDisease() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOfInterest() {
        return isOfInterest;
    }

    public void setOfInterest(boolean isOfInterest) {
        this.isOfInterest = isOfInterest;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapDisease that = (HealthMapDisease) o;

        if (isOfInterest != that.isOfInterest) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isOfInterest ? 1 : 0);
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
}
