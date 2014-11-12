package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one row of a disease occurrence spread table.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceSpreadTableRow {
    private String countryName;
    private boolean isForMinimumDiseaseSpread;
    private List<Integer> occurrenceCounts;

    public DiseaseOccurrenceSpreadTableRow() {
    }

    public DiseaseOccurrenceSpreadTableRow(Country country, List<Integer> occurrenceCounts) {
        this.countryName = country.getName();
        this.isForMinimumDiseaseSpread = country.isForMinDataSpread();
        this.occurrenceCounts = occurrenceCounts;
    }

    public String getCountryName() {
        return countryName;
    }

    public boolean getIsForMinimumDiseaseSpread() {
        return isForMinimumDiseaseSpread;
    }

    public List<Integer> getOccurrenceCounts() {
        return occurrenceCounts;
    }
}
