package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * Return the set of occurrences to be used in the model run, satisfying Minimum Data Spread conditions.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterHelper {

    private DiseaseService diseaseService;
    private LocationService locationService;

    // Minimum Data Spread parameters for the disease group
    private List<DiseaseOccurrence> allOccurrences;
    private int minDataVolume;
    private Integer minDistinctCountries;
    private Integer highFrequencyThreshold;
    private Integer minHighFrequencyCountries;
    private boolean occursInAfrica;
    private List<Integer> countriesOfInterest;

    public ModelRunRequesterHelper(DiseaseService diseaseService, LocationService locationService) {
        this.diseaseService = diseaseService;
        this.locationService = locationService;
    }

    /**
     * Set the MDS calculation parameters for the specified disease group.
     * @param diseaseGroupId The ID of the disease group for the model run.
     */
    public void setParameters(int diseaseGroupId) {
        allOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        minDataVolume = diseaseGroup.getMinDataVolume();
        minDistinctCountries = diseaseGroup.getMinDistinctCountries();
        highFrequencyThreshold = diseaseGroup.getHighFrequencyThreshold();
        minHighFrequencyCountries = diseaseGroup.getMinHighFrequencyCountries();
        occursInAfrica = diseaseGroup.occursInAfrica();
        countriesOfInterest = occursInAfrica ? locationService.getCountriesForMinDataSpreadCalculation() : null;
    }

    /**
     * Gets the set of occurrences to be used in the model run.
     * @return The list of occurrences with which to run the model,
     * or null if the MDS thresholds are not met and the model should not run.
     */
    public List<DiseaseOccurrence> selectModelRunDiseaseOccurrences() {
        return ((allOccurrences.size() < minDataVolume) || anyParametersNull()) ? null : selectSubset();
    }

    private boolean anyParametersNull() {
        return (minDistinctCountries == null) || (highFrequencyThreshold == null) ||
               (minHighFrequencyCountries == null);
    }

    private List<DiseaseOccurrence> selectSubset() {
        // Select subset of n most recent occurrences (allOccurrences list is sorted by occurrence date)
        int n = minDataVolume;
        List<DiseaseOccurrence> occurrences = allOccurrences.subList(0, n);

        // If MDS is not met, continue to select points until it does, unless we run out of points.
        while (!minimumDataSpreadMet(occurrences)) {
            if (n == allOccurrences.size()) {
                return null;
            }
            n++;
            occurrences = allOccurrences.subList(0, n);
        }
        return occurrences;
    }

    private boolean minimumDataSpreadMet(List<DiseaseOccurrence> occurrences) {
        if (occursInAfrica) {
            return distinctCountriesCheck(occurrences) && highFrequencyCountriesCheck(occurrences);
        } else {
            return distinctCountriesCheck(occurrences);
        }
    }

    private boolean distinctCountriesCheck(List<DiseaseOccurrence> occurrences) {
        List<Integer> countriesWithAtLeastOneOccurrence = extractDistinctGaulCodes(occurrences);
        if (occursInAfrica) {
            countriesWithAtLeastOneOccurrence = considerOnlyCountriesOfInterest(countriesWithAtLeastOneOccurrence);
        }
        return countriesWithAtLeastOneOccurrence.size() > minDistinctCountries;
    }

    private List<Integer> extractDistinctGaulCodes(List<DiseaseOccurrence> occurrences) {
        Set<Location> distinctLocations = new HashSet<>(
               extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
        return convert(distinctLocations, new Converter<Location, Integer>() {
            public Integer convert(Location location) {
                return location.getCountryGaulCode();
            }
        });
    }

    // Keep only the countries with occurrences that feature in list of African countries, ignoring all others.
    private List<Integer> considerOnlyCountriesOfInterest(List<Integer> countries) {
        countries.retainAll(countriesOfInterest);
        return countries;
    }

    private boolean highFrequencyCountriesCheck(List<DiseaseOccurrence> occurrences) {
        Map<Integer, Integer> occurrenceCountPerCountry = constructOccurrenceCountPerCountryMap(occurrences);
        List<Integer> highFrequencyOccurrenceCountries = extractHighFrequencyCountries(occurrenceCountPerCountry);
        highFrequencyOccurrenceCountries = considerOnlyCountriesOfInterest(highFrequencyOccurrenceCountries);
        return highFrequencyOccurrenceCountries.size() > minHighFrequencyCountries;
    }

    private Map<Integer, Integer> constructOccurrenceCountPerCountryMap(List<DiseaseOccurrence> occurrences) {
        Map<Integer, Integer> map = new HashMap<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            Integer countryGaulCode = occurrence.getLocation().getCountryGaulCode();
            Integer value = (map.containsKey(countryGaulCode)) ? map.get(countryGaulCode) : 0;
            map.put(countryGaulCode, value + 1);
        }
        return map;
    }

    private List<Integer> extractHighFrequencyCountries(Map<Integer, Integer> map) {
        List<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > highFrequencyThreshold) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}
