package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a "sub-disease" as defined by HealthMap. This is specified in the comment field of a HealthMap alert.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "healthmap_subdisease")
@Immutable
public class HealthMapSubDisease {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The parent HealthMap disease.
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "healthmap_disease_id")
    private HealthMapDisease healthMapDisease;

    // The sub-disease name.
    @Column(nullable = false)
    private String name;

    // The corresponding disease group as defined by SEEG.
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "disease_group_id")
    private DiseaseGroup diseaseGroup;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public HealthMapSubDisease() {
    }

    public HealthMapSubDisease(HealthMapDisease healthMapDisease, String name, DiseaseGroup diseaseGroup) {
        this.healthMapDisease = healthMapDisease;
        this.name = name;
        this.diseaseGroup = diseaseGroup;
    }

    public Integer getId() {
        return id;
    }

    public HealthMapDisease getHealthMapDisease() {
        return healthMapDisease;
    }

    public String getName() {
        return name;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapSubDisease that = (HealthMapSubDisease) o;

        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (healthMapDisease != null ? !healthMapDisease.equals(that.healthMapDisease) : that.healthMapDisease != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (healthMapDisease != null ? healthMapDisease.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
