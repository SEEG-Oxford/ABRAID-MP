package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a disease occurrence spread table. This is a count of disease occurrences by country (rows) and year
 * (columns).
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceSpreadTable {
    private Collection<Integer> headingYears;
    private List<DiseaseOccurrenceSpreadTableRow> rows;
    private String errorMessage;

    public DiseaseOccurrenceSpreadTable(Collection<Integer> headingYears) {
        this.headingYears = headingYears;
        this.rows = new ArrayList<>();
    }

    public DiseaseOccurrenceSpreadTable(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Collection<Integer> getHeadingYears() {
        return headingYears;
    }

    public List<DiseaseOccurrenceSpreadTableRow> getRows() {
        return rows;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
