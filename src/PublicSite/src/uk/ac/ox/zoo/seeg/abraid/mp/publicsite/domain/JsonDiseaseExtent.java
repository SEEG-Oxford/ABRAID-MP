package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtent;

/**
 * Represents the disease extent parameters of a JsonDiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseExtent {
    private Integer maxMonthsAgoForHigherOccurrenceScore;
    private Integer higherOccurrenceScore;
    private Integer lowerOccurrenceScore;
    private Double minValidationWeighting;
    private Integer minOccurrencesForPresence;
    private Integer minOccurrencesForPossiblePresence;

    public JsonDiseaseExtent() {
    }

    public JsonDiseaseExtent(DiseaseExtent parameters) {
        setMaxMonthsAgoForHigherOccurrenceScore(parameters.getMaxMonthsAgoForHigherOccurrenceScore());
        setHigherOccurrenceScore(parameters.getHigherOccurrenceScore());
        setLowerOccurrenceScore(parameters.getLowerOccurrenceScore());
        setMinValidationWeighting(parameters.getMinValidationWeighting());
        setMinOccurrencesForPresence(parameters.getMinOccurrencesForPresence());
        setMinOccurrencesForPossiblePresence(parameters.getMinOccurrencesForPossiblePresence());
    }

    public Integer getMaxMonthsAgoForHigherOccurrenceScore() {
        return maxMonthsAgoForHigherOccurrenceScore;
    }

    public void setMaxMonthsAgoForHigherOccurrenceScore(Integer maxMonthsAgoForHigherOccurrenceScore) {
        this.maxMonthsAgoForHigherOccurrenceScore = maxMonthsAgoForHigherOccurrenceScore;
    }

    public Integer getHigherOccurrenceScore() {
        return higherOccurrenceScore;
    }

    public void setHigherOccurrenceScore(Integer higherOccurrenceScore) {
        this.higherOccurrenceScore = higherOccurrenceScore;
    }

    public Integer getLowerOccurrenceScore() {
        return lowerOccurrenceScore;
    }

    public void setLowerOccurrenceScore(Integer lowerOccurrenceScore) {
        this.lowerOccurrenceScore = lowerOccurrenceScore;
    }

    public Double getMinValidationWeighting() {
        return minValidationWeighting;
    }

    public void setMinValidationWeighting(Double minValidationWeighting) {
        this.minValidationWeighting = minValidationWeighting;
    }

    public Integer getMinOccurrencesForPresence() {
        return minOccurrencesForPresence;
    }

    public void setMinOccurrencesForPresence(Integer minOccurrencesForPresence) {
        this.minOccurrencesForPresence = minOccurrencesForPresence;
    }

    public Integer getMinOccurrencesForPossiblePresence() {
        return minOccurrencesForPossiblePresence;
    }

    public void setMinOccurrencesForPossiblePresence(Integer minOccurrencesForPossiblePresence) {
        this.minOccurrencesForPossiblePresence = minOccurrencesForPossiblePresence;
    }
}
