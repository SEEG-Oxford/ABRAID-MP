package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents the extent class (e.g. presence, absence) of a disease group across an administrative unit.
 * The admin unit will either be AdminUnitGlobal or AdminUnitTropical, depending on the property of the DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId",
                query = "from AdminUnitDiseaseExtentClass a " +
                        "inner join fetch a.adminUnitGlobal " +
                        "where a.diseaseGroup.id=:diseaseGroupId"
        ),
        @NamedQuery(
                name = "getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId",
                query = "from AdminUnitDiseaseExtentClass a " +
                        "inner join fetch a.adminUnitTropical " +
                        "where a.diseaseGroup.id=:diseaseGroupId"
        )
})
@Entity
@Table(name = "admin_unit_disease_extent_class")
public class AdminUnitDiseaseExtentClass {
    // The id of the DiseaseGroup-AdminUnit pairing's class.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The global administrative unit.
    @ManyToOne
    @JoinColumn(name = "global_gaul_code")
    private AdminUnitGlobal adminUnitGlobal;

    // The tropical administrative unit.
    @ManyToOne
    @JoinColumn(name = "tropical_gaul_code")
    private AdminUnitTropical adminUnitTropical;

    // The disease group.
    @ManyToOne
    @JoinColumn(name = "disease_group_id")
    private DiseaseGroup diseaseGroup;

    // The disease extent class.
    @ManyToOne
    @JoinColumn(name = "disease_extent_class", nullable = false)
    private DiseaseExtentClass diseaseExtentClass;

    // The number of disease occurrences giving rise to this extent class.
    @Column(name = "occurrence_count")
    private Integer occurrenceCount;

    // Whether this extent class has changed since it was last generated.
    @Column(name = "has_changed")
    private boolean hasChanged = true;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public AdminUnitDiseaseExtentClass() {
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount) {
        this.adminUnitGlobal = adminUnitGlobal;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
        this.occurrenceCount = occurrenceCount;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount) {
        this.adminUnitTropical = adminUnitTropical;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
        this.occurrenceCount = occurrenceCount;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount,
                                       boolean hasChanged) {
        this.adminUnitGlobal = adminUnitGlobal;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
        this.occurrenceCount = occurrenceCount;
        this.hasChanged = hasChanged;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount,
                                       boolean hasChanged) {
        this.adminUnitTropical = adminUnitTropical;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
        this.occurrenceCount = occurrenceCount;
        this.hasChanged = hasChanged;
    }

    /**
     * The AdminUnitGlobal or the AdminUnitTropical, whichever of the pair is not null.
     * @return The (global or tropical) admin unit.
     */
    public AdminUnitGlobalOrTropical getAdminUnitGlobalOrTropical() {
        return (adminUnitGlobal == null) ? adminUnitTropical : adminUnitGlobal;
    }

    public Integer getId() {
        return id;
    }

    public AdminUnitGlobal getAdminUnitGlobal() {
        return adminUnitGlobal;
    }

    public void setAdminUnitGlobal(AdminUnitGlobal adminUnitGlobal) {
        this.adminUnitGlobal = adminUnitGlobal;
    }

    public AdminUnitTropical getAdminUnitTropical() {
        return adminUnitTropical;
    }

    public void setAdminUnitTropical(AdminUnitTropical adminUnitTropical) {
        this.adminUnitTropical = adminUnitTropical;
    }

    /**
     * Sets the global or tropical admin unit.
     * @param adminUnit The global or tropical admin unit.
     */
    public void setAdminUnitGlobalOrTropical(AdminUnitGlobalOrTropical adminUnit) {
        if (adminUnit instanceof AdminUnitGlobal) {
            setAdminUnitGlobal((AdminUnitGlobal) adminUnit);
        } else if (adminUnit instanceof AdminUnitTropical) {
            setAdminUnitTropical((AdminUnitTropical) adminUnit);
        }
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public DiseaseExtentClass getDiseaseExtentClass() {
        return diseaseExtentClass;
    }

    public void setDiseaseExtentClass(DiseaseExtentClass diseaseExtentClass) {
        this.diseaseExtentClass = diseaseExtentClass;
    }

    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    /**
     * Returns whether this extent class has changed since it was last generated.
     * @return Whether this extent class has changed since it was last generated.
     */
    public boolean hasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
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

        AdminUnitDiseaseExtentClass that = (AdminUnitDiseaseExtentClass) o;

        if (hasChanged != that.hasChanged) return false;
        if (adminUnitGlobal != null ? !adminUnitGlobal.equals(that.adminUnitGlobal) : that.adminUnitGlobal != null)
            return false;
        if (adminUnitTropical != null ? !adminUnitTropical.equals(that.adminUnitTropical) : that.adminUnitTropical != null)
            return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseExtentClass != that.diseaseExtentClass) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (occurrenceCount != null ? !occurrenceCount.equals(that.occurrenceCount) : that.occurrenceCount != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (adminUnitGlobal != null ? adminUnitGlobal.hashCode() : 0);
        result = 31 * result + (adminUnitTropical != null ? adminUnitTropical.hashCode() : 0);
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (diseaseExtentClass != null ? diseaseExtentClass.hashCode() : 0);
        result = 31 * result + (occurrenceCount != null ? occurrenceCount.hashCode() : 0);
        result = 31 * result + (hasChanged ? 1 : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON

    @Override
    public String toString() {
        return "AdminUnitDiseaseExtentClass{" +
                "id=" + id +
                ", adminUnitGlobal=" + (adminUnitGlobal != null ? adminUnitGlobal.getGaulCode() : "") +
                ", adminUnitTropical=" + (adminUnitTropical != null ? adminUnitTropical.getGaulCode() : "") +
                ", diseaseExtentClass=" + diseaseExtentClass.getName() +
                ", occurrenceCount=" + occurrenceCount +
                ", hasChanged=" + hasChanged +
                ", createdDate=" + createdDate +
                '}';
    }
}
