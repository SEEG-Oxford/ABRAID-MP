package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.apache.commons.lang.StringUtils;
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
    private static final String CONVERSION_MESSAGE = "Converting %d CSV file line(s).";
    private static final String SUCCESS_MESSAGE =
            "Saved %d disease occurrence(s) in %d location(s) (of which %d location(s) passed QC).";

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
     * @return A message upon the success of the data acquisition.
     * @throws DataAcquisitionException Upon failure of the data acquisition.
     */
    public String acquireDataFromCsv(String csv, boolean isGoldStandard) throws DataAcquisitionException {
        try {
            List<CsvDiseaseOccurrence> csvDiseaseOccurrences = retrieveDataFromCsv(csv);
            List<DiseaseOccurrence> convertedOccurrences = convert(csvDiseaseOccurrences, isGoldStandard);
            return createSuccessMessage(convertedOccurrences);
        } finally {
            csvLookupData.clearLookups();
        }
    }

    private List<CsvDiseaseOccurrence> retrieveDataFromCsv(String csv) {
        try {
            return CsvDiseaseOccurrence.readFromCsv(csv);
        } catch (Exception e) {
            String message = String.format(INVALID_FORMAT_ERROR_MESSAGE, e.getMessage());
            LOGGER.error(message, e);
            throw new DataAcquisitionException(message, e);
        }
    }

    private List<DiseaseOccurrence> convert(List<CsvDiseaseOccurrence> csvDiseaseOccurrences, boolean isGoldStandard) {
        List<DiseaseOccurrence> convertedOccurrences = new ArrayList<>();
        LOGGER.info(String.format(CONVERSION_MESSAGE, csvDiseaseOccurrences.size()));

        List<String> errorMessages = new ArrayList<>();
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
                // This CSV disease occurrence could not be acquired. So add the CSV line number to
                // the exception message and store it for later rethrowing.
                String message = String.format(LINE_ERROR_MESSAGE, i + 1, e.getMessage());
                LOGGER.error(message, e);
                errorMessages.add(message);
            }
        }

        if (errorMessages.size() > 0) {
            // If there were errors during acquisition, they have already been logged so now rethrow them.
            // This allows all errors in the file to be reported to the user rather than one error at a time.
            String message = StringUtils.join(errorMessages, System.lineSeparator());
            throw new DataAcquisitionException(message);
        }

        return convertedOccurrences;
    }

    private String createSuccessMessage(List<DiseaseOccurrence> occurrences) {
        Set<Location> uniqueLocations = findUniqueLocations(occurrences);
        List<Location> locationsPassingQc = findLocationsPassingQc(uniqueLocations);
        String message = String.format(SUCCESS_MESSAGE, occurrences.size(), uniqueLocations.size(),
                locationsPassingQc.size());
        LOGGER.info(message);
        return message;
    }

    private Set<Location> findUniqueLocations(List<DiseaseOccurrence> occurrences) {
        return new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
    }

    private List<Location> findLocationsPassingQc(Set<Location> locations) {
        return select(locations, having(on(Location.class).hasPassedQc(), IsEqual.equalTo(true)));
    }
}
