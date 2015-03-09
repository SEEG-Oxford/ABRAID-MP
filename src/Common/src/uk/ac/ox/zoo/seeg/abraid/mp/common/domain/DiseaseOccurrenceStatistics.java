package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.joda.time.DateTime;

/**
 * A DTO for statistics about a collection of disease occurrences.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceStatistics {
    private long occurrenceCount;
    private long modelEligibleOccurrenceCount;
    private DateTime minimumOccurrenceDate;
    private DateTime maximumOccurrenceDate;

    public DiseaseOccurrenceStatistics(long occurrenceCount, long modelEligibleOccurrenceCount,
                                       DateTime minimumOccurrenceDate, DateTime maximumOccurrenceDate) {
        this.occurrenceCount = occurrenceCount;
        this.modelEligibleOccurrenceCount = modelEligibleOccurrenceCount;
        this.minimumOccurrenceDate = minimumOccurrenceDate;
        this.maximumOccurrenceDate = maximumOccurrenceDate;
    }

    public long getOccurrenceCount() {
        return occurrenceCount;
    }

    public long getModelEligibleOccurrenceCount() {
        return modelEligibleOccurrenceCount;
    }

    public DateTime getMinimumOccurrenceDate() {
        return minimumOccurrenceDate;
    }

    public DateTime getMaximumOccurrenceDate() {
        return maximumOccurrenceDate;
    }
}
