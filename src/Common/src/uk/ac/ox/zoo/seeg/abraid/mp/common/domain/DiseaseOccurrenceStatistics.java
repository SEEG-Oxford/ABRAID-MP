package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.joda.time.DateTime;

/**
 * A DTO for statistics about a collection of disease occurrences.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceStatistics {
    private long occurrenceCount;
    private DateTime minimumOccurrenceDate;
    private DateTime maximumOccurrenceDate;

    public DiseaseOccurrenceStatistics(long occurrenceCount, DateTime minimumOccurrenceDate,
                                       DateTime maximumOccurrenceDate) {
        this.occurrenceCount = occurrenceCount;
        this.minimumOccurrenceDate = minimumOccurrenceDate;
        this.maximumOccurrenceDate = maximumOccurrenceDate;
    }

    public long getOccurrenceCount() {
        return occurrenceCount;
    }

    public DateTime getMinimumOccurrenceDate() {
        return minimumOccurrenceDate;
    }

    public DateTime getMaximumOccurrenceDate() {
        return maximumOccurrenceDate;
    }
}
