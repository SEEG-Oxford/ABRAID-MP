package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents an occurrence of a disease group, in a location, as reported by an alert.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getDiseaseOccurrencesForExistenceCheck",
                query = "from DiseaseOccurrence where diseaseGroup=:diseaseGroup and location=:location " +
                        "and alert=:alert and occurrenceDate=:occurrenceDate"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesYetToBeReviewedByExpert",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.validatorDiseaseGroup.id=:validatorDiseaseGroupId " +
                        "and d.isValidated = false " +
                        "and d.id not in (select diseaseOccurrence.id from DiseaseOccurrenceReview where " +
                        "expert.id=:expertId)"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForDiseaseExtent",
                query = DiseaseOccurrence.DISEASE_EXTENT_QUERY
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForDiseaseExtentByFeedIds",
                query = DiseaseOccurrence.DISEASE_EXTENT_QUERY + " and d.alert.feed.id in :feedIds"
        ),
        @NamedQuery(
                name = "getDiseaseOccurrencesForModelRunRequest",
                query = DiseaseOccurrence.DISEASE_OCCURRENCE_BASE_QUERY +
                        "where d.diseaseGroup.id=:diseaseGroupId and d.isValidated = true"
        ),
        @NamedQuery(
                name = "getNewOccurrencesCountByDiseaseGroup",
                query = "select count(*) from DiseaseOccurrence where isValidated is not null and " +
                        "diseaseGroup.id=:diseaseGroupId and createdDate > diseaseGroup.lastModelRunPrepDate"
        )
})
@Entity
@Table(name = "disease_occurrence")
public class DiseaseOccurrence {
    /**
     * An HQL fragment used as a basis for disease occurrence queries. It ensures that Hibernate populate the objects
     * and their parents using one select statement.
     */
    public static final String DISEASE_OCCURRENCE_BASE_QUERY =
            "from DiseaseOccurrence as d " +
            "inner join fetch d.location " +
            "inner join fetch d.alert " +
            "inner join fetch d.alert.feed " +
            "inner join fetch d.alert.feed.provenance " +
            "inner join fetch d.diseaseGroup ";

    /**
     * An HQL fragment used to get disease occurrences for a disease extent.
     */
    public static final String DISEASE_EXTENT_QUERY =
            "select new uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceForDiseaseExtent" +
            "       (d.occurrenceDate, d.location.adminUnitGlobalGaulCode, d.location.adminUnitTropicalGaulCode) " +
            "from DiseaseOccurrence d " +
            "where d.diseaseGroup.id = :diseaseGroupId " +
            "and d.validationWeighting >= :minimumValidationWeighting " +
            "and d.occurrenceDate >= :minimumOccurrenceDate " +
            "and d.location.hasPassedQc = true " +
            "and ((:isGlobal = true and d.location.adminUnitGlobalGaulCode is not null) or " +
            "     (:isGlobal = false and d.location.adminUnitTropicalGaulCode is not null))";

    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease group that occurred.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "disease_group_id", nullable = false)
    private DiseaseGroup diseaseGroup;

    // The location of this occurrence.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // The alert containing this occurrence.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    // Boolean indicating whether the occurrence has been through (system or expert) validation.
    @Column(name = "is_validated")
    private Boolean isValidated;

    // The weighting as calculated from experts' responses during data validation process.
    @Column(name = "expert_weighting")
    private Double expertWeighting;

    // The weighting as predicted via the system (which may include machine learning).
    @Column(name = "machine_weighting")
    private Double machineWeighting;

    // The validation weighting used in the data weighting formula.
    // Takes the value of the expertWeighting if it exists, otherwise the machineWeighting value.
    @Column(name = "validation_weighting")
    private Double validationWeighting;

    // The final weighting to be used in the model run,
    // combining location resolution weighting, feed weighting, disease group type weighting and validation weighting.
    @Column(name = "final_weighting")
    private Double finalWeighting;

    // The final weighting to be used in later model runs, following a refactor where location's spatial resolution
    // weighting is handled separately. This value combines feed weighting, disease group type weighting and validation
    // weighting.
    @Column(name = "final_weighting_excl_spatial")
    private Double finalWeightingExcludingSpatial;

    // The date of the disease occurrence.
    @Column(name = "occurrence_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime occurrenceDate;

    public DiseaseOccurrence() {
    }

    public DiseaseOccurrence(int id) {
        this.id = id;
    }

    public DiseaseOccurrence(Integer id, DiseaseGroup diseaseGroup, Location location, Alert alert, Boolean isValidated,
                             Double validationWeighting, DateTime occurrenceDate) {
        this.id = id;
        this.diseaseGroup = diseaseGroup;
        this.location = location;
        this.alert = alert;
        this.isValidated = isValidated;
        this.validationWeighting = validationWeighting;
        this.occurrenceDate = occurrenceDate;
    }

    public Integer getId() {
        return id;
    }

    public DiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public ValidatorDiseaseGroup getValidatorDiseaseGroup() {
        return diseaseGroup.getValidatorDiseaseGroup();
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

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public Boolean isValidated() {
        return isValidated;
    }

    public void setValidated(Boolean isValidated) {
        this.isValidated = isValidated;
    }

    public Double getExpertWeighting() {
        return expertWeighting;
    }

    public void setExpertWeighting(Double expertWeighting) {
        this.expertWeighting = expertWeighting;
    }

    public Double getMachineWeighting() {
        return machineWeighting;
    }

    public void setMachineWeighting(Double machineWeighting) {
        this.machineWeighting = machineWeighting;
    }

    public Double getValidationWeighting() {
        return validationWeighting;
    }

    public void setValidationWeighting(Double validationWeighting) {
        this.validationWeighting = validationWeighting;
    }

    public Double getFinalWeighting() {
        return finalWeighting;
    }

    public void setFinalWeighting(Double finalWeighting) {
        this.finalWeighting = finalWeighting;
    }

    public Double getFinalWeightingExcludingSpatial() {
        return finalWeightingExcludingSpatial;
    }

    public void setFinalWeightingExcludingSpatial(Double finalWeightingExcludingSpatial) {
        this.finalWeightingExcludingSpatial = finalWeightingExcludingSpatial;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(DateTime occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiseaseOccurrence)) return false;

        DiseaseOccurrence that = (DiseaseOccurrence) o;

        if (alert != null ? !alert.equals(that.alert) : that.alert != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseGroup != null ? !diseaseGroup.equals(that.diseaseGroup) : that.diseaseGroup != null) return false;
        if (expertWeighting != null ? !expertWeighting.equals(that.expertWeighting) : that.expertWeighting != null)
            return false;
        if (finalWeighting != null ? !finalWeighting.equals(that.finalWeighting) : that.finalWeighting != null)
            return false;
        if (finalWeightingExcludingSpatial != null ? !finalWeightingExcludingSpatial.equals(that.finalWeightingExcludingSpatial) : that.finalWeightingExcludingSpatial != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (isValidated != null ? !isValidated.equals(that.isValidated) : that.isValidated != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (occurrenceDate != null ? !occurrenceDate.equals(that.occurrenceDate) : that.occurrenceDate != null)
            return false;
        if (machineWeighting != null ? !machineWeighting.equals(that.machineWeighting) : that.machineWeighting != null)
            return false;
        if (validationWeighting != null ? !validationWeighting.equals(that.validationWeighting) : that.validationWeighting != null)
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
        result = 31 * result + (isValidated != null ? isValidated.hashCode() : 0);
        result = 31 * result + (expertWeighting != null ? expertWeighting.hashCode() : 0);
        result = 31 * result + (machineWeighting != null ? machineWeighting.hashCode() : 0);
        result = 31 * result + (validationWeighting != null ? validationWeighting.hashCode() : 0);
        result = 31 * result + (finalWeighting != null ? finalWeighting.hashCode() : 0);
        result = 31 * result + (finalWeightingExcludingSpatial != null ? finalWeightingExcludingSpatial.hashCode() : 0);
        result = 31 * result + (occurrenceDate != null ? occurrenceDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
