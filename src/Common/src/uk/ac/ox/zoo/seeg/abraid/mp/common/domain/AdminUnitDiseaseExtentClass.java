package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Collection;

/**
 * Represents the current extent class (e.g. presence, absence) of an administrative unit, for a specific disease group.
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
        ),
        @NamedQuery(
                name = "getLatestDiseaseExtentClassChangeDateByDiseaseGroupId",
                query = "select max(a.classChangedDate) " +
                        "from AdminUnitDiseaseExtentClass a " +
                        "where a.diseaseGroup.id=:diseaseGroupId"
        )
})
@Entity
@Table(name = "admin_unit_disease_extent_class")
public class AdminUnitDiseaseExtentClass extends AbstractAdminUnitDiseaseExtentClass {
    // The disease group.
    @ManyToOne
    @JoinColumn(name = "disease_group_id")
    private DiseaseGroup diseaseGroup;

    // The date on which the modelling disease extent class last changed.
    @Column(name = "class_changed_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime classChangedDate;

    // The disease extent class current under review by experts.
    @ManyToOne
    @JoinColumn(name = "validator_disease_extent_class", nullable = false)
    private DiseaseExtentClass validatorDiseaseExtentClass;

    // The number of disease occurrences giving rise to the validator extent class.
    @Column(name = "validator_occurrence_count", nullable = false)
    private int validatorOccurrenceCount;

    // List of the latest disease occurrences that were used in determining the validator extent class classification.
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_unit_disease_extent_class_id")
    private Collection<DiseaseOccurrence> latestValidatorOccurrences;

    public AdminUnitDiseaseExtentClass() {
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical) {
        this.setAdminUnitTropical(adminUnitTropical);
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
            DiseaseExtentClass diseaseExtentClass, DiseaseExtentClass validatorDiseaseExtentClass,
            Integer validatorOccurrenceCount) {
        this.setAdminUnitGlobal(adminUnitGlobal);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.validatorDiseaseExtentClass = validatorDiseaseExtentClass;
        this.validatorOccurrenceCount = validatorOccurrenceCount;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
            DiseaseExtentClass diseaseExtentClass, DiseaseExtentClass validatorDiseaseExtentClass,
            Integer validatorOccurrenceCount) {
        this.setAdminUnitTropical(adminUnitTropical);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.validatorDiseaseExtentClass = validatorDiseaseExtentClass;
        this.validatorOccurrenceCount = validatorOccurrenceCount;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
            DiseaseExtentClass diseaseExtentClass, DiseaseExtentClass validatorDiseaseExtentClass,
            Integer validatorOccurrenceCount, DateTime classChangedDate) {
        this.setAdminUnitGlobal(adminUnitGlobal);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.validatorDiseaseExtentClass = validatorDiseaseExtentClass;
        this.validatorOccurrenceCount = validatorOccurrenceCount;
        this.classChangedDate = classChangedDate;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
            DiseaseExtentClass diseaseExtentClass, DiseaseExtentClass validatorDiseaseExtentClass,
            Integer validatorOccurrenceCount, DateTime classChangedDate) {
        this.setAdminUnitTropical(adminUnitTropical);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.validatorDiseaseExtentClass = validatorDiseaseExtentClass;
        this.validatorOccurrenceCount = validatorOccurrenceCount;
        this.classChangedDate = classChangedDate;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public DateTime getClassChangedDate() {
        return classChangedDate;
    }

    public void setClassChangedDate(DateTime classChangedDate) {
        this.classChangedDate = classChangedDate;
    }

    public int getValidatorOccurrenceCount() {
        return validatorOccurrenceCount;
    }

    public void setValidatorOccurrenceCount(int occurrenceCount) {
        this.validatorOccurrenceCount = occurrenceCount;
    }

    public Collection<DiseaseOccurrence> getLatestValidatorOccurrences() {
        return latestValidatorOccurrences;
    }

    public void setLatestValidatorOccurrences(Collection<DiseaseOccurrence> occurrences) {
        this.latestValidatorOccurrences = occurrences;
    }

    public DiseaseExtentClass getValidatorDiseaseExtentClass() {
        return validatorDiseaseExtentClass;
    }

    public void setValidatorDiseaseExtentClass(DiseaseExtentClass validatorDiseaseExtentClass) {
        this.validatorDiseaseExtentClass = validatorDiseaseExtentClass;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AdminUnitDiseaseExtentClass that = (AdminUnitDiseaseExtentClass) o;

        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (classChangedDate != null ? !classChangedDate.equals(that.classChangedDate) : that.classChangedDate != null)
            return false;
        if (validatorDiseaseExtentClass != null ? !validatorDiseaseExtentClass.equals(that.validatorDiseaseExtentClass) : that.validatorDiseaseExtentClass != null)
            return false;
        if (validatorOccurrenceCount != that.validatorOccurrenceCount) return false;
        if (latestValidatorOccurrences != null ? !latestValidatorOccurrences.equals(that.latestValidatorOccurrences) : that.latestValidatorOccurrences != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (classChangedDate != null ? classChangedDate.hashCode() : 0);
        result = 31 * result + (validatorDiseaseExtentClass != null ? validatorDiseaseExtentClass.hashCode() : 0);
        result = 31 * result + validatorOccurrenceCount;
        result = 31 * result + (latestValidatorOccurrences != null ? latestValidatorOccurrences.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
