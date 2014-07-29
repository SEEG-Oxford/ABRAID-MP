package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

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

    @Column(name = "max_months_ago")
    private Integer maximumMonthsAgo;

    @Column(name = "min_validation_weighting")
    private Double minimumValidationWeighting;

    @Column(name = "min_occurrences_for_presence")
    private Integer minimumOccurrencesForPresence;

    @Column(name = "min_occurrences_for_possible_presence")
    private Integer minimumOccurrencesForPossiblePresence;

    @Column(name = "max_months_ago_for_higher_occurrence_score")
    private Integer maximumMonthsAgoForHigherOccurrenceScore;

    @Column(name = "lower_occurrence_score")
    private Integer lowerOccurrenceScore;

    @Column(name = "higher_occurrence_score")
    private Integer higherOccurrenceScore;

    @OneToOne
    @JoinColumn(name = "disease_group_id")
    private DiseaseGroup diseaseGroup;

    public DiseaseExtent() {
    }

    public DiseaseExtent(Integer maximumMonthsAgo, Double minimumValidationWeighting,
                         Integer minimumOccurrencesForPresence, Integer minimumOccurrencesForPossiblePresence,
                         Integer maximumMonthsAgoForHigherOccurrenceScore,
                         Integer lowerOccurrenceScore, Integer higherOccurrenceScore) {
        this.maximumMonthsAgo = maximumMonthsAgo;
        this.minimumValidationWeighting = minimumValidationWeighting;
        this.minimumOccurrencesForPresence = minimumOccurrencesForPresence;
        this.minimumOccurrencesForPossiblePresence = minimumOccurrencesForPossiblePresence;
        this.maximumMonthsAgoForHigherOccurrenceScore = maximumMonthsAgoForHigherOccurrenceScore;
        this.lowerOccurrenceScore = lowerOccurrenceScore;
        this.higherOccurrenceScore = higherOccurrenceScore;
    }

    public Integer getDiseaseGroupId() {
        return diseaseGroupId;
    }

    public void setDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.diseaseGroup = diseaseGroup;
    }

    public Integer getMaximumMonthsAgo() {
        return maximumMonthsAgo;
    }

    public void setMaximumMonthsAgo(Integer maximumMonthsAgo) {
        this.maximumMonthsAgo = maximumMonthsAgo;
    }

    public Double getMinimumValidationWeighting() {
        return minimumValidationWeighting;
    }

    public void setMinimumValidationWeighting(Double minimumValidationWeighting) {
        this.minimumValidationWeighting = minimumValidationWeighting;
    }

    public Integer getMinimumOccurrencesForPresence() {
        return minimumOccurrencesForPresence;
    }

    public void setMinimumOccurrencesForPresence(Integer minimumOccurrencesForPresence) {
        this.minimumOccurrencesForPresence = minimumOccurrencesForPresence;
    }

    public Integer getMinimumOccurrencesForPossiblePresence() {
        return minimumOccurrencesForPossiblePresence;
    }

    public void setMinimumOccurrencesForPossiblePresence(Integer minimumOccurrencesForPossiblePresence) {
        this.minimumOccurrencesForPossiblePresence = minimumOccurrencesForPossiblePresence;
    }

    public Integer getMaximumMonthsAgoForHigherOccurrenceScore() {
        return maximumMonthsAgoForHigherOccurrenceScore;
    }

    public void setMaximumMonthsAgoForHigherOccurrenceScore(Integer maximumMonthsAgoForHigherOccurrenceScore) {
        this.maximumMonthsAgoForHigherOccurrenceScore = maximumMonthsAgoForHigherOccurrenceScore;
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

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiseaseExtent)) return false;

        DiseaseExtent that = (DiseaseExtent) o;

        if (diseaseGroupId != null ? !diseaseGroupId.equals(that.diseaseGroupId) : that.diseaseGroupId != null)
            return false;
        if (higherOccurrenceScore != null ? !higherOccurrenceScore.equals(that.higherOccurrenceScore) : that.higherOccurrenceScore != null)
            return false;
        if (lowerOccurrenceScore != null ? !lowerOccurrenceScore.equals(that.lowerOccurrenceScore) : that.lowerOccurrenceScore != null)
            return false;
        if (maximumMonthsAgo != null ? !maximumMonthsAgo.equals(that.maximumMonthsAgo) : that.maximumMonthsAgo != null)
            return false;
        if (maximumMonthsAgoForHigherOccurrenceScore != null ? !maximumMonthsAgoForHigherOccurrenceScore.equals(that.maximumMonthsAgoForHigherOccurrenceScore) : that.maximumMonthsAgoForHigherOccurrenceScore != null)
            return false;
        if (minimumOccurrencesForPossiblePresence != null ? !minimumOccurrencesForPossiblePresence.equals(that.minimumOccurrencesForPossiblePresence) : that.minimumOccurrencesForPossiblePresence != null)
            return false;
        if (minimumOccurrencesForPresence != null ? !minimumOccurrencesForPresence.equals(that.minimumOccurrencesForPresence) : that.minimumOccurrencesForPresence != null)
            return false;
        if (minimumValidationWeighting != null ? !minimumValidationWeighting.equals(that.minimumValidationWeighting) : that.minimumValidationWeighting != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseGroupId != null ? diseaseGroupId.hashCode() : 0;
        result = 31 * result + (maximumMonthsAgo != null ? maximumMonthsAgo.hashCode() : 0);
        result = 31 * result + (minimumValidationWeighting != null ? minimumValidationWeighting.hashCode() : 0);
        result = 31 * result + (minimumOccurrencesForPresence != null ? minimumOccurrencesForPresence.hashCode() : 0);
        result = 31 * result + (minimumOccurrencesForPossiblePresence != null ? minimumOccurrencesForPossiblePresence.hashCode() : 0);
        result = 31 * result + (maximumMonthsAgoForHigherOccurrenceScore != null ? maximumMonthsAgoForHigherOccurrenceScore.hashCode() : 0);
        result = 31 * result + (lowerOccurrenceScore != null ? lowerOccurrenceScore.hashCode() : 0);
        result = 31 * result + (higherOccurrenceScore != null ? higherOccurrenceScore.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
