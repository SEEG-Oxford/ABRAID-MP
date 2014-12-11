package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

/**
 * Validates a CSV disease occurrence.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrenceValidator {
    private static final String FIELD_MISSING = "%s is missing";
    private static final String FIELD_TOO_LONG = "%s is too long (maximum %d characters)";
    private static final String FIELD_OUT_OF_RANGE = "%s is %.5f, which is out of range (%.5f to %.5f)";

    private static final int SITE_MAX_CHARACTERS = 1000;
    private static final int URL_MAX_CHARACTERS = 2000;
    private static final int LONGITUDE_MINIMUM = -180;
    private static final int LONGITUDE_MAXIMUM = 180;
    private static final int LATITUDE_MINIMUM = -90;
    private static final int LATITUDE_MAXIMUM = 90;

    private CsvDiseaseOccurrence occurrence;

    public CsvDiseaseOccurrenceValidator(CsvDiseaseOccurrence occurrence) {
        this.occurrence = occurrence;
    }

    /**
     * Validate the CSV disease occurrence.
     * @throws DataAcquisitionException if the CSV disease occurrence is invalid.
     */
    public void validate() throws DataAcquisitionException {
        validateFieldMissing("Site", occurrence.getSite());
        validateFieldTooLong("Site", occurrence.getSite(), SITE_MAX_CHARACTERS);
        validateFieldMissing("Longitude", occurrence.getLongitude());
        validateFieldOutOfRange("Longitude", occurrence.getLongitude(), LONGITUDE_MINIMUM, LONGITUDE_MAXIMUM);
        validateFieldMissing("Latitude", occurrence.getLatitude());
        validateFieldOutOfRange("Latitude", occurrence.getLatitude(), LATITUDE_MINIMUM, LATITUDE_MAXIMUM);
        validateFieldMissing("Precision", occurrence.getPrecision());
        validateFieldMissing("Country", occurrence.getCountryName());
        validateFieldMissing("Disease", occurrence.getDiseaseGroupName());
        validateFieldMissing("Occurrence date", occurrence.getOccurrenceDate());
        validateFieldMissing("Feed name", occurrence.getFeedName());
        validateFieldTooLong("URL", occurrence.getUrl(), URL_MAX_CHARACTERS);
    }

    private void validateFieldMissing(String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            throw new DataAcquisitionException(String.format(FIELD_MISSING, fieldName));
        }
    }

    private void validateFieldTooLong(String fieldName, String fieldValue, int maxCharacters) {
        if (fieldValue != null && fieldValue.length() > maxCharacters) {
            throw new DataAcquisitionException(String.format(FIELD_TOO_LONG, fieldName, maxCharacters));
        }
    }

    private void validateFieldOutOfRange(String fieldName, Double fieldValue, double minimum, double maximum) {
        if (fieldValue != null && (fieldValue < minimum || fieldValue > maximum)) {
            throw new DataAcquisitionException(String.format(FIELD_OUT_OF_RANGE, fieldName, fieldValue, minimum,
                    maximum));
        }
    }
}
