package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseGroupDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.ManualValidationEnforcer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the CsvDataAcquirer class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDataAcquirerIntegrationTest extends AbstractDataAcquisitionSpringIntegrationTests {
    private static final String CSV_HEADER = "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Feed,Summary,URL,Alert Title\n";
    private static final String CSV_OCCURRENCE1 = "\"Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia\",101.7,3.16667,precise,Malaysia,dengue,10/3/2014,\"SEEG Data 2014\",,onm.php?id=XX_ALERT_ID_XX,\"Dengue -- Kuala Lumpur, Malaysia\"\n";
    private static final String CSV_OCCURRENCE2 = "New Zealand,176.61475,-38.53923,Country,New Zealand,dengue,13/01/2014,SEEG Data 2014,\"SPC says the number of dengue fever outbreaks in the Paific over the past year is unprecedented and more research needs to be done into its cause. Duration: 3′ 21″. Play now; Download: Ogg | MP3 ;...\",,Regional dengue outbreak unprecedented - SPC - Radio New Zealand\n";
    private static final String TEST_FOLDER = "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/acquirers/csv";
    private static final String TEST_ISO_8859_1_FILE = "dengue_iso-8859-1.csv";

    @Autowired
    private CsvDataAcquirer csvDataAcquirer;

    @ReplaceWithMock
    @Autowired
    private ManualValidationEnforcer manualValidationEnforcer;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Before
    public void setup() {
        // Sun, 27 Apr 2014 09:45:41
        // CSV_OCCURRENCE1, CSV_OCCURRENCE2 and TEST_ISO_8859_1_FILE all have occurrence dates less than 1 year prior.
        DateTimeUtils.setCurrentMillisFixed(1398591941000L);
    }

    @Test
    public void acquireIsSuccessful() {
        List<DiseaseOccurrence> occurrences = acquire(false, false, null);
        assertNormalValidationParameters(occurrences.get(0));
        assertNormalValidationParameters(occurrences.get(1));
    }

    @Test
    public void acquireGoldStandardIsSuccessful() {
        List<DiseaseOccurrence> occurrences = acquire(false, true, null);
        assertGoldStandardValidationParameters(occurrences.get(0));
        assertGoldStandardValidationParameters(occurrences.get(1));
    }

    @Test
    public void acquireBiasIsSuccessful() {
        DiseaseGroup disease = diseaseGroupDao.getById(87);
        List<DiseaseOccurrence> occurrences = acquire(true, true, disease);
        assertBiasParameters(occurrences.get(0), disease);
        assertBiasParameters(occurrences.get(1), disease);
    }

    @Test
    public void acquireISO88591FileIsSuccessful() throws IOException {
        // Arrange
        byte[] csv = FileUtils.readFileToByteArray(new File(TEST_FOLDER, TEST_ISO_8859_1_FILE));

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv, false, false, null);

        // Assert
        assertThat(messages).hasSize(3);
        // Windows-1252 is a superset of ISO-8859-1
        assertThat(messages.get(0)).isEqualTo("Detected character set windows-1252, converting to UTF-8.");
        assertThat(messages.get(1)).isEqualTo("Found 1 CSV file line(s) to convert.");
        // If the CSV file was saved without error, the special character in Côte d'Ivoire was interpreted correctly.
        // We don't check location.country_gaul_code as it will be null if shapefile geometries are not in the database.
        assertThat(messages.get(2)).contains("Saved 1 disease occurrence(s) in 1 location(s)");
    }

    @Test
    public void acquireFailsOnFirstAndThirdLines() {
        // Arrange
        String csvString = CSV_HEADER + "Test site 1\n" + CSV_OCCURRENCE1 + "Test site 2, 20.5\n";
        byte[] csv = csvString.getBytes();

        // Act
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv, false, false, null);

        // Assert
        assertThat(messages).hasSize(4);
        assertThat(messages.get(0)).isEqualTo("Found 3 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo("Saved 1 disease occurrence(s) in 1 location(s) (of which 1 location(s) passed QC).");
        assertThat(messages.get(2)).isEqualTo("Error in CSV file on line 2: Longitude is missing.");
        assertThat(messages.get(3)).isEqualTo("Error in CSV file on line 4: Latitude is missing.");
    }

    private void assertNormalValidationParameters(DiseaseOccurrence occurrence) {
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getAlert().getFeed().getProvenance().getName()).isEqualTo("Manual dataset");
        assertThat(occurrence.getBiasDisease()).isNull();
    }

    private void assertGoldStandardValidationParameters(DiseaseOccurrence occurrence) {
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(1.0);
        assertThat(occurrence.getFinalWeighting()).isEqualTo(1.0);
        assertThat(occurrence.getAlert().getFeed().getProvenance().getName()).isEqualTo("Manual gold standard dataset");
        assertThat(occurrence.getBiasDisease()).isNull();
    }

    private void assertBiasParameters(DiseaseOccurrence occurrence, DiseaseGroup biasDisease) {
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.BIAS);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getAlert().getFeed().getProvenance().getName()).isEqualTo("Manual dataset");
        assertThat(occurrence.getBiasDisease()).isEqualTo(biasDisease);
    }

    private List<DiseaseOccurrence> acquire(boolean isBias, boolean isGoldStandard, DiseaseGroup biasDisease) {
        String csvString = CSV_HEADER + CSV_OCCURRENCE1 + CSV_OCCURRENCE2;
        byte[] csv = csvString.getBytes();
        List<String> messages = csvDataAcquirer.acquireDataFromCsv(csv, isBias, isGoldStandard, biasDisease);
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo("Found 2 CSV file line(s) to convert.");
        assertThat(messages.get(1)).isEqualTo(
                "Saved 2 disease occurrence(s) in 2 location(s) (of which 2 location(s) passed QC).");

        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertFirstOccurrence(occurrences.get(0));
        assertSecondOccurrence(occurrences.get(1));
        return occurrences;
    }

    private void assertFirstOccurrence(DiseaseOccurrence occurrence) {
        Location occurrence1Location = occurrence.getLocation();
        assertThat(occurrence1Location.getName()).isEqualTo("Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia");
        assertThat(occurrence1Location.getGeom().getX()).isEqualTo(101.7);
        assertThat(occurrence1Location.getGeom().getY()).isEqualTo(3.16667);
        assertThat(occurrence1Location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        assertThat(occurrence1Location.getGeoNameId()).isNull();
        assertThat(occurrence1Location.getHealthMapCountryId()).isNull();
        assertThat(occurrence1Location.getCreatedDate()).isNotNull();
        assertThat(occurrence1Location.hasPassedQc()).isTrue();
        assertThat(occurrence1Location.getAdminUnitQCGaulCode()).isNull();
        assertThat(occurrence1Location.getAdminUnitGlobalGaulCode()).isEqualTo(153);
        assertThat(occurrence1Location.getAdminUnitTropicalGaulCode()).isEqualTo(153);
        assertThat(occurrence1Location.getCountryGaulCode()).isEqualTo(153);
        assertThat(occurrence1Location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or " +
                "ADMIN2. QC stage 2 passed: location already within land. QC stage 3 passed: location already " +
                "within country.");

        Alert occurrence1Alert = occurrence.getAlert();
        assertThat(occurrence1Alert.getFeed().getName()).isEqualTo("SEEG Data 2014");
        assertThat(occurrence1Alert.getPublicationDate()).isNull();
        assertThat(occurrence1Alert.getHealthMapAlertId()).isNull();
        assertThat(occurrence1Alert.getUrl()).isEqualTo("onm.php?id=XX_ALERT_ID_XX");
        assertThat(occurrence1Alert.getSummary()).isNull();
        assertThat(occurrence1Alert.getTitle()).isEqualTo("Dengue -- Kuala Lumpur, Malaysia");
        assertThat(occurrence1Alert.getCreatedDate()).isNotNull();

        DiseaseGroup occurrence1DiseaseGroup = occurrence.getDiseaseGroup();
        assertThat(occurrence1DiseaseGroup.getName()).isEqualTo("Dengue");

        assertThat(occurrence.getOccurrenceDate().getMillis()).isEqualTo(
                new DateTime("2014-03-10T00:00:00Z").getMillis());
        assertThat(occurrence.getCreatedDate()).isNotNull();
    }

    private void assertSecondOccurrence(DiseaseOccurrence occurrence) {
        Location occurrence2Location = occurrence.getLocation();
        assertThat(occurrence2Location.getName()).isEqualTo("New Zealand");
        assertThat(occurrence2Location.getGeom().getX()).isEqualTo(176.61475);
        assertThat(occurrence2Location.getGeom().getY()).isEqualTo(-38.53923);
        assertThat(occurrence2Location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
        assertThat(occurrence2Location.getGeoNameId()).isNull();
        assertThat(occurrence2Location.getHealthMapCountryId()).isNull();
        assertThat(occurrence2Location.getCreatedDate()).isNotNull();
        assertThat(occurrence2Location.hasPassedQc()).isTrue();
        assertThat(occurrence2Location.getAdminUnitQCGaulCode()).isNull();
        assertThat(occurrence2Location.getAdminUnitGlobalGaulCode()).isEqualTo(179);
        assertThat(occurrence2Location.getAdminUnitTropicalGaulCode()).isEqualTo(179);
        assertThat(occurrence2Location.getCountryGaulCode()).isEqualTo(179);
        assertThat(occurrence2Location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or " +
                "ADMIN2. QC stage 2 passed: location already within land. QC stage 3 passed: location already within " +
                "country.");

        Alert occurrence2Alert = occurrence.getAlert();
        assertThat(occurrence2Alert.getFeed().getName()).isEqualTo("SEEG Data 2014");
        assertThat(occurrence2Alert.getPublicationDate()).isNull();
        assertThat(occurrence2Alert.getHealthMapAlertId()).isNull();
        assertThat(occurrence2Alert.getUrl()).isNull();
        assertThat(occurrence2Alert.getSummary()).isEqualTo("SPC says the number of dengue fever outbreaks in the" +
                " Paific over the past year is unprecedented and more research needs to be done into its cause. D" +
                "uration: 3′ 21″. Play now; Download: Ogg | MP3 ;...");
        assertThat(occurrence2Alert.getTitle()).isEqualTo("Regional dengue outbreak unprecedented - SPC - Radio New" +
                " Zealand");
        assertThat(occurrence2Alert.getCreatedDate()).isNotNull();

        DiseaseGroup occurrence2DiseaseGroup = occurrence.getDiseaseGroup();
        assertThat(occurrence2DiseaseGroup.getName()).isEqualTo("Dengue");

        assertThat(occurrence.getOccurrenceDate().getMillis()).isEqualTo(
                new DateTime("2014-01-13T00:00:00Z").getMillis());
        assertThat(occurrence.getCreatedDate()).isNotNull();
    }

    private List<DiseaseOccurrence> getLastTwoDiseaseOccurrences() {
        List<DiseaseOccurrence> diseaseOccurrences = diseaseOccurrenceDao.getAll();
        Collections.sort(diseaseOccurrences, new Comparator<DiseaseOccurrence>() {
            @Override
            public int compare(DiseaseOccurrence o1, DiseaseOccurrence o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        int size = diseaseOccurrences.size();
        assertThat(size).isGreaterThanOrEqualTo(2);
        return Arrays.asList(diseaseOccurrences.get(size - 2), diseaseOccurrences.get(size - 1));
    }
}
