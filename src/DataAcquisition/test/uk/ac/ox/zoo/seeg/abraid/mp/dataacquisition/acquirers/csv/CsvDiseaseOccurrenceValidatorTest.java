package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CsvDiseaseOccurrenceValidator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrenceValidatorTest {
    @Test
    public void validatorIsSuccessfulWithValidOccurrence() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        CsvDiseaseOccurrenceValidator validator = new CsvDiseaseOccurrenceValidator(occurrence);
        validator.validate();
    }

    @Test
    public void validatorFailsIfSiteIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setSite("");
        validateAndExpectFailure(occurrence, "Site is missing");
    }

    @Test
    public void validatorFailsIfSiteIsTooLong() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setSite(fillString(1001));
        validateAndExpectFailure(occurrence, "Site is too long (maximum 1000 characters)");
    }

    @Test
    public void validatorFailsIfLongitudeIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setLongitude(null);
        validateAndExpectFailure(occurrence, "Longitude is missing");
    }

    @Test
    public void validatorFailsIfLongitudeIsTooLow() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setLongitude(-180.00001);
        validateAndExpectFailure(occurrence, "Longitude is -180.00001, which is out of range (-180.00000 to 180.00000)");
    }

    @Test
    public void validatorFailsIfLongitudeIsTooHigh() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setLongitude(180.00001);
        validateAndExpectFailure(occurrence, "Longitude is 180.00001, which is out of range (-180.00000 to 180.00000)");
    }

    @Test
    public void validatorFailsIfLatitudeIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setLatitude(null);
        validateAndExpectFailure(occurrence, "Latitude is missing");
    }

    @Test
    public void validatorFailsIfLatitudeIsTooLow() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setLatitude(-90.00001);
        validateAndExpectFailure(occurrence, "Latitude is -90.00001, which is out of range (-90.00000 to 90.00000)");
    }

    @Test
    public void validatorFailsIfLatitudeIsTooHigh() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setLatitude(90.00001);
        validateAndExpectFailure(occurrence, "Latitude is 90.00001, which is out of range (-90.00000 to 90.00000)");
    }

    @Test
    public void validatorFailsIfPrecisionIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setPrecision(null);
        validateAndExpectFailure(occurrence, "Precision is missing");
    }

    @Test
    public void validatorFailsIfCountryNameIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setCountryName("  ");
        validateAndExpectFailure(occurrence, "Country is missing");
    }

    @Test
    public void validatorFailsIfDiseaseGroupNameIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setDiseaseGroupName("  ");
        validateAndExpectFailure(occurrence, "Disease is missing");
    }

    @Test
    public void validatorFailsIfOccurrenceDateIsMissing() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setOccurrenceDate("  ");
        validateAndExpectFailure(occurrence, "Occurrence date is missing");
    }

    @Test
    public void validatorFailsIfUrlIsTooLong() {
        CsvDiseaseOccurrence occurrence = createValidCsvDiseaseOccurrence();
        occurrence.setUrl(fillString(2001));
        validateAndExpectFailure(occurrence, "URL is too long (maximum 2000 characters)");
    }

    private void validateAndExpectFailure(CsvDiseaseOccurrence occurrence, String errorMessage) {
        CsvDiseaseOccurrenceValidator validator = new CsvDiseaseOccurrenceValidator(occurrence);
        catchException(validator).validate();
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException()).hasMessage(errorMessage);
    }

    private CsvDiseaseOccurrence createValidCsvDiseaseOccurrence() {
        CsvDiseaseOccurrence occurrence = new CsvDiseaseOccurrence();
        occurrence.setSite(fillString(1000));
        occurrence.setLongitude(51.75042);
        occurrence.setLatitude(-1.24759);
        occurrence.setPrecision(LocationPrecision.PRECISE.name());
        occurrence.setCountryName("United Kingdom");
        occurrence.setDiseaseGroupName("Dengue");
        occurrence.setOccurrenceDate("10/09/2014");
        occurrence.setFeedName("SEEG Data 2014");
        occurrence.setAlertTitle("Disease occurrence title");
        return occurrence;
    }

    private String fillString(int numberOfCharacters) {
        return new String(new char[numberOfCharacters]).replace("\0", "A");
    }
}
