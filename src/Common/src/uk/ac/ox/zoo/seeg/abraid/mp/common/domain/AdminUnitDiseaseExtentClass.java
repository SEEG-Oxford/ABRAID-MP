package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents the extent class (ie presence, absence) of a disease group across a administrative unit.
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
    @Column(name = "disease_extent_class")
    @Enumerated(EnumType.STRING)
    private DiseaseExtentClass diseaseExtentClass;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

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

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminUnitDiseaseExtentClass)) return false;

        AdminUnitDiseaseExtentClass that = (AdminUnitDiseaseExtentClass) o;

        if (!createdDate.equals(that.createdDate)) return false;
        if (diseaseExtentClass != that.diseaseExtentClass) return false;
        if (!diseaseGroup.equals(that.diseaseGroup)) return false;
        if (adminUnitGlobal != null ? !adminUnitGlobal.equals(that.adminUnitGlobal) : that.adminUnitGlobal != null)
            return false;
        if (!id.equals(that.id)) return false;
        if (adminUnitTropical != null ? !adminUnitTropical.equals(that.adminUnitTropical) : that.adminUnitTropical != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (adminUnitGlobal != null ? adminUnitGlobal.hashCode() : 0);
        result = 31 * result + (adminUnitTropical != null ? adminUnitTropical.hashCode() : 0);
        result = 31 * result + diseaseGroup.hashCode();
        result = 31 * result + diseaseExtentClass.hashCode();
        result = 31 * result + createdDate.hashCode();
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
