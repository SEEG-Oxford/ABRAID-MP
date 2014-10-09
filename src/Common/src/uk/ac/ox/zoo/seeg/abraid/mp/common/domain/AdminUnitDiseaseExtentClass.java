package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

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

    // The date on which the disease extent class last changed.
    @Column(name = "class_changed_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime classChangedDate;

    // List of disease occurrences used to determine this disease extent class classification.
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "adminUnitDiseaseExtentClass")
    private List<DiseaseOccurrence> diseaseOccurrences;

    public AdminUnitDiseaseExtentClass() {
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass) {
        this.adminUnitGlobal = adminUnitGlobal;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass) {
        this.adminUnitTropical = adminUnitTropical;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, DateTime classChangedDate) {
        this.adminUnitGlobal = adminUnitGlobal;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
        this.classChangedDate = classChangedDate;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, DateTime classChangedDate) {
        this.adminUnitTropical = adminUnitTropical;
        this.diseaseGroup = diseaseGroup;
        this.diseaseExtentClass = diseaseExtentClass;
        this.classChangedDate = classChangedDate;
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

    public DateTime getClassChangedDate() {
        return classChangedDate;
    }

    public void setClassChangedDate(DateTime classChangedDate) {
        this.classChangedDate = classChangedDate;
    }

    public List<DiseaseOccurrence> getDiseaseOccurrences() {
        return diseaseOccurrences;
    }

    public void setDiseaseOccurrences(List<DiseaseOccurrence> occurrences) {
        this.diseaseOccurrences = occurrences;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminUnitDiseaseExtentClass)) return false;

        AdminUnitDiseaseExtentClass that = (AdminUnitDiseaseExtentClass) o;

        if (adminUnitGlobal != null ? !adminUnitGlobal.equals(that.adminUnitGlobal) : that.adminUnitGlobal != null)
            return false;
        if (adminUnitTropical != null ? !adminUnitTropical.equals(that.adminUnitTropical) : that.adminUnitTropical != null)
            return false;
        if (classChangedDate != null ? !classChangedDate.equals(that.classChangedDate) : that.classChangedDate != null)
            return false;
        if (diseaseExtentClass != null ? !diseaseExtentClass.equals(that.diseaseExtentClass) : that.diseaseExtentClass != null)
            return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (adminUnitGlobal != null ? adminUnitGlobal.hashCode() : 0);
        result = 31 * result + (adminUnitTropical != null ? adminUnitTropical.hashCode() : 0);
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (diseaseExtentClass != null ? diseaseExtentClass.hashCode() : 0);
        result = 31 * result + (classChangedDate != null ? classChangedDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
