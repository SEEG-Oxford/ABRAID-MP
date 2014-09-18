package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

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
    @Autowired
    private CsvDataAcquirer csvDataAcquirer;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Test
    public void acquireIsSuccessful() {
        String csv =
                "Site,Longitude,Latitude,Precision,Country,Disease,Occurrence Date,Title,Summary,URL\n" +
                "\"Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia\",101.7,3.16667,precise,Malaysia,dengue,10/3/2014,\"Dengue -- Kuala Lumpur, Malaysia\",,onm.php?id=XX_ALERT_ID_XX\n" +
                "New Zealand,176.61475,-38.53923,Country,New Zealand,dengue,13/01/2014,Regional dengue outbreak unprecedented - SPC - Radio New Zealand,\"SPC says the number of dengue fever outbreaks in the Paific over the past year is unprecedented and more research needs to be done into its cause. Duration: 3′ 21″. Play now; Download: Ogg | MP3 ;...\",\n";

        String message = csvDataAcquirer.acquireDataFromCsv(csv);
        assertThat(message).isEqualTo("Saved 2 disease occurrence(s) in 2 location(s) (of which 2 location(s) passed QC)");

        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertFirstOccurrence(occurrences.get(0));
        assertSecondOccurrence(occurrences.get(1));
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
        assertThat(occurrence1Alert.getFeed().getName()).isEqualTo("Uploaded");
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
        assertThat(occurrence2Alert.getFeed().getName()).isEqualTo("Uploaded");
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
