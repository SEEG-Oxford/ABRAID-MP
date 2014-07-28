package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

/**
 * Represents a set of parameters for generating the disease extent for a single disease.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentParameters {
    private int maximumMonthsAgo;
    private double minimumValidationWeighting;
    private int minimumOccurrencesForPresence;
    private int minimumOccurrencesForPossiblePresence;
    private int maximumMonthsAgoForHigherOccurrenceScore;
    private int lowerOccurrenceScore;
    private int higherOccurrenceScore;

    public DiseaseExtentParameters(int maximumMonthsAgo, double minimumValidationWeighting,
                                   int minimumOccurrencesForPresence, int minimumOccurrencesForPossiblePresence,
                                   int maximumMonthsAgoForHigherOccurrenceScore, int lowerOccurrenceScore,
                                   int higherOccurrenceScore) {
        this.maximumMonthsAgo = maximumMonthsAgo;
        this.minimumValidationWeighting = minimumValidationWeighting;
        this.minimumOccurrencesForPresence = minimumOccurrencesForPresence;
        this.minimumOccurrencesForPossiblePresence = minimumOccurrencesForPossiblePresence;
        this.maximumMonthsAgoForHigherOccurrenceScore = maximumMonthsAgoForHigherOccurrenceScore;
        this.lowerOccurrenceScore = lowerOccurrenceScore;
        this.higherOccurrenceScore = higherOccurrenceScore;
    }

    public int getMaximumMonthsAgo() {
        return maximumMonthsAgo;
    }

    public double getMinimumValidationWeighting() {
        return minimumValidationWeighting;
    }

    public int getMinimumOccurrencesForPresence() {
        return minimumOccurrencesForPresence;
    }

    public int getMinimumOccurrencesForPossiblePresence() {
        return minimumOccurrencesForPossiblePresence;
    }

    public int getMaximumMonthsAgoForHigherOccurrenceScore() {
        return maximumMonthsAgoForHigherOccurrenceScore;
    }

    public int getLowerOccurrenceScore() {
        return lowerOccurrenceScore;
    }

    public int getHigherOccurrenceScore() {
        return higherOccurrenceScore;
    }
}
