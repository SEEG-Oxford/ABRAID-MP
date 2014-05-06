package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.joda.time.DateTime;

/**
 * A DTO for a disease occurrence, containing fields used to generate the disease extent.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceForDiseaseExtent {
    private DateTime occurrenceDate;

    private Double validationWeighting;

    private Integer adminUnitGlobalGaulCode;

    private Integer adminUnitTropicalGaulCode;

    public DiseaseOccurrenceForDiseaseExtent(DateTime occurrenceDate, Double validationWeighting,
                                             Integer adminUnitGlobalGaulCode, Integer adminUnitTropicalGaulCode) {
        this.occurrenceDate = occurrenceDate;
        this.validationWeighting = validationWeighting;
        this.adminUnitGlobalGaulCode = adminUnitGlobalGaulCode;
        this.adminUnitTropicalGaulCode = adminUnitTropicalGaulCode;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    public Double getValidationWeighting() {
        return validationWeighting;
    }

    public Integer getAdminUnitGlobalOrTropicalGaulCode() {
        return (adminUnitGlobalGaulCode != null) ? adminUnitGlobalGaulCode : adminUnitTropicalGaulCode;
    }
}
