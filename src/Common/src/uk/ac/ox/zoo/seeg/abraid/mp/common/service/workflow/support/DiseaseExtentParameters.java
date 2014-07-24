package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import java.util.List;

/**
 * Represents a set of parameters for generating the disease extent for a single disease.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentParameters {
    private int maximumYearsAgo;
    private double minimumValidationWeighting;
    private int minimumOccurrencesForPresence;
    private int minimumOccurrencesForPossiblePresence;
    private int maximumYearsAgoForHigherOccurrenceScore;
    private int lowerOccurrenceScore;
    private int higherOccurrenceScore;

    public DiseaseExtentParameters(int maximumYearsAgo, double minimumValidationWeighting,
                                   int minimumOccurrencesForPresence, int minimumOccurrencesForPossiblePresence,
                                   int maximumYearsAgoForHigherOccurrenceScore, int lowerOccurrenceScore,
                                   int higherOccurrenceScore) {
        this.maximumYearsAgo = maximumYearsAgo;
        this.minimumValidationWeighting = minimumValidationWeighting;
        this.minimumOccurrencesForPresence = minimumOccurrencesForPresence;
        this.minimumOccurrencesForPossiblePresence = minimumOccurrencesForPossiblePresence;
        this.maximumYearsAgoForHigherOccurrenceScore = maximumYearsAgoForHigherOccurrenceScore;
        this.lowerOccurrenceScore = lowerOccurrenceScore;
        this.higherOccurrenceScore = higherOccurrenceScore;
    }

    public int getMaximumYearsAgo() {
        return maximumYearsAgo;
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

    public int getMaximumYearsAgoForHigherOccurrenceScore() {
        return maximumYearsAgoForHigherOccurrenceScore;
    }

    public int getLowerOccurrenceScore() {
        return lowerOccurrenceScore;
    }

    public int getHigherOccurrenceScore() {
        return higherOccurrenceScore;
    }
}
