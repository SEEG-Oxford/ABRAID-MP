package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static java.util.Map.Entry;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Return the set of occurrences to be used in the model run, satisfying Minimum Data Spread conditions.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterHelper {

    // Log messages
    private static final String NOT_REQUESTING_LOG_MESSAGE =
            "Not requesting a model run for disease group %d (%s) because ";
    private static final String MDV_NOT_SATISFIED_LOG_MESSAGE = "minimum data volume (%d) is not satisfied";
    private static final String MDS_NOT_SATISFIED_LOG_MESSAGE = "minimum data spread is not satisfied";

    // Exception messages (these are displayed in the user interface via the Bad Request response text)
    private static final String MDV_NOT_SATISFIED_EXCEPTION_MESSAGE =
            "Model cannot run because minimum data volume is not satisfied.";
    private static final String MDS_NOT_SATISFIED_EXCEPTION_MESSAGE =
            "Model cannot run because minimum data spread is not satisfied.";

    private static final Logger LOGGER = Logger.getLogger(ModelRunRequesterHelper.class);

    private DiseaseService diseaseService;
    private LocationService locationService;

    // Minimum Data Spread parameters for the disease group
    private List<DiseaseOccurrence> allOccurrences;
    private DiseaseGroup diseaseGroup;
    private int minDataVolume;
    private Integer minDistinctCountries;
    private Integer highFrequencyThreshold;
    private Integer minHighFrequencyCountries;
    private Boolean occursInAfrica;

    // Reference structures used to compare values against in MDS checks
    private List<Integer> countriesOfInterest;
    private Set<Integer> countriesWithAtLeastOneOccurrence;     // For disease groups using all countries
    private Map<Integer, Integer> occurrenceCountPerCountry;    // For disease groups using only the African countries

    public ModelRunRequesterHelper(DiseaseService diseaseService, LocationService locationService, int diseaseGroupId) {
        this.diseaseService = diseaseService;
        this.locationService = locationService;
        initialise(diseaseGroupId);
    }

    // Set the MDS calculation parameters for the specified disease group.
    private void initialise(int diseaseGroupId) {
        allOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
        diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        minDataVolume = diseaseGroup.getMinDataVolume();
        minDistinctCountries = diseaseGroup.getMinDistinctCountries();
        highFrequencyThreshold = diseaseGroup.getHighFrequencyThreshold();
        minHighFrequencyCountries = diseaseGroup.getMinHighFrequencyCountries();
        occursInAfrica = diseaseGroup.occursInAfrica();
    }

    /**
     * Gets the list of occurrences to be used in the model run.
     * @return The list of occurrences with which to run the model,
     * @throws ModelRunRequesterException if the model should not run because the required thresholds have not been
     * reached.
     */
    public List<DiseaseOccurrence> selectModelRunDiseaseOccurrences() throws ModelRunRequesterException {
        List<DiseaseOccurrence> occurrences = null;
        if (minDataVolumeSatisfied()) {
            occurrences = selectSubset();
            if (occursInAfrica != null) {
                occurrences = occursInAfrica ? refineSubsetForAfricanDiseaseGroup(occurrences) :
                                               refineSubsetForOtherDiseaseGroup(occurrences);
            }
        } else {
            handleCannotRunModel(String.format(MDV_NOT_SATISFIED_LOG_MESSAGE, minDataVolume),
                    MDV_NOT_SATISFIED_EXCEPTION_MESSAGE);
        }
        return occurrences;
    }

    private boolean minDataVolumeSatisfied() {
        return (allOccurrences.size() >= minDataVolume);
    }

    // Select subset of n most recent occurrences (allOccurrences list is sorted by occurrence date)
    private List<DiseaseOccurrence> selectSubset() {
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        for (int i = 0; i < minDataVolume; i++) {
            occurrences.add(allOccurrences.get(i));
        }
        return occurrences;
    }

    // If MDS is not met, continue to select points until it does, unless we run out of points.
    private List<DiseaseOccurrence> refineSubsetForAfricanDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        if (parametersNotNull(minDistinctCountries, highFrequencyThreshold, minHighFrequencyCountries)) {
            countriesOfInterest = locationService.getCountriesForMinDataSpreadCalculation();
            constructOccurrenceCountPerCountryMap(occurrences);
            while (!minDataSpreadCheckForAfricanDiseaseGroup()) {
                int n = occurrences.size();
                if (n == allOccurrences.size()) {
                    handleCannotRunModel(MDS_NOT_SATISFIED_LOG_MESSAGE, MDS_NOT_SATISFIED_EXCEPTION_MESSAGE);
                }
                DiseaseOccurrence nextOccurrence = allOccurrences.get(n);
                occurrences.add(nextOccurrence);
                addCountryToOccurrenceCountMap(nextOccurrence.getLocation().getCountryGaulCode());
            }
        }
        return occurrences;
    }

    private List<DiseaseOccurrence> refineSubsetForOtherDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        if (minDistinctCountries != null) {
            extractDistinctGaulCodes(occurrences);
            while (!minDataSpreadCheckForOtherDiseaseGroup()) {
                int n = occurrences.size();
                if (n == allOccurrences.size()) {
                    handleCannotRunModel(MDS_NOT_SATISFIED_LOG_MESSAGE, MDS_NOT_SATISFIED_EXCEPTION_MESSAGE);
                }
                DiseaseOccurrence nextOccurrence = allOccurrences.get(n);
                occurrences.add(nextOccurrence);
                countriesWithAtLeastOneOccurrence.add(nextOccurrence.getLocation().getCountryGaulCode());
            }
        }
        return occurrences;
    }

    private static boolean parametersNotNull(Integer... args) {
        List<Integer> values = Arrays.asList(args);
        List<Integer> notNullValues = filter(notNullValue(), values);
        return (values.size() == notNullValues.size());
    }

    private void constructOccurrenceCountPerCountryMap(List<DiseaseOccurrence> occurrences) {
        occurrenceCountPerCountry = new HashMap<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            Integer countryGaulCode = occurrence.getLocation().getCountryGaulCode();
            addCountryToOccurrenceCountMap(countryGaulCode);
        }
    }

    // Only "Countries of Interest" are added to the map - with their corresponding occurrence count
    private void addCountryToOccurrenceCountMap(Integer gaulCode) {
        if (countriesOfInterest.contains(gaulCode)) {
            int value = (occurrenceCountPerCountry.containsKey(gaulCode)) ? occurrenceCountPerCountry.get(gaulCode) : 0;
            occurrenceCountPerCountry.put(gaulCode, value + 1);
        }
    }

    private boolean minDataSpreadCheckForAfricanDiseaseGroup() {
        Set<Integer> distinctCountries = occurrenceCountPerCountry.keySet();
        boolean distinctCountriesCheck = (distinctCountries.size() >= minDistinctCountries);
        Set<Integer> highFrequencyOccurrenceCountries = extractHighFrequencyCountries();
        boolean highFrequencyCountriesCheck = (highFrequencyOccurrenceCountries.size() >= minHighFrequencyCountries);
        return (distinctCountriesCheck & highFrequencyCountriesCheck);
    }

    private Set<Integer> extractHighFrequencyCountries() {
        Set<Integer> set = new HashSet<>();
        for (Entry<Integer, Integer> entry : occurrenceCountPerCountry.entrySet()) {
            if (entry.getValue() >= highFrequencyThreshold) {
                set.add(entry.getKey());
            }
        }
        return set;
    }

    private void extractDistinctGaulCodes(List<DiseaseOccurrence> occurrences) {
        Set<Location> locations = new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
        List<Integer> gaulCodes = convert(locations, new Converter<Location, Integer>() {
            public Integer convert(Location location) { return location.getCountryGaulCode(); }
        });
        countriesWithAtLeastOneOccurrence = new HashSet<>(gaulCodes);
    }

    private boolean minDataSpreadCheckForOtherDiseaseGroup() {
        return (countriesWithAtLeastOneOccurrence.size() >= minDistinctCountries);
    }

    private void handleCannotRunModel(String logSuffixMessage, String exceptionMessage) {
        LOGGER.warn(String.format(NOT_REQUESTING_LOG_MESSAGE + logSuffixMessage, diseaseGroup.getId(),
                diseaseGroup.getName()));
        throw new ModelRunRequesterException(exceptionMessage);
    }
}
