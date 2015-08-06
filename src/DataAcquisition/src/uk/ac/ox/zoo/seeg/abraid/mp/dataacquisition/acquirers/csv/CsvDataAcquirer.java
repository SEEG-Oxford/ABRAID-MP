package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.apache.log4j.Logger;
import org.hamcrest.core.IsEqual;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.CharacterSetUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DiseaseOccurrenceDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.ManualValidationEnforcer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;

/**
 * Acquires data from a generic CSV file.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDataAcquirer {
    private static final Logger LOGGER = Logger.getLogger(CsvDataAcquirer.class);

    private static final String CONVERTING_TO_UTF8_MESSAGE = "Detected character set %s, converting to UTF-8.";
    private static final String INVALID_FORMAT_ERROR_MESSAGE = "CSV file has invalid format: %s.";
    private static final String LINE_ERROR_MESSAGE = "Error in CSV file on line %d: %s.";
    private static final String CONVERSION_MESSAGE = "Found %d CSV file line(s) to convert.";
    private static final String SAVED_MESSAGE =
            "Saved %d disease occurrence(s) in %d location(s) (of which %d location(s) passed QC).";
    private static final String SAVED_NO_OCCURRENCES_MESSAGE = "Did not save any disease occurrences.";

    private final CsvDiseaseOccurrenceConverter converter;
    private final DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer;
    private final CsvLookupData csvLookupData;
    private final ManualValidationEnforcer manualValidationEnforcer;

    public CsvDataAcquirer(CsvDiseaseOccurrenceConverter converter,
                           DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer,
                           CsvLookupData csvLookupData,
                           ManualValidationEnforcer manualValidationEnforcer) {
        this.converter = converter;
        this.diseaseOccurrenceDataAcquirer = diseaseOccurrenceDataAcquirer;
        this.csvLookupData = csvLookupData;
        this.manualValidationEnforcer = manualValidationEnforcer;
    }

    /**
     * Acquires data from a generic CSV file.
     * @param csv The content of the CSV file.
     * @param isGoldStandard Whether or not this is a "gold standard" data set.
     * @return A list of messages resulting from the data acquisition.
     */
    public List<String> acquireDataFromCsv(byte[] csv, boolean isGoldStandard) {
        List<String> messages = new ArrayList<>();

        List<CsvDiseaseOccurrence> csvDiseaseOccurrences = retrieveDataFromCsv(csv, messages);
        if (csvDiseaseOccurrences != null) {
            addConversionMessage(csvDiseaseOccurrences, messages);
            int initialMessageCount = messages.size();
            Set<DiseaseOccurrence> convertedOccurrences = convert(csvDiseaseOccurrences, isGoldStandard, messages);
            if (!isGoldStandard) {
                manualValidationEnforcer.addRandomSubsetToManualValidation(convertedOccurrences);
            }
            addCountMessage(convertedOccurrences, messages, initialMessageCount);
            csvLookupData.clearLookups();
        }

        return messages;
    }

    private List<CsvDiseaseOccurrence> retrieveDataFromCsv(byte[] csv, List<String> messages) {
        try {
            String convertedCsv = convertToUTF8IfNecessary(csv, messages);
            return CsvDiseaseOccurrence.readFromCsv(convertedCsv);
        } catch (Exception e) {
            String message = String.format(INVALID_FORMAT_ERROR_MESSAGE, e.getMessage());
            LOGGER.error(message, e);
            messages.add(message);
            return null;
        }
    }

    private String convertToUTF8IfNecessary(byte[] input, List<String> messages) {
        byte[] convertedInput;
        Charset fromCharset = CharacterSetUtils.detectCharacterSet(input);

        if (fromCharset != null && !fromCharset.equals(StandardCharsets.UTF_8)) {
            // We have detected that the input is not UTF-8, so convert it to UTF-8
            String message = String.format(CONVERTING_TO_UTF8_MESSAGE, fromCharset.name());
            LOGGER.info(message);
            messages.add(message);
            convertedInput = CharacterSetUtils.convertToCharacterSet(input, fromCharset, StandardCharsets.UTF_8);
        } else {
            convertedInput = input;
        }

        return new String(convertedInput, StandardCharsets.UTF_8);
    }

    private Set<DiseaseOccurrence> convert(List<CsvDiseaseOccurrence> csvDiseaseOccurrences, boolean isGoldStandard,
                                            List<String> messages) {
        // Convert
        Set<DiseaseOccurrence> convertedOccurrences = new HashSet<>();
        for (int i = 0; i < csvDiseaseOccurrences.size(); i++) {
            CsvDiseaseOccurrence csvDiseaseOccurrence = csvDiseaseOccurrences.get(i);
            DiseaseOccurrence occurrence = null;
            try {
                // Convert the CSV disease occurrence into an ABRAID disease occurrence
                occurrence = converter.convert(csvDiseaseOccurrence, isGoldStandard);
                String ageWarning = diseaseOccurrenceDataAcquirer.checkOccurrenceAge(occurrence);
                if (ageWarning == null) {
                    convertedOccurrences.add(occurrence);
                } else {
                    throw new DataAcquisitionException(ageWarning);
                }
            } catch (DataAcquisitionException e) {
                // This CSV disease occurrence could not be acquired. So add the exception message to the list of
                // messages that will be returned. The CSV line number includes the header row.
                String message = String.format(LINE_ERROR_MESSAGE, i + 2, e.getMessage());
                LOGGER.warn(message);
                messages.add(message);
            }

        }

        // Acquire
        return diseaseOccurrenceDataAcquirer.acquire(convertedOccurrences);
    }

    private void addConversionMessage(List<CsvDiseaseOccurrence> csvDiseaseOccurrences, List<String> messages) {
        String message = String.format(CONVERSION_MESSAGE, csvDiseaseOccurrences.size());
        LOGGER.info(message);
        messages.add(message);
    }

    private void addCountMessage(Set<DiseaseOccurrence> occurrences, List<String> messages, int initialMessageCount) {
        Set<Location> uniqueLocations = findUniqueLocations(occurrences);
        List<Location> locationsPassingQc = findLocationsPassingQc(uniqueLocations);
        String message;
        if (occurrences.size() > 0) {
            message = String.format(SAVED_MESSAGE, occurrences.size(), uniqueLocations.size(),
                    locationsPassingQc.size());
        } else {
            message = SAVED_NO_OCCURRENCES_MESSAGE;
        }
        LOGGER.info(message);
        // Use initialMessageCount to ensure that the count message comes immediately after the initial messages
        messages.add(initialMessageCount, message);
    }

    private Set<Location> findUniqueLocations(Set<DiseaseOccurrence> occurrences) {
        return new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
    }

    private List<Location> findLocationsPassingQc(Set<Location> locations) {
        return select(locations, having(on(Location.class).hasPassedQc(), IsEqual.equalTo(true)));
    }
}
