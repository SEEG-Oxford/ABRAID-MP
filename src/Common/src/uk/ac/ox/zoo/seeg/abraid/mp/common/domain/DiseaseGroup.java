package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease
 * itself.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getDiseaseGroupByName",
                query = "from DiseaseGroup where name=:name"
        )
})
@Entity
@Table(name = "disease_group")
public class DiseaseGroup {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The parent disease group, or null if this is a top-level group (i.e. a cluster).
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private DiseaseGroup parentGroup;

    // The disease group name.
    @Column
    private String name;

    // The disease group type.
    @Column(name = "group_type")
    @Enumerated(EnumType.STRING)
    private DiseaseGroupType groupType;

    // The disease group public name (for display).
    @Column(name = "public_name")
    private String publicName;

    // The disease group short name (for display).
    @Column(name = "short_name")
    private String shortName;

    // A name allowing the disease groups to be grouped further, for display in DataValidator.
    @Column(name = "validator_set")
    private String validatorSet;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public DiseaseGroup() {
    }

    public DiseaseGroup(String name) {
        this.name = name;
    }

    public DiseaseGroup(Integer id) {
        this.id = id;
    }

    public DiseaseGroup(DiseaseGroup parentGroup, String name, DiseaseGroupType groupType) {
        this.parentGroup = parentGroup;
        this.name = name;
        this.groupType = groupType;
    }

    public DiseaseGroup(Integer id, DiseaseGroup parentGroup, String name, DiseaseGroupType groupType) {
        this.id = id;
        this.parentGroup = parentGroup;
        this.name = name;
        this.groupType = groupType;
    }

    public Integer getId() {
        return id;
    }

    public DiseaseGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(DiseaseGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiseaseGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(DiseaseGroupType groupType) {
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

    public String getDisplayName() {
        return (getShortName() != null ? getShortName() :
                (getPublicName() != null ? getPublicName() : getName()));
    }

    public String getValidatorSet() {
        return validatorSet;
    }

    public void setValidatorSet(String validatorSet) {
        this.validatorSet = validatorSet;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseGroup that = (DiseaseGroup) o;

        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (groupType != that.groupType) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parentGroup != null ? !parentGroup.equals(that.parentGroup) : that.parentGroup != null) return false;
        if (publicName != null ? !publicName.equals(that.publicName) : that.publicName != null) return false;
        if (shortName != null ? !shortName.equals(that.shortName) : that.shortName != null) return false;
        if (validatorSet != null ? !validatorSet.equals(that.validatorSet) : that.validatorSet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentGroup != null ? parentGroup.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (groupType != null ? groupType.hashCode() : 0);
        result = 31 * result + (publicName != null ? publicName.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (validatorSet != null ? validatorSet.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
}
