package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static java.util.Map.Entry;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Return the set of occurrences to be used in the model run, satisfying Minimum Data Volume and Spread conditions.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunOccurrencesSelector {

    // Log messages
    private static final String NOT_REQUESTING_EMAIL_MESSAGE =
            "Not requesting a model run for disease group %d (%s) because ";
    private static final String NOT_REQUESTING_EMAIL_SUBJECT = "Minimum Data Volume/Spread Not Satisfied";
    private static final String MDV_SATISFIED_LOG_MESSAGE =
            "Minimum Data Volume is satisfied: %d occurrence(s) exceeds threshold of %d";
    private static final String MDV_NOT_SATISFIED_LOG_MESSAGE =
            "Minimum Data Volume is not satisfied: %d occurrence(s) does not exceed threshold of %d";
    private static final String MDS_NOT_SATISFIED_LOG_MESSAGE =
            "Minimum Data Spread is not satisfied: no occurrences in any countries of interest";
    private static final String AFRICAN_COUNTRY_CLAUSE =
            "at least 1 occurrence in %d countries, and at least %d occurrence(s) in %d countries";
    private static final String AFRICAN_MDS_NOT_SATISFIED_LOG_MESSAGE =
            "Minimum Data Spread is not satisfied: " +
                    "should have " + AFRICAN_COUNTRY_CLAUSE + ", but only has " + AFRICAN_COUNTRY_CLAUSE;
    private static final String OTHER_COUNTRY_CLAUSE =
            "at least 1 occurrence in %d distinct countries";
    private static final String OTHER_MDS_NOT_SATISFIED_LOG_MESSAGE =
            "Minimum Data Spread is not satisfied: " +
                    OTHER_COUNTRY_CLAUSE + " does not exceed threshold of %d countries";
    private static final String MDS_SATISFIED_LOG_MESSAGE =
            "Minimum Data Spread is satisfied: ";
    private static final String SKIPPING_MDS_CALCULATION =
            "Skipping Minimum Data Spread calculation; at least one parameter is not defined";

    // Exception messages (these are displayed in the user interface via the Bad Request response text)
    private static final String MDV_NOT_SATISFIED_EXCEPTION_MESSAGE =
            "Model cannot run because minimum data volume is not satisfied.";
    private static final String MDS_NOT_SATISFIED_EXCEPTION_MESSAGE =
            "Model cannot run because minimum data spread is not satisfied.";

    private static final Logger LOGGER = Logger.getLogger(ModelRunOccurrencesSelector.class);

    private DiseaseService diseaseService;
    private LocationService locationService;
    private EmailService emailService;

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

    public ModelRunOccurrencesSelector(DiseaseService diseaseService, LocationService locationService,
                                       EmailService emailService, int diseaseGroupId,
                                       boolean onlyUseGoldStandardOccurrences) {
        this.diseaseService = diseaseService;
        this.locationService = locationService;
        this.emailService = emailService;
        initialise(diseaseGroupId, onlyUseGoldStandardOccurrences);
    }

    // Set the MDS calculation parameters for the specified disease group.
    private void initialise(int diseaseGroupId, boolean onlyUseGoldStandardOccurrences) {
        allOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId,
                onlyUseGoldStandardOccurrences);
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
     * @throws ModelRunWorkflowException if the model should not run because the required thresholds have not been
     * reached.
     */
    public List<DiseaseOccurrence> selectModelRunDiseaseOccurrences() throws ModelRunWorkflowException {
        List<DiseaseOccurrence> occurrences = null;

        // Minimum Data Volume must always be satisfied
        if (minDataVolumeSatisfied()) {
            LOGGER.info(String.format(MDV_SATISFIED_LOG_MESSAGE, allOccurrences.size(), minDataVolume));
            if (diseaseGroup.isAutomaticModelRunsEnabled()) {
                // If automatic model runs are enabled, select the subset of occurrences provided by the Minimum
                // Data Volume, then add occurrences until the Minimum Data Spread is achieved. These are selected
                // most recent first, to keep the model input data contemporary.
                occurrences = selectSubset();
                if (occursInAfrica != null) {
                    occurrences = occursInAfrica ? refineSubsetForAfricanDiseaseGroup(occurrences) :
                                                   refineSubsetForOtherDiseaseGroup(occurrences);
                }
            } else {
                // If automatic model runs are disabled, all occurrences are sent to the model
                occurrences = allOccurrences;
            }
        } else {
            handleCannotRunModel(String.format(MDV_NOT_SATISFIED_LOG_MESSAGE, allOccurrences.size(), minDataVolume),
                    MDV_NOT_SATISFIED_EXCEPTION_MESSAGE);
        }
        return occurrences;
    }

    private boolean minDataVolumeSatisfied() {
        return (allOccurrences.size() >= minDataVolume);
    }

    // Select subset of n most recent occurrences (allOccurrences list is sorted by occurrence date)
    // N.B. Occurrences must be added to a new list, instead of returning a subList. The latter only provides a view to
    // allOccurrences, so adding to occurrences also adds to allOccurrences, leading to OutOfMemoryError.
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
                    handleCannotRunModel(buildAfricanMDSNotSatisfiedLogMessage(), MDS_NOT_SATISFIED_EXCEPTION_MESSAGE);
                }
                DiseaseOccurrence nextOccurrence = allOccurrences.get(n);
                occurrences.add(nextOccurrence);
                addCountryToOccurrenceCountMap(nextOccurrence.getLocation().getCountryGaulCode());
            }
            handleCanRunModel();
        } else {
            LOGGER.info(SKIPPING_MDS_CALCULATION);
        }
        return occurrences;
    }

    private List<DiseaseOccurrence> refineSubsetForOtherDiseaseGroup(List<DiseaseOccurrence> occurrences) {
        if (minDistinctCountries != null) {
            extractDistinctGaulCodes(occurrences);
            while (!minDataSpreadCheckForOtherDiseaseGroup()) {
                int n = occurrences.size();
                if (n == allOccurrences.size()) {
                    handleCannotRunModel(buildOtherMDSNotSatisfiedLogMessage(), MDS_NOT_SATISFIED_EXCEPTION_MESSAGE);
                }
                DiseaseOccurrence nextOccurrence = allOccurrences.get(n);
                occurrences.add(nextOccurrence);
                countriesWithAtLeastOneOccurrence.add(nextOccurrence.getLocation().getCountryGaulCode());
            }
            handleCanRunModel();
        } else {
            LOGGER.info(SKIPPING_MDS_CALCULATION);
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
        countriesWithAtLeastOneOccurrence = new HashSet<>(
                extract(occurrences, on(DiseaseOccurrence.class).getLocation().getCountryGaulCode())
        );
    }

    private boolean minDataSpreadCheckForOtherDiseaseGroup() {
        return (countriesWithAtLeastOneOccurrence.size() >= minDistinctCountries);
    }

    private String buildAfricanMDSNotSatisfiedLogMessage() {
        int n = occurrenceCountPerCountry.keySet().size();
        return (n == 0) ? MDS_NOT_SATISFIED_LOG_MESSAGE : String.format(AFRICAN_MDS_NOT_SATISFIED_LOG_MESSAGE,
            minDistinctCountries, highFrequencyThreshold, minHighFrequencyCountries,
            occurrenceCountPerCountry.keySet().size(), highFrequencyThreshold, extractHighFrequencyCountries().size());
    }

    private String buildOtherMDSNotSatisfiedLogMessage() {
        int n = countriesWithAtLeastOneOccurrence.size();
        return (n == 0) ? MDS_NOT_SATISFIED_LOG_MESSAGE : String.format(OTHER_MDS_NOT_SATISFIED_LOG_MESSAGE,
            countriesWithAtLeastOneOccurrence.size(), minDistinctCountries);
    }

    private void handleCannotRunModel(String longMessage, String shortMessage) {
        // Log so it's in the logs. Send an e-mail to the default address so that the "longer" message is visible to
        // the user (particularly relevant if this was triggered by Data Manager). Throw an exception so that the
        // transaction rolls back, and to send a shorter message back to the user if it was triggered manually.
        String formattedLongMessage = String.format(NOT_REQUESTING_EMAIL_MESSAGE + longMessage, diseaseGroup.getId(),
                diseaseGroup.getName());
        LOGGER.warn(formattedLongMessage);

        try {
            emailService.sendEmail(NOT_REQUESTING_EMAIL_SUBJECT, formattedLongMessage);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }

        // And throw an exception
        throw new ModelRunWorkflowException(shortMessage);
    }

    private void handleCanRunModel() {
        String message;
        if (occursInAfrica) {
            message = String.format(AFRICAN_COUNTRY_CLAUSE,
            occurrenceCountPerCountry.keySet().size(), highFrequencyThreshold, extractHighFrequencyCountries().size());
        } else {
            message = String.format(OTHER_COUNTRY_CLAUSE, countriesWithAtLeastOneOccurrence.size());
        }
        LOGGER.info(MDS_SATISFIED_LOG_MESSAGE + message);
    }
}
