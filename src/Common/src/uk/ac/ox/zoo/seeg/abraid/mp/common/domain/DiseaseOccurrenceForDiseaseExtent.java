package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.joda.time.DateTime;

/**
 * A DTO for a disease occurrence, containing fields used to generate the disease extent.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceForDiseaseExtent {
    private DateTime occurrenceDate;
    private int adminUnitGaulCode;

    public DiseaseOccurrenceForDiseaseExtent(DateTime occurrenceDate, int adminUnitGaulCode) {
        this.occurrenceDate = occurrenceDate;
        this.adminUnitGaulCode = adminUnitGaulCode;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    public int getAdminUnitGaulCode() {
        return adminUnitGaulCode;
    }
}
