package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * Represents a disease as defined by HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "healthmap_disease")
public class HealthMapDisease {
    private static final String NAMES_FOR_DISPLAY_FORMAT = "%s [sub-name: %s]";

    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease name.
    @Column(nullable = false)
    private String name;

    // The name of any subdisease, or null if none.
    @Column(name = "sub_name")
    private String subName;

    // The disease ID from HealthMap.
    @Column(name = "healthmap_disease_id")
    private Integer healthMapDiseaseId;

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

    public HealthMapDisease() {
    }

    public HealthMapDisease(Integer healthMapDiseaseId, String name, String subName, DiseaseGroup diseaseGroup) {
        this.healthMapDiseaseId = healthMapDiseaseId;
        this.name = name;
        this.subName = subName;
        this.diseaseGroup = diseaseGroup;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getNamesForDisplay() {
        return StringUtils.hasText(subName) ? String.format(NAMES_FOR_DISPLAY_FORMAT, name, subName) : name;
    }

    public Integer getHealthMapDiseaseId() {
        return healthMapDiseaseId;
    }

    public void setHealthMapDiseaseId(Integer healthMapDiseaseId) {
        this.healthMapDiseaseId = healthMapDiseaseId;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
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

        HealthMapDisease that = (HealthMapDisease) o;

        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (healthMapDiseaseId != null ? !healthMapDiseaseId.equals(that.healthMapDiseaseId) : that.healthMapDiseaseId != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (subName != null ? !subName.equals(that.subName) : that.subName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (subName != null ? subName.hashCode() : 0);
        result = 31 * result + (healthMapDiseaseId != null ? healthMapDiseaseId.hashCode() : 0);
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
