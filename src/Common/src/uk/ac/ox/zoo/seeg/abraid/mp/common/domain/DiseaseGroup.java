package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease
 * itself.
 *
 * Copyright (c) 2014 University of Oxford
 */
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
    @Column(nullable = false)
    private String name;

    // The disease group type.
    @Column(name = "group_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiseaseGroupType groupType;

    // The disease group public name (for display).
    @Column(name = "public_name")
    private String publicName;

    // The disease group short name (for display).
    @Column(name = "short_name")
    private String shortName;

    // The disease group abbreviated name.
    private String abbreviation;

    // True if the disease group is global, false if tropical, null if unknown.
    @Column(name = "is_global")
    private Boolean isGlobal;

    // A link to a further grouping of diseases for use by experts in the Data Validator.
    @ManyToOne
    @JoinColumn(name = "validator_disease_group_id")
    private ValidatorDiseaseGroup validatorDiseaseGroup;

    // The weighting, initially determined by group type.
    @Column
    private Double weighting;

    // The date on which the weightings were last updated, in preparation for a model run.
    @Column(name = "last_model_run_prep_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastModelRunPrepDate;

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

    public DiseaseGroup(String name, ValidatorDiseaseGroup validatorDiseaseGroup) {
        this.name = name;
        this.validatorDiseaseGroup = validatorDiseaseGroup;
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

    public String getPublicName() { return publicName; }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    /**
     * Gets the disease group's public name for display, if it exists.
     * @return The disease group's public name for display.
     */
    public String getPublicNameForDisplay() {
        if (StringUtils.hasText(getPublicName())) {
            return getPublicName();
        }
        return getName();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the disease group's short name for display, if it exists.
     * This should only be used on the Data Validation page's layer selector dropdown menu.
     * @return The disease group's short name for display.
     */
    public String getShortNameForDisplay() {
        if (StringUtils.hasText(getShortName())) {
            return getShortName();
        }
        return getName();
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(Boolean isGlobal) {
        this.isGlobal = isGlobal;
    }
    public ValidatorDiseaseGroup getValidatorDiseaseGroup() {
        return validatorDiseaseGroup;
    }

    public void setValidatorDiseaseGroup(ValidatorDiseaseGroup validatorDiseaseGroup) {
        this.validatorDiseaseGroup = validatorDiseaseGroup;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public DateTime getLastModelRunPrepDate() {
        return lastModelRunPrepDate;
    }

    public void setLastModelRunPrepDate(DateTime lastModelRunPrepDate) {
        this.lastModelRunPrepDate = lastModelRunPrepDate;
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

        DiseaseGroup that = (DiseaseGroup) o;

        if (abbreviation != null ? !abbreviation.equals(that.abbreviation) : that.abbreviation != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (groupType != that.groupType) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (isGlobal != null ? !isGlobal.equals(that.isGlobal) : that.isGlobal != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parentGroup != null ? !parentGroup.equals(that.parentGroup) : that.parentGroup != null) return false;
        if (publicName != null ? !publicName.equals(that.publicName) : that.publicName != null) return false;
        if (shortName != null ? !shortName.equals(that.shortName) : that.shortName != null) return false;
        if (validatorDiseaseGroup != null ? !validatorDiseaseGroup.equals(that.validatorDiseaseGroup) : that.validatorDiseaseGroup != null)
            return false;

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
        result = 31 * result + (abbreviation != null ? abbreviation.hashCode() : 0);
        result = 31 * result + (isGlobal != null ? isGlobal.hashCode() : 0);
        result = 31 * result + (validatorDiseaseGroup != null ? validatorDiseaseGroup.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
