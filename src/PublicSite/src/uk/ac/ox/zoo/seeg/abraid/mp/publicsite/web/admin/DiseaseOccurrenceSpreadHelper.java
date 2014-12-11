package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.apache.commons.lang.builder.CompareToBuilder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.DiseaseOccurrenceSpreadTable;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

/**
 * Helper class for generating a disease occurrence spread table for a particular disease group.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceSpreadHelper {
    private DiseaseService diseaseService;
    private LocationService locationService;

    private static final String DISEASE_GROUP_DOES_NOT_EXIST_MESSAGE = "This disease group does not exist.";
    private static final String NO_OCCURRENCES_MESSAGE = "This disease group has no relevant occurrences.";

    public DiseaseOccurrenceSpreadHelper(DiseaseService diseaseService, LocationService locationService) {
        this.diseaseService = diseaseService;
        this.locationService = locationService;
    }

    /**
     * Gets a disease occurrence spread table for the specified disease group. This is a count of disease occurrences
     * (excluding country-level points) by the country they occur in (rows) and year (columns).
     * @param diseaseGroupId The disease group ID.
     * @return A disease occurrence spread table.
     */
    public DiseaseOccurrenceSpreadTable getDiseaseOccurrenceSpreadTable(int diseaseGroupId) {
        if (diseaseService.getDiseaseGroupById(diseaseGroupId) == null) {
            return new DiseaseOccurrenceSpreadTable(DISEASE_GROUP_DOES_NOT_EXIST_MESSAGE);
        }

        // Get disease occurrences by disease group and multiple statuses, excluding country-level points.
        List<DiseaseOccurrence> occurrences = getOccurrencesForTable(diseaseGroupId);

        if (occurrences.size() > 0) {
            List<Country> countries = getCountries();
            Set<Integer> years = getUniqueOccurrenceYears(occurrences);
            int[][] tableArray = getOccurrencesTableArray(occurrences, countries, years);
            return convertArrayToTable(countries, years, tableArray);
        } else {
            return new DiseaseOccurrenceSpreadTable(NO_OCCURRENCES_MESSAGE);
        }
    }

    private List<DiseaseOccurrence> getOccurrencesForTable(int diseaseGroupId) {
        return select(diseaseService.getDiseaseOccurrencesByDiseaseGroupIdAndStatuses(
                        diseaseGroupId,
                        DiseaseOccurrenceStatus.READY,
                        DiseaseOccurrenceStatus.IN_REVIEW,
                        DiseaseOccurrenceStatus.AWAITING_BATCHING),
           having(on(DiseaseOccurrence.class).getLocation().getPrecision(), not(equalTo(LocationPrecision.COUNTRY))));
    }

    private List<Country> getCountries() {
        List<Country> countries = locationService.getAllCountries();
        Collections.sort(countries, new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                return new CompareToBuilder()
                        .append(o2.isForMinDataSpread(), o1.isForMinDataSpread()) // descending (i.e. true before false)
                        .append(o1.getName(), o2.getName()) // ascending
                        .toComparison();
            }
        });
        return countries;
    }

    private Set<Integer> getUniqueOccurrenceYears(List<DiseaseOccurrence> occurrences) {
        // Use a TreeSet to keep the set sorted
        Set<Integer> years = new TreeSet<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            years.add(occurrence.getOccurrenceDate().getYear());
        }
        return years;
    }

    private int[][] getOccurrencesTableArray(List<DiseaseOccurrence> occurrences, List<Country> countries,
                                             Set<Integer> years) {
        // Map country GAUL codes to consecutive table rows
        Map<Integer, Integer> countryMap = new HashMap<>();
        Integer row = 0;
        for (Country country : countries) {
            countryMap.put(country.getGaulCode(), row);
            row++;
        }

        // Map occurrence years to consecutive table columns
        Map<Integer, Integer> yearMap = new HashMap<>();
        Integer column = 0;
        for (int year : years) {
            yearMap.put(year, column);
            column++;
        }

        // Add occurrences to the table (which is an array for ease of incrementing)
        int[][] tableArray = new int[countryMap.size()][yearMap.size()];
        for (DiseaseOccurrence occurrence : occurrences) {
            row = countryMap.get(occurrence.getLocation().getCountryGaulCode());
            column = yearMap.get(occurrence.getOccurrenceDate().getYear());
            if (row != null && column != null) {
                // Increment the number of occurrences for this country and year
                tableArray[row][column]++;
            }
        }

        return tableArray;
    }

    private DiseaseOccurrenceSpreadTable convertArrayToTable(List<Country> countries, Set<Integer> years,
                                                             int[][] tableArray) {
        DiseaseOccurrenceSpreadTable table = new DiseaseOccurrenceSpreadTable(years);
        int row = 0;
        for (Country country : countries) {
            table.addRow(country, toList(tableArray[row]));
            row++;
        }
        return table;
    }

    private List<Integer> toList(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int item : array) {
            list.add(item);
        }
        return list;
    }
}
