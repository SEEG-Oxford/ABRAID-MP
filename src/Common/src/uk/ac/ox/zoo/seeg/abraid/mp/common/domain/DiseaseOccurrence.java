package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Represents an occurrence of a disease group, in a location, as reported by an alert.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class DiseaseOccurrence {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease group that occurred.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "diseaseGroupId")
    private DiseaseGroup diseaseGroup;

    // The location of this occurrence.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "locationId")
    private Location location;

    // The alert containing this occurrence.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "alertId")
    private Alert alert;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    private Date createdDate;

    // A weighting to take into account the method of disease diagnosis.
    private Double diagnosticWeight;

    // The start date of the disease occurrence (if known).
    @Column
    private Date occurrenceStartDate;

    public DiseaseOccurrence() {
    }

    public Integer getId() {
        return id;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Double getDiagnosticWeight() {
        return diagnosticWeight;
    }

    public void setDiagnosticWeight(Double diagnosticWeight) {
        this.diagnosticWeight = diagnosticWeight;
    }

    public Date getOccurrenceStartDate() {
        return occurrenceStartDate;
    }

    public void setOccurrenceStartDate(Date occurrenceStartDate) {
        this.occurrenceStartDate = occurrenceStartDate;
    }

    @Override
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseOccurrence that = (DiseaseOccurrence) o;

        if (alert != null ? !alert.equals(that.alert) : that.alert != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diagnosticWeight != null ? !diagnosticWeight.equals(that.diagnosticWeight) : that.diagnosticWeight != null)
            return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (occurrenceStartDate != null ? !occurrenceStartDate.equals(that.occurrenceStartDate) : that.occurrenceStartDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (diseaseGroup != null ? diseaseGroup.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (alert != null ? alert.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (diagnosticWeight != null ? diagnosticWeight.hashCode() : 0);
        result = 31 * result + (occurrenceStartDate != null ? occurrenceStartDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
