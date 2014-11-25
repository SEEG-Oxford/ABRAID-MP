package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents the extent class (e.g. presence, absence) of an administrative unit.
 * The admin unit will either be AdminUnitGlobal or AdminUnitTropical, depending on the property of the DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
@MappedSuperclass
public abstract class AbstractAdminUnitDiseaseExtentClass {
    // The primary key.
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

    // The disease extent class.
    @ManyToOne
    @JoinColumn(name = "disease_extent_class", nullable = false)
    private DiseaseExtentClass diseaseExtentClass;

    protected AbstractAdminUnitDiseaseExtentClass() {
    }

    protected AbstractAdminUnitDiseaseExtentClass(AbstractAdminUnitDiseaseExtentClass adminUnitExtentClass) {
        adminUnitGlobal = adminUnitExtentClass.getAdminUnitGlobal();
        adminUnitTropical = adminUnitExtentClass.getAdminUnitTropical();
        diseaseExtentClass = adminUnitExtentClass.getDiseaseExtentClass();
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
     * The AdminUnitGlobal or the AdminUnitTropical, whichever of the pair is not null.
     * @return The (global or tropical) admin unit.
     */
    public AdminUnitGlobalOrTropical getAdminUnitGlobalOrTropical() {
        return (adminUnitGlobal == null) ? adminUnitTropical : adminUnitGlobal;
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

    public DiseaseExtentClass getDiseaseExtentClass() {
        return diseaseExtentClass;
    }

    public void setDiseaseExtentClass(DiseaseExtentClass diseaseExtentClass) {
        this.diseaseExtentClass = diseaseExtentClass;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAdminUnitDiseaseExtentClass)) return false;

        AbstractAdminUnitDiseaseExtentClass that = (AbstractAdminUnitDiseaseExtentClass) o;

        if (adminUnitGlobal != null ? !adminUnitGlobal.equals(that.adminUnitGlobal) : that.adminUnitGlobal != null)
            return false;
        if (adminUnitTropical != null ? !adminUnitTropical.equals(that.adminUnitTropical) : that.adminUnitTropical != null)
            return false;
        if (diseaseExtentClass != null ? !diseaseExtentClass.equals(that.diseaseExtentClass) : that.diseaseExtentClass != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (adminUnitGlobal != null ? adminUnitGlobal.hashCode() : 0);
        result = 31 * result + (adminUnitTropical != null ? adminUnitTropical.hashCode() : 0);
        result = 31 * result + (diseaseExtentClass != null ? diseaseExtentClass.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
