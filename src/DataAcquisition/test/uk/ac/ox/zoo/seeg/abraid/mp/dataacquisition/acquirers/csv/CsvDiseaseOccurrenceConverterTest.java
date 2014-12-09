package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import java.util.HashMap;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the CsvDiseaseOccurrenceConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrenceConverterTest {
    private CsvDiseaseOccurrenceConverter converter;

    private static final String FEED_NAME = "Test feed name";
    private static final String GOLD_STANDARD_FEED_NAME = "Test gold standard feed name";

    @Before
    public void setUp() {
        CsvLookupData csvLookupData = setUpCsvLookupData();
        converter = new CsvDiseaseOccurrenceConverter(csvLookupData);
    }

    private CsvLookupData setUpCsvLookupData() {
        HashMap<String, Country> countryMap = new HashMap<>();
        countryMap.put("france", new Country(85, "France"));
        countryMap.put("venezuela", new Country(263, "Venezuela"));

        HashMap<String, DiseaseGroup> diseaseGroupMap = new HashMap<>();
        diseaseGroupMap.put("dengue", new DiseaseGroup(87, "Dengue"));
        diseaseGroupMap.put("malarias", new DiseaseGroup(202, "Malarias"));

        Feed feed = new Feed(FEED_NAME, new Provenance(ProvenanceNames.MANUAL));
        Feed goldStandardFeed = new Feed(GOLD_STANDARD_FEED_NAME, new Provenance(ProvenanceNames.MANUAL_GOLD_STANDARD));

        CsvLookupData csvLookupData = mock(CsvLookupData.class);
        when(csvLookupData.getCountryMap()).thenReturn(countryMap);
        when(csvLookupData.getDiseaseGroupMap()).thenReturn(diseaseGroupMap);
        when(csvLookupData.getFeedForManuallyUploadedData(FEED_NAME, false)).thenReturn(feed);
        when(csvLookupData.getFeedForManuallyUploadedData(GOLD_STANDARD_FEED_NAME, true)).thenReturn(goldStandardFeed);
        return csvLookupData;
    }

    @Test
    public void convertSucceedsForValidInputWithGoldStandardData() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createGoldStandardCsvDiseaseOccurrence();
        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence, true);
        // Assert
        assertLocation(occurrence);
        assertGoldStandardAlert(occurrence);
        assertDiseaseGroup(occurrence);
    }

    @Test
    public void convertSucceedsForValidInput() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence, false);
        // Assert
        assertLocation(occurrence);
        assertAlert(occurrence);
        assertDiseaseGroup(occurrence);
    }

    private void assertLocation(DiseaseOccurrence occurrence) {
        Location location = occurrence.getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getName()).isEqualTo("Paris");
        assertThat(location.getGeom().getX()).isEqualTo(2.3508);
        assertThat(location.getGeom().getY()).isEqualTo(48.8567);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        assertThat(location.getCountryGaulCode()).isEqualTo(85);
    }

    private void assertGoldStandardAlert(DiseaseOccurrence occurrence) {
        Alert alert = occurrence.getAlert();
        assertThat(alert).isNotNull();
        assertThat(alert.getFeed()).isNotNull();
        assertThat(alert.getFeed().getName()).isEqualTo(GOLD_STANDARD_FEED_NAME);
        assertThat(alert.getFeed().getProvenance().getName()).isEqualTo(ProvenanceNames.MANUAL_GOLD_STANDARD);
        assertThat(alert.getTitle()).isEqualTo("Disease occurrence title");
        assertThat(alert.getSummary()).isEqualTo("Disease occurrence summary");
        assertThat(alert.getUrl()).isEqualTo("http://testurl.com");
    }

    private void assertAlert(DiseaseOccurrence occurrence) {
        Alert alert = occurrence.getAlert();
        assertThat(alert).isNotNull();
        assertThat(alert.getFeed()).isNotNull();
        assertThat(alert.getFeed().getName()).isEqualTo(FEED_NAME);
        assertThat(alert.getFeed().getProvenance().getName()).isEqualTo(ProvenanceNames.MANUAL);
        assertThat(alert.getTitle()).isEqualTo("Disease occurrence title");
        assertThat(alert.getSummary()).isEqualTo("Disease occurrence summary");
        assertThat(alert.getUrl()).isEqualTo("http://testurl.com");
    }

    private void assertDiseaseGroup(DiseaseOccurrence occurrence) {
        DiseaseGroup diseaseGroup = occurrence.getDiseaseGroup();
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getId()).isEqualTo(87);
        assertThat(diseaseGroup.getName()).isEqualTo("Dengue");
        assertEqual(occurrence.getOccurrenceDate(), "2014-09-10T00:00:00Z");
    }

    @Test
    public void convertFailsIfValidatorFails() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setSite(null);

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Site is missing");
    }

    @Test
    public void convertFailsIfDiseaseGroupNameIsInvalid() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setDiseaseGroupName("Tonsillitis");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Disease group name \"Tonsillitis\" is invalid");
    }

    @Test
    public void convertSucceedsFor2DigitMonthYearOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("05/2013");

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence, false);

        // Assert
        assertEqual(occurrence.getOccurrenceDate(), "2013-05-01T00:00:00Z");
    }

    @Test
    public void convertSucceedsFor3CharacterMonth2DigitYearOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("May-13");

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence, false);

        // Assert
        assertEqual(occurrence.getOccurrenceDate(), "2013-05-01T00:00:00Z");
    }

    @Test
    public void convertSucceedsFor3CharacterMonth4DigitYearOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("May-2013");

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence, false);

        // Assert
        assertEqual(occurrence.getOccurrenceDate(), "2013-05-01T00:00:00Z");
    }

    @Test
    public void convertSucceedsForYearOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("2012");

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence, false);

        // Assert
        assertEqual(occurrence.getOccurrenceDate(), "2012-01-01T00:00:00Z");
    }

    @Test
    public void convertFailsForInvalidOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("01/13/2013");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Occurrence date \"01/13/2013\" is invalid (valid formats are " +
                "dd/MM/YYYY, MM/YYYY, YYYY, MMM-YY, MMM-YYYY)");
    }

    @Test
    public void convertFailsForInvalidPrecision() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setPrecision("  Street");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Location precision \"Street\" is invalid");
    }

    @Test
    public void convertFailsForInvalidCountryName() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setCountryName("Outer Mongolia");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Country name \"Outer Mongolia\" is invalid");
    }

    private void expectFailure(CsvDiseaseOccurrence csvDiseaseOccurrence, String message) {
        catchException(converter).convert(csvDiseaseOccurrence, false);
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException()).hasMessage(message);
    }

    private CsvDiseaseOccurrence createGoldStandardCsvDiseaseOccurrence() {
        CsvDiseaseOccurrence occurrence = createBaseCsvDiseaseOccurrence();
        occurrence.setFeedName(GOLD_STANDARD_FEED_NAME);
        return occurrence;
    }

    private CsvDiseaseOccurrence createCsvDiseaseOccurrence() {
        CsvDiseaseOccurrence occurrence = createBaseCsvDiseaseOccurrence();
        occurrence.setFeedName(FEED_NAME);
        return occurrence;
    }

    private CsvDiseaseOccurrence createBaseCsvDiseaseOccurrence() {
        CsvDiseaseOccurrence occurrence = new CsvDiseaseOccurrence();
        occurrence.setSite(" Paris ");
        occurrence.setLongitude(2.3508);
        occurrence.setLatitude(48.8567);
        occurrence.setPrecision(LocationPrecision.PRECISE.name());
        occurrence.setCountryName("France");
        occurrence.setDiseaseGroupName("DENGUE");
        occurrence.setOccurrenceDate("10/09/2014");
        occurrence.setAlertTitle("Disease occurrence title");
        occurrence.setSummary("Disease occurrence summary");
        occurrence.setUrl("http://testurl.com");
        return occurrence;
    }

    private void assertEqual(DateTime actualDateTime, String expectedDateTimeISOFormat) {
        long expectedMillis = new DateTime(expectedDateTimeISOFormat).getMillis();
        assertThat(actualDateTime.getMillis()).isEqualTo(expectedMillis);
    }
}
