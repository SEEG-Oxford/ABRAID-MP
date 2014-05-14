package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.GeoNameDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.startsWith;
import static org.mockito.Mockito.when;

/**
 * Tests the Main class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainTest extends AbstractWebServiceClientIntegrationTests {
    public static final String HEALTHMAP_URL_PREFIX = "http://healthmap.org";
    public static final String GEONAMES_URL_PREFIX = "http://api.geonames.org/getJSON?username=edwiles&geonameId=";
    public static final String MODELWRAPPER_URL_PREFIX = "http://username:password@localhost:8080/ModelWrapper";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private GeoNameDao geoNameDao;

    @Test
    public void mainMethodAcquiresDataFromWebService() {
        // Arrange
        mockHealthMapRequest();
        mockGeoNamesRequests();
        mockModelWrapperRequest();

        // Act
        Main.runMain(applicationContext, new String[] {});

        // Assert
        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertFirstLocation(occurrences.get(0));
        assertSecondLocation(occurrences.get(1));
    }

    @Test
    public void mainMethodAcquiresDataFromFiles() {
        // Arrange
        String[] fileNames = {
                "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap_json1.txt",
                "DataAcquisition/test/uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/healthmap_json2.txt"
        };
        mockGeoNamesRequests();
        mockModelWrapperRequest();

        // Act
        Main.runMain(applicationContext, fileNames);

        // Assert
        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertFirstLocation(occurrences.get(0));
        assertSecondLocation(occurrences.get(1));
    }

    private void mockHealthMapRequest() {
        when(webServiceClient.makeGetRequest(startsWith(HEALTHMAP_URL_PREFIX))).thenReturn(getHealthMapJson());
    }

    private void mockGeoNamesRequests() {
        when(webServiceClient.makeGetRequest(startsWith(GEONAMES_URL_PREFIX + "1735161")))
                .thenReturn(getGeoNamesJson(1735161, "PPLC"));
        when(webServiceClient.makeGetRequest(startsWith(GEONAMES_URL_PREFIX + "2186224")))
                .thenReturn(getGeoNamesJson(2186224, "PCLI"));
    }

    private void mockModelWrapperRequest() {
        when(webServiceClient.makePostRequestWithJSON(startsWith(MODELWRAPPER_URL_PREFIX), anyString()))
                .thenReturn("{\"modelRunName\":\"testname\"}");
    }

    private void assertFirstLocation(DiseaseOccurrence occurrence) {
        Location occurrence1Location = occurrence.getLocation();
        assertThat(occurrence1Location.getName()).isEqualTo("Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia");
        assertThat(occurrence1Location.getGeom().getX()).isEqualTo(101.7);
        assertThat(occurrence1Location.getGeom().getY()).isEqualTo(3.16667);
        assertThat(occurrence1Location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        assertThat(occurrence1Location.getGeoNameId()).isEqualTo(1735161);
        assertThat(occurrence1Location.getHealthMapCountryId()).isEqualTo(147);
        assertThat(occurrence1Location.getCreatedDate()).isNotNull();
        assertThat(occurrence1Location.hasPassedQc()).isTrue();
        assertThat(occurrence1Location.getAdminUnitQCGaulCode()).isNull();
        assertThat(occurrence1Location.getAdminUnitGlobalGaulCode()).isEqualTo(153);
        assertThat(occurrence1Location.getAdminUnitTropicalGaulCode()).isEqualTo(153);
        assertThat(occurrence1Location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or " +
                "ADMIN2. QC stage 2 passed: location already within land. QC stage 3 passed: location already " +
                "within HealthMap country.");

        assertThatGeoNameExists(1735161, "PPLC");

        Alert occurrence1Alert = occurrence.getAlert();
        assertThat(occurrence1Alert.getFeed().getName()).isEqualTo("Eyewitness Reports");
        assertThat(occurrence1Alert.getFeed().getHealthMapFeedId()).isEqualTo(34);
        assertThat(occurrence1Alert.getFeed().getLanguage()).isEqualTo("my");
        assertThat(occurrence1Alert.getPublicationDate()).isEqualTo(new DateTime("2014-03-10T04:00:00+0000"));
        assertThat(occurrence1Alert.getHealthMapAlertId()).isEqualTo(2324002);
        assertThat(occurrence1Alert.getUrl()).isEqualTo("onm.php?id=XX_ALERT_ID_XX");
        assertThat(occurrence1Alert.getSummary()).isNullOrEmpty();
        assertThat(occurrence1Alert.getTitle()).isEqualTo("Dengue -- Kuala Lumpur, Malaysia");
        assertThat(occurrence1Alert.getCreatedDate()).isNotNull();

        DiseaseGroup occurrence1DiseaseGroup = occurrence.getDiseaseGroup();
        assertThat(occurrence1DiseaseGroup.getName()).isEqualTo("Dengue");

        assertThat(occurrence.getOccurrenceDate()).isEqualTo(new DateTime("2014-03-10T04:00:00+0000"));
        assertThat(occurrence.getCreatedDate()).isNotNull();
    }

    private void assertSecondLocation(DiseaseOccurrence occurrence) {
        Location occurrence2Location = occurrence.getLocation();
        assertThat(occurrence2Location.getName()).isEqualTo("New Zealand");
        assertThat(occurrence2Location.getGeom().getX()).isEqualTo(172.65939);
        assertThat(occurrence2Location.getGeom().getY()).isEqualTo(-42.42349);
        assertThat(occurrence2Location.getPrecision()).isEqualTo(LocationPrecision.COUNTRY);
        assertThat(occurrence2Location.getGeoNameId()).isEqualTo(2186224);
        assertThat(occurrence2Location.getHealthMapCountryId()).isEqualTo(164);
        assertThat(occurrence2Location.getCreatedDate()).isNotNull();
        assertThat(occurrence2Location.hasPassedQc()).isTrue();
        assertThat(occurrence2Location.getAdminUnitQCGaulCode()).isNull();
        assertThat(occurrence2Location.getAdminUnitGlobalGaulCode()).isEqualTo(179);
        assertThat(occurrence2Location.getAdminUnitTropicalGaulCode()).isEqualTo(179);
        assertThat(occurrence2Location.getQcMessage()).isEqualTo("QC stage 1 passed: location not an ADMIN1 or " +
                "ADMIN2. QC stage 2 passed: location is a country. QC stage 3 passed: location already " +
                "within HealthMap country.");

        assertThatGeoNameExists(2186224, "PCLI");

        Alert occurrence2Alert = occurrence.getAlert();
        assertThat(occurrence2Alert.getFeed().getName()).isEqualTo("Google News");
        assertThat(occurrence2Alert.getFeed().getHealthMapFeedId()).isEqualTo(4);
        assertThat(occurrence2Alert.getFeed().getLanguage()).isNull();
        assertThat(occurrence2Alert.getPublicationDate()).isEqualTo(new DateTime("2014-03-10T02:50:58+0000"));
        assertThat(occurrence2Alert.getHealthMapAlertId()).isEqualTo(2323248);
        assertThat(occurrence2Alert.getUrl()).isEqualTo("http://news.google.com/news/url?sa=t&fd=R&usg=AFQjCNF4EFD" +
                "nuQ1IvVKEkyHzR4WL8Uf0mQ&cid=c3a7d30bb8a4878e06b80cf16b898331&url=http://www.radionz.co.nz/interna" +
                "tional/programmes/datelinepacific/audio/2588433/regional-dengue-outbreak-unprecedented-spc");
        assertThat(occurrence2Alert.getSummary()).isEqualTo("SPC says the number of dengue fever outbreaks in the" +
                " Paific over the past year is unprecedented and more research needs to be done into its cause. D" +
                "uration: 3′ 21″. Play now; Download: Ogg | MP3 ;...");
        assertThat(occurrence2Alert.getTitle()).isEqualTo("Regional dengue outbreak unprecedented - SPC - Radio New" +
                " Zealand");
        assertThat(occurrence2Alert.getCreatedDate()).isNotNull();

        DiseaseGroup occurrence2DiseaseGroup = occurrence.getDiseaseGroup();
        assertThat(occurrence2DiseaseGroup.getName()).isEqualTo("Dengue");

        assertThat(occurrence.getOccurrenceDate()).isEqualTo(new DateTime("2014-03-10T02:50:58+0000"));
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

    private void assertThatGeoNameExists(int id, String featureCode) {
        GeoName geoName = geoNameDao.getById(id);
        assertThat(geoName).isNotNull();
        assertThat(geoName.getId()).isEqualTo(id);
        assertThat(geoName.getFeatureCode()).isEqualTo(featureCode);
    }

    private String getHealthMapJson() {
        return "[\n" +
                "{\n" +
                "\"country\": \"Malaysia\",\n" +
                "\"place_name\": \"Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia\",\n" +
                "\"lat\": \"3.166667\",\n" +
                "\"lng\": \"101.699997\",\n" +
                "\"geonameid\": \"1735161\",\n" +
                "\"place_basic_type\": \"p\",\n" +
                "\"place_id\": \"3350\",\n" +
                "\"country_id\": \"147\",\n" +
                "\"alerts\": [\n" +
                "{\n" +
                "\"feed\": \"Eyewitness Reports\",\n" +
                "\"disease\": \"Dengue\",\n" +
                "\"summary\": \"Dengue -- Kuala Lumpur, Malaysia\",\n" +
                "\"date\": \"2014-03-10 00:00:00-0400\",\n" +
                "\"formatted_date\": \"10 March 2014 00:00:00 EDT\",\n" +
                "\"link\": \"http://healthmap.org/ln.php?2324002\",\n" +
                "\"descr\": \"\",\n" +
                "\"rating\": {\n" +
                "\"count\": 0,\n" +
                "\"rating\": 3\n" +
                "},\n" +
                "\"species_name\": \"Humans\",\n" +
                "\"dup_count\": \"0\",\n" +
                "\"place_category\": [],\n" +
                "\"original_url\": \"onm.php?id=XX_ALERT_ID_XX\",\n" +
                "\"disease_id\": \"33\",\n" +
                "\"feed_id\": \"34\",\n" +
                "\"feed_lang\": \"my\"\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "{\n" +
                "\"country\": \"New Zealand\",\n" +
                "\"place_name\": \"New Zealand\",\n" +
                "\"lat\": \"-42.423489\",\n" +
                "\"lng\": \"172.659393\",\n" +
                "\"geonameid\": \"2186224\",\n" +
                "\"place_basic_type\": \"c\",\n" +
                "\"place_id\": \"164\",\n" +
                "\"country_id\": \"164\",\n" +
                "\"alerts\": [\n" +
                "{\n" +
                "\"feed\": \"Google News\",\n" +
                "\"disease\": \"Dengue\",\n" +
                "\"summary\": \"Regional dengue outbreak unprecedented - SPC - Radio New Zealand\",\n" +
                "\"date\": \"2014-03-09 22:50:58-0400\",\n" +
                "\"formatted_date\": \" 9 March 2014 22:50:58 EDT\",\n" +
                "\"link\": \"http://healthmap.org/ln.php?2323248\",\n" +
                "\"descr\": \"SPC says the number of dengue fever outbreaks in the Paific over the past year is " +
                "unprecedented and more research needs to be done into its cause. Duration: 3′ 21″. Play now; Dow" +
                "nload: Ogg | MP3 ;...\",\n" +
                "\"rating\": {\n" +
                "\"count\": 0,\n" +
                "\"rating\": 3\n" +
                "},\n" +
                "\"species_name\": \"Humans\",\n" +
                "\"dup_count\": \"0\",\n" +
                "\"place_category\": [],\n" +
                "\"original_url\": \"http://news.google.com/news/url?sa=t&fd=R&usg=AFQjCNF4EFDnuQ1IvVKEkyHzR4WL8Uf" +
                "0mQ&cid=c3a7d30bb8a4878e06b80cf16b898331&url=http://www.radionz.co.nz/international/programmes/" +
                "datelinepacific/audio/2588433/regional-dengue-outbreak-unprecedented-spc\",\n" +
                "\"disease_id\": \"33\",\n" +
                "\"feed_id\": \"4\"\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "]";
    }

    private String getGeoNamesJson(Integer geoNameId, String featureCode) {
        return "{\n" +
                "\"fcode\": \"" + featureCode + "\",\n" +
                "\"geonameId\": " + geoNameId.toString() + "\n" +
                "}\n";
    }
}
