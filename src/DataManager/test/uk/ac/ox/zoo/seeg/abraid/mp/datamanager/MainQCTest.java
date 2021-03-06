package uk.ac.ox.zoo.seeg.abraid.mp.datamanager;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.GeoNameDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GeoName;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.startsWith;
import static org.mockito.Mockito.when;

/**
 * Performs QC tests on the Main class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainQCTest extends AbstractWebServiceClientIntegrationTests {
    public static final String GEONAMES_URL_PREFIX = "http://api.geonames.org/getJSON?username=fakekey&geonameId=";

    @Autowired
    private ApplicationContext applicationContext;

    @ReplaceWithMock
    @Autowired
    private WebServiceClient webServiceClient;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private GeoNameDao geoNameDao;

    @Before
    public void setup() {
        // Mon, 27 Jan 2014 12:12:21
        DateTimeUtils.setCurrentMillisFixed(1390824741000L);
    }

    @Test
    public void mainMethodAcquiresDataFromFiles() {
        // Arrange
        String[] fileNames = {
                "DataManager/test/uk/ac/ox/zoo/seeg/abraid/mp/datamanager/healthmap_json_qc.txt"
        };
        mockGeoNamesRequests();

        // Act
        Main.runMain(applicationContext, fileNames);

        // Assert
        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertFirstLocation(occurrences.get(0));
        assertSecondLocation(occurrences.get(1));
    }

    private void mockGeoNamesRequests() {
        when(webServiceClient.makeGetRequest(startsWith(GEONAMES_URL_PREFIX + "2638384")))
                .thenReturn(getGeoNamesJson(2638384, "ADM2"));
    }

    private void assertFirstLocation(DiseaseOccurrence occurrence) {
        Location occurrence1Location = occurrence.getLocation();
        assertThat(occurrence1Location.getName()).isEqualTo("Bremen, Germany");
        assertThat(occurrence1Location.getPrecision()).isEqualTo(LocationPrecision.ADMIN1);
        assertThat(occurrence1Location.getAdminUnitQCGaulCode()).isEqualTo(1312);
        assertThat(occurrence1Location.getQcMessage()).isEqualTo("QC stage 1 passed: closest distance is 16.09% of " +
                "the square root of the area. QC stage 2 passed: location already within land. QC stage 3 passed: " +
                "location already within country.");
    }

    private void assertSecondLocation(DiseaseOccurrence occurrence) {
        Location occurrence2Location = occurrence.getLocation();
        assertThat(occurrence2Location.getName()).isEqualTo("Isles of Scilly, England, United Kingdom");
        assertThat(occurrence2Location.getPrecision()).isEqualTo(LocationPrecision.ADMIN2);
        assertThat(occurrence2Location.getAdminUnitQCGaulCode()).isNull();
        assertThat(occurrence2Location.getQcMessage()).isEqualTo("QC stage 1 failed: closest distance is 196.04% of " +
                "the square root of the area (GAUL code 40101: \"Cornwall\").");

        assertThatGeoNameExists(2638384, "ADM2");
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

    private void assertThatGeoNameExists(int id, String featureCode) {
        GeoName geoName = geoNameDao.getById(id);
        assertThat(geoName).isNotNull();
        assertThat(geoName.getId()).isEqualTo(id);
        assertThat(geoName.getFeatureCode()).isEqualTo(featureCode);
    }

    private String getGeoNamesJson(Integer geoNameId, String featureCode) {
        return "{\n" +
                "\"fcode\": \"" + featureCode + "\",\n" +
                "\"geonameId\": " + geoNameId.toString() + "\n" +
                "}\n";
    }
}
