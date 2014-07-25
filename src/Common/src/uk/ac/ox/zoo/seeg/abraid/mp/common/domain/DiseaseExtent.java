package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

/**
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "disease_extent")
public class DiseaseExtent {
    @Id
    @Column(name = "disease_group_id")
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "disease_group"))
    private int diseaseGroupId;

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

    public int getDiseaseGroupId() {
        return diseaseGroupId;
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
}
