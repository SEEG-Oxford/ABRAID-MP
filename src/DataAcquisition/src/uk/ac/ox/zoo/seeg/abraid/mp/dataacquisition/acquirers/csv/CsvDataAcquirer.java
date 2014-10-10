package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.apache.log4j.Logger;
import org.hamcrest.core.IsEqual;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DiseaseOccurrenceDataAcquirer;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

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
    private static final String INVALID_FORMAT_ERROR_MESSAGE = "CSV file has invalid format: %s.";
    private static final String LINE_ERROR_MESSAGE = "Error in CSV file on line %d: %s.";
    private static final String CONVERSION_MESSAGE = "Found %d CSV file line(s) to convert.";
    private static final String SAVED_MESSAGE =
            "Saved %d disease occurrence(s) in %d location(s) (of which %d location(s) passed QC).";
    private static final String SAVED_NO_OCCURRENCES_MESSAGE = "Did not save any disease occurrences.";

    private CsvDiseaseOccurrenceConverter converter;
    private DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer;
    private CsvLookupData csvLookupData;

    public CsvDataAcquirer(CsvDiseaseOccurrenceConverter converter,
                           DiseaseOccurrenceDataAcquirer diseaseOccurrenceDataAcquirer, CsvLookupData csvLookupData) {
        this.converter = converter;
        this.diseaseOccurrenceDataAcquirer = diseaseOccurrenceDataAcquirer;
        this.csvLookupData = csvLookupData;
    }

    /**
     * Acquires data from a generic CSV file.
     * @param csv The content of the CSV file.
     * @param isGoldStandard Whether or not this is a "gold standard" data set.
     * @return A list of messages resulting from the data acquisition.
     */
    public List<String> acquireDataFromCsv(String csv, boolean isGoldStandard) {
        List<String> messages = new ArrayList<>();

        List<CsvDiseaseOccurrence> csvDiseaseOccurrences = retrieveDataFromCsv(csv, messages);
        if (csvDiseaseOccurrences != null) {
            List<DiseaseOccurrence> convertedOccurrences = convert(csvDiseaseOccurrences, isGoldStandard, messages);
            addCountMessage(convertedOccurrences, messages);
            csvLookupData.clearLookups();
        }

        return messages;
    }

    private List<CsvDiseaseOccurrence> retrieveDataFromCsv(String csv, List<String> messages) {
        try {
            return CsvDiseaseOccurrence.readFromCsv(csv);
        } catch (Exception e) {
            String message = String.format(INVALID_FORMAT_ERROR_MESSAGE, e.getMessage());
            LOGGER.error(message, e);
            messages.add(message);
            return null;
        }
    }

    private List<DiseaseOccurrence> convert(List<CsvDiseaseOccurrence> csvDiseaseOccurrences, boolean isGoldStandard,
                                            List<String> messages) {
        List<DiseaseOccurrence> convertedOccurrences = new ArrayList<>();
        String message = String.format(CONVERSION_MESSAGE, csvDiseaseOccurrences.size());
        LOGGER.info(message);
        messages.add(message);

        for (int i = 0; i < csvDiseaseOccurrences.size(); i++) {
            CsvDiseaseOccurrence csvDiseaseOccurrence = csvDiseaseOccurrences.get(i);
            try {
                // Convert the CSV disease occurrence into an ABRAID disease occurrence
                DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence);
                // Now acquire the ABRAID disease occurrence
                if (diseaseOccurrenceDataAcquirer.acquire(occurrence, isGoldStandard)) {
                    convertedOccurrences.add(occurrence);
                }
            } catch (DataAcquisitionException e) {
                // This CSV disease occurrence could not be acquired. So add the exception message to the list of
                // messages that will be returned. The CSV line number includes the header row.
                message = String.format(LINE_ERROR_MESSAGE, i + 2, e.getMessage());
                LOGGER.warn(message);
                messages.add(message);
            }
        }

        return convertedOccurrences;
    }

    private void addCountMessage(List<DiseaseOccurrence> occurrences, List<String> messages) {
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
        messages.add(1, message);
    }

    private Set<Location> findUniqueLocations(List<DiseaseOccurrence> occurrences) {
        return new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
    }

    private List<Location> findLocationsPassingQc(Set<Location> locations) {
        return select(locations, having(on(Location.class).hasPassedQc(), IsEqual.equalTo(true)));
    }
}
