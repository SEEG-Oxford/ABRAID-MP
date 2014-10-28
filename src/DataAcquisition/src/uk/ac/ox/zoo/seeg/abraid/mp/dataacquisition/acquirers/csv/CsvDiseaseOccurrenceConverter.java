package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Converts a CsvDiseaseOccurrence into a DiseaseOccurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrenceConverter {
    private static final String LOCATION_PRECISION_INVALID_MESSAGE = "Location precision \"%s\" is invalid";
    private static final String COUNTRY_NAME_INVALID_MESSAGE = "Country name \"%s\" is invalid";
    private static final String DISEASE_GROUP_NAME_INVALID_MESSAGE = "Disease group name \"%s\" is invalid";
    private static final String OCCURRENCE_DATE_INVALID_MESSAGE = "Occurrence date \"%s\" is invalid " +
            "(valid formats are %s)";

    private static final String[] OCCURRENCE_DATE_FORMATS = {"dd/MM/YYYY", "MM/YYYY", "YYYY", "MMM-YY", "MMM-YYYY"};

    private CsvLookupData csvLookupData;
    private List<DateTimeFormatter> occurrenceDateTimeFormatters;

    public CsvDiseaseOccurrenceConverter(CsvLookupData csvLookupData) {
        this.csvLookupData = csvLookupData;

        occurrenceDateTimeFormatters = new ArrayList<>();
        for (String format : OCCURRENCE_DATE_FORMATS) {
            // Create a DateTimeFormatter for each format pattern. The subsequent DateTimes will have time zone UTC
            // so that in the database the time is always midnight.
            occurrenceDateTimeFormatters.add(DateTimeFormat.forPattern(format).withZoneUTC());
        }
    }

    /**
     * Converts a CsvDiseaseOccurrence into a DiseaseOccurrence.
     * @param csvDiseaseOccurrence The CsvDiseaseOccurrence.
     * @return The converted DiseaseOccurrence.
     * @throws DataAcquisitionException if the DiseaseOccurrence could not be converted.
     */
    public DiseaseOccurrence convert(CsvDiseaseOccurrence csvDiseaseOccurrence) throws DataAcquisitionException {
        validate(csvDiseaseOccurrence);
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setLocation(convertLocation(csvDiseaseOccurrence));
        occurrence.setAlert(convertAlert(csvDiseaseOccurrence));
        occurrence.setDiseaseGroup(convertDiseaseGroup(csvDiseaseOccurrence));
        occurrence.setOccurrenceDate(convertOccurrenceDate(csvDiseaseOccurrence.getOccurrenceDate()));
        return occurrence;
    }

    private void validate(CsvDiseaseOccurrence csvDiseaseOccurrence) {
        CsvDiseaseOccurrenceValidator validator = new CsvDiseaseOccurrenceValidator(csvDiseaseOccurrence);
        validator.validate();
    }

    private Location convertLocation(CsvDiseaseOccurrence csvDiseaseOccurrence) {
        Location location = new Location();
        location.setName(csvDiseaseOccurrence.getSite());
        location.setGeom(csvDiseaseOccurrence.getLongitude(), csvDiseaseOccurrence.getLatitude());
        location.setPrecision(convertPrecision(csvDiseaseOccurrence.getPrecision()));
        location.setCountryGaulCode(convertCountry(csvDiseaseOccurrence.getCountryName()));
        return location;
    }

    private Alert convertAlert(CsvDiseaseOccurrence csvDiseaseOccurrence) {
        Alert alert = new Alert();
        alert.setFeed(csvLookupData.getFeedForUploadedData());
        alert.setTitle(csvDiseaseOccurrence.getTitle());
        alert.setSummary(csvDiseaseOccurrence.getSummary());
        alert.setUrl(csvDiseaseOccurrence.getUrl());
        return alert;
    }

    private DiseaseGroup convertDiseaseGroup(CsvDiseaseOccurrence csvDiseaseOccurrence) {
        String diseaseGroupName = csvDiseaseOccurrence.getDiseaseGroupName();
        DiseaseGroup diseaseGroup = csvLookupData.getDiseaseGroupMap().get(diseaseGroupName.toLowerCase());
        if (diseaseGroup != null) {
            return diseaseGroup;
        }
        throw new DataAcquisitionException(String.format(DISEASE_GROUP_NAME_INVALID_MESSAGE, diseaseGroupName));
    }

    private DateTime convertOccurrenceDate(String occurrenceDate) {
        // Try to parse the occurrence date using each formatter, until the parse is successful
        for (DateTimeFormatter formatter : occurrenceDateTimeFormatters) {
            try {
                return formatter.parseDateTime(occurrenceDate);
            } catch (IllegalArgumentException e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
                // Parsing failed for this formatter, so continue the loop
            }
        }

        // Parsing failed for all formatters
        String validFormats = StringUtils.collectionToDelimitedString(Arrays.asList(OCCURRENCE_DATE_FORMATS), ", ");
        throw new DataAcquisitionException(String.format(OCCURRENCE_DATE_INVALID_MESSAGE, occurrenceDate,
                validFormats));
    }

    private LocationPrecision convertPrecision(String precision) {
        try {
            return LocationPrecision.valueOf(precision.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DataAcquisitionException(String.format(LOCATION_PRECISION_INVALID_MESSAGE, precision));
        }
    }

    private int convertCountry(String countryName) {
        Country country = csvLookupData.getCountryMap().get(countryName.toLowerCase());
        if (country != null) {
            return country.getGaulCode();
        }
        throw new DataAcquisitionException(String.format(COUNTRY_NAME_INVALID_MESSAGE, countryName));
    }
}
