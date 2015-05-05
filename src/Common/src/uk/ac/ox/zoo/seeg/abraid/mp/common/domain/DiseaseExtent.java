package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Collection;

/**
 * Represents the parameters required to calculate the disease group's current extent.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "disease_extent")
public class DiseaseExtent {
    @Id
    @Column(name = "disease_group_id")
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "foreign",
                      parameters = @Parameter(name = "property", value = "diseaseGroup"))
    private Integer diseaseGroupId;

    // The minimum value of disease_occurrence.validation_weighting used in generating the disease extent
    @Column(name = "min_validation_weighting")
    private Double minValidationWeighting;

    // The minimum number of occurrences in an admin unit to indicate presence (initial extent generation)
    @Column(name = "min_occurrences_for_presence")
    private Integer minOccurrencesForPresence;

    // The minimum number of occurrences in an admin unit to indicate possible presence (initial extent generation)
    @Column(name = "min_occurrences_for_possible_presence")
    private Integer minOccurrencesForPossiblePresence;

    // Occurrences up to this age are given the higher occurrence score, older ones are given the lower score
    @Column(name = "max_months_ago_for_higher_occurrence_score")
    private Integer maxMonthsAgoForHigherOccurrenceScore;

    // The lower score given to disease occurrences during extent generation
    @Column(name = "lower_occurrence_score")
    private Integer lowerOccurrenceScore;

    // The higher score given to disease occurrences during extent generation
    @Column(name = "higher_occurrence_score")
    private Integer higherOccurrenceScore;

    @OneToOne
    @JoinColumn(name = "disease_group_id")
    private DiseaseGroup diseaseGroup;

    // List of disease occurrences used in the last update of the validator disease extent class.
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "disease_extent_disease_occurrence",
            joinColumns = @JoinColumn(name = "disease_group_id"),
            inverseJoinColumns = @JoinColumn(name = "disease_occurrence_id"))
    @Fetch(FetchMode.SELECT)
    private Collection<DiseaseOccurrence> lastValidatorExtentUpdateInputOccurrences;

    public DiseaseExtent() {
    }

    public DiseaseExtent(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public DiseaseExtent(DiseaseGroup diseaseGroup, Double minValidationWeighting,
                         Integer minOccurrencesForPresence, Integer minOccurrencesForPossiblePresence,
                         Integer maxMonthsAgoForHigherOccurrenceScore,
                         Integer lowerOccurrenceScore, Integer higherOccurrenceScore) {
        this(diseaseGroup);
        this.minValidationWeighting = minValidationWeighting;
        this.minOccurrencesForPresence = minOccurrencesForPresence;
        this.minOccurrencesForPossiblePresence = minOccurrencesForPossiblePresence;
        this.maxMonthsAgoForHigherOccurrenceScore = maxMonthsAgoForHigherOccurrenceScore;
        this.lowerOccurrenceScore = lowerOccurrenceScore;
        this.higherOccurrenceScore = higherOccurrenceScore;
    }

    public Integer getDiseaseGroupId() {
        return diseaseGroupId;
    }

    public Double getMinValidationWeighting() {
        return minValidationWeighting;
    }

    public void setMinValidationWeighting(Double minimumValidationWeighting) {
        this.minValidationWeighting = minimumValidationWeighting;
    }

    public Integer getMinOccurrencesForPresence() {
        return minOccurrencesForPresence;
    }

    public void setMinOccurrencesForPresence(Integer minimumOccurrencesForPresence) {
        this.minOccurrencesForPresence = minimumOccurrencesForPresence;
    }

    public Integer getMinOccurrencesForPossiblePresence() {
        return minOccurrencesForPossiblePresence;
    }

    public void setMinOccurrencesForPossiblePresence(Integer minimumOccurrencesForPossiblePresence) {
        this.minOccurrencesForPossiblePresence = minimumOccurrencesForPossiblePresence;
    }

    public Integer getMaxMonthsAgoForHigherOccurrenceScore() {
        return maxMonthsAgoForHigherOccurrenceScore;
    }

    public void setMaxMonthsAgoForHigherOccurrenceScore(Integer maximumMonthsAgoForHigherOccurrenceScore) {
        this.maxMonthsAgoForHigherOccurrenceScore = maximumMonthsAgoForHigherOccurrenceScore;
    }

    public Integer getLowerOccurrenceScore() {
        return lowerOccurrenceScore;
    }

    public void setLowerOccurrenceScore(Integer lowerOccurrenceScore) {
        this.lowerOccurrenceScore = lowerOccurrenceScore;
    }

    public Integer getHigherOccurrenceScore() {
        return higherOccurrenceScore;
    }

    public void setHigherOccurrenceScore(Integer higherOccurrenceScore) {
        this.higherOccurrenceScore = higherOccurrenceScore;
    }

    public Collection<DiseaseOccurrence> getLastValidatorExtentUpdateInputOccurrences() {
        return lastValidatorExtentUpdateInputOccurrences;
    }

    public void setLastValidatorExtentUpdateInputOccurrences(
            Collection<DiseaseOccurrence> lastValidatorExtentUpdateInputOccurrences) {
        this.lastValidatorExtentUpdateInputOccurrences = lastValidatorExtentUpdateInputOccurrences;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseExtent that = (DiseaseExtent) o;

        if (diseaseGroupId != null ? !diseaseGroupId.equals(that.diseaseGroupId) : that.diseaseGroupId != null)
            return false;
        if (higherOccurrenceScore != null ? !higherOccurrenceScore.equals(that.higherOccurrenceScore) : that.higherOccurrenceScore != null)
            return false;
        if (lowerOccurrenceScore != null ? !lowerOccurrenceScore.equals(that.lowerOccurrenceScore) : that.lowerOccurrenceScore != null)
            return false;
        if (maxMonthsAgoForHigherOccurrenceScore != null ? !maxMonthsAgoForHigherOccurrenceScore.equals(that.maxMonthsAgoForHigherOccurrenceScore) : that.maxMonthsAgoForHigherOccurrenceScore != null)
            return false;
        if (minOccurrencesForPossiblePresence != null ? !minOccurrencesForPossiblePresence.equals(that.minOccurrencesForPossiblePresence) : that.minOccurrencesForPossiblePresence != null)
            return false;
        if (minOccurrencesForPresence != null ? !minOccurrencesForPresence.equals(that.minOccurrencesForPresence) : that.minOccurrencesForPresence != null)
            return false;
        if (minValidationWeighting != null ? !minValidationWeighting.equals(that.minValidationWeighting) : that.minValidationWeighting != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseGroupId != null ? diseaseGroupId.hashCode() : 0;
        result = 31 * result + (minValidationWeighting != null ? minValidationWeighting.hashCode() : 0);
        result = 31 * result + (minOccurrencesForPresence != null ? minOccurrencesForPresence.hashCode() : 0);
        result = 31 * result + (minOccurrencesForPossiblePresence != null ? minOccurrencesForPossiblePresence.hashCode() : 0);
        result = 31 * result + (maxMonthsAgoForHigherOccurrenceScore != null ? maxMonthsAgoForHigherOccurrenceScore.hashCode() : 0);
        result = 31 * result + (lowerOccurrenceScore != null ? lowerOccurrenceScore.hashCode() : 0);
        result = 31 * result + (higherOccurrenceScore != null ? higherOccurrenceScore.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
