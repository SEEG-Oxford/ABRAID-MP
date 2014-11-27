package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

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
        )
})
@Entity
@Table(name = "admin_unit_disease_extent_class")
public class AdminUnitDiseaseExtentClass extends AbstractAdminUnitDiseaseExtentClass {
    // The disease group.
    @ManyToOne
    @JoinColumn(name = "disease_group_id")
    private DiseaseGroup diseaseGroup;

    // The date on which the disease extent class last changed.
    @Column(name = "class_changed_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime classChangedDate;

    // The number of disease occurrences giving rise to this extent class.
    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    // List of the latest disease occurrences that were used in determining this disease extent class classification.
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_unit_disease_extent_class_id")
    private List<DiseaseOccurrence> latestOccurrences;

    public AdminUnitDiseaseExtentClass() {
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical) {
        this.setAdminUnitTropical(adminUnitTropical);
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount) {
        this.setAdminUnitGlobal(adminUnitGlobal);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.occurrenceCount = occurrenceCount;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount) {
        this.setAdminUnitTropical(adminUnitTropical);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.occurrenceCount = occurrenceCount;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitGlobal adminUnitGlobal, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount,
                                       DateTime classChangedDate) {
        this.setAdminUnitGlobal(adminUnitGlobal);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.occurrenceCount = occurrenceCount;
        this.classChangedDate = classChangedDate;
    }

    public AdminUnitDiseaseExtentClass(AdminUnitTropical adminUnitTropical, DiseaseGroup diseaseGroup,
                                       DiseaseExtentClass diseaseExtentClass, Integer occurrenceCount,
                                       DateTime classChangedDate) {
        this.setAdminUnitTropical(adminUnitTropical);
        this.diseaseGroup = diseaseGroup;
        this.setDiseaseExtentClass(diseaseExtentClass);
        this.occurrenceCount = occurrenceCount;
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

    public int getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    public List<DiseaseOccurrence> getLatestOccurrences() {
        return latestOccurrences;
    }

    public void setLatestOccurrences(List<DiseaseOccurrence> occurrences) {
        this.latestOccurrences = occurrences;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AdminUnitDiseaseExtentClass that = (AdminUnitDiseaseExtentClass) o;

        if (occurrenceCount != that.occurrenceCount) return false;
        if (classChangedDate != null ? !classChangedDate.equals(that.classChangedDate) : that.classChangedDate != null)
            return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (latestOccurrences != null ? !latestOccurrences.equals(that.latestOccurrences) : that.latestOccurrences != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (classChangedDate != null ? classChangedDate.hashCode() : 0);
        result = 31 * result + occurrenceCount;
        result = 31 * result + (latestOccurrences != null ? latestOccurrences.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
