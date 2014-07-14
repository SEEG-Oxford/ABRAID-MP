package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseGroupDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.GeoNameDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunRequesterException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.io.File;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * Tests the Main class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainTest extends AbstractWebServiceClientIntegrationTests {
    public static final String HEALTHMAP_URL_PREFIX = "http://healthmap.org";
    public static final String GEONAMES_URL_PREFIX = "http://api.geonames.org/getJSON?username=edwiles&geonameId=";
    public static final String MODELWRAPPER_URL_PREFIX = "http://username:password@localhost:8080/modelwrapper";
    public static final String LARGE_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_large_double.tif";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private GeoNameDao geoNameDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ModelRunService modelRunService;

    @Test
    public void mainMethodAcquiresDataFromWebService() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        mockHealthMapRequest();
        mockGeoNamesRequests();
        mockModelWrapperRequest();
        createAndSaveTestModelRun(diseaseGroupId);
        insertTestDiseaseExtent(diseaseGroupId, GeometryUtils.createMultiPolygon(getFivePointedPolygon()));
        setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(diseaseGroupId);

        // Act
        runMain(new String[]{});

        // Assert
        assertThatDiseaseOccurrencesAreCorrect();
        assertThatDiseaseOccurrenceValidationParametersAreCorrect();
        assertThatRelevantDiseaseOccurrencesHaveFinalWeightings();
        assertThatModelWrapperWebServiceWasCalledCorrectly();
        assertThatRollbackDidNotOccur();
    }

    private void setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(28);
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(null);
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
        setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(87);

        // Act
        runMain(fileNames);

        // Assert
        assertThatDiseaseOccurrencesAreCorrect();
        assertThatRelevantDiseaseOccurrencesHaveFinalWeightings();
        assertThatModelWrapperWebServiceWasCalledCorrectly();
        assertThatRollbackDidNotOccur();
    }

    @Test
    public void mainMethodRollsBackModelRunIfItFails() {
        // Arrange
        mockHealthMapRequest();
        mockGeoNamesRequests();
        setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(87);
        when(webServiceClient.makePostRequestWithJSON(startsWith(MODELWRAPPER_URL_PREFIX), anyString()))
                .thenThrow(new ModelRunRequesterException("Test message"));

        // Act
        runMain(new String[]{});

        // Assert
        assertThatDiseaseOccurrencesAreCorrect();
        assertThatRollbackOccurred();
    }

    private void runMain(String[] fileNames) {
        Main.runMain(applicationContext, fileNames);
    }

    private void assertThatDiseaseOccurrencesAreCorrect() {
        // Assert that we have created two disease occurrences and they are the correct ones
        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertFirstOccurrence(occurrences.get(0));
        assertSecondOccurrence(occurrences.get(1));
    }

    private void assertThatDiseaseOccurrenceValidationParametersAreCorrect() {
        // Assert that we have created two disease occurrences and they are the correct ones
        List<DiseaseOccurrence> occurrences = getLastTwoDiseaseOccurrences();
        assertThatDiseaseOccurrenceValidationParametersAreCorrect(occurrences.get(0), 0.46, 9936.810453, 0.7, true);
        assertThatDiseaseOccurrenceValidationParametersAreCorrect(occurrences.get(1), 0.49, 15749.275209, 0.7, true);
    }

    private void assertThatDiseaseOccurrenceValidationParametersAreCorrect(DiseaseOccurrence occurrence,
            double environmentalSuitability, double distanceFromDiseaseExtent, double machineWeighting,
            boolean isValidated) {
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability, offset(5e-7));
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent, offset(5e-7));
        assertThat(occurrence.getMachineWeighting()).isEqualTo(machineWeighting, offset(5e-7));
        assertThat(occurrence.isValidated()).isEqualTo(isValidated);
    }

    private void assertThatModelWrapperWebServiceWasCalledCorrectly() {
        // Assert that the model wrapper web service has been called once for dengue (disease group 87), with
        // the specified number of occurrence points and disease extent classes
        verify(modelWrapperWebService, atLeastOnce()).startRun(
                argThat(new DiseaseGroupIdMatcher(87)),
                argThat(new ListSizeMatcher<DiseaseOccurrence>(28)),
                argThat(new MapSizeMatcher<Integer, Integer>(460)));
        verify(webServiceClient, atLeastOnce()).makePostRequestWithJSON(
                startsWith(MODELWRAPPER_URL_PREFIX), anyString());
    }

    private void assertThatRelevantDiseaseOccurrencesHaveFinalWeightings() {
        List<DiseaseOccurrence> diseaseOccurrences = diseaseOccurrenceDao.getAll();
        for (DiseaseOccurrence diseaseOccurrence : diseaseOccurrences) {
            if (diseaseOccurrence.getDiseaseGroup().getId() == 87 &&
                    diseaseOccurrence.isValidated() != null && diseaseOccurrence.isValidated()) {
                assertThat(diseaseOccurrence.getFinalWeighting()).isNotNull();
            }
        }
    }

    private void assertThatRollbackOccurred() {
        assertThat(getIsRollbackOnly()).isTrue();
    }

    private void assertThatRollbackDidNotOccur() {
        assertThat(getIsRollbackOnly()).isFalse();
    }

    private boolean getIsRollbackOnly() {
        return transactionManager.getTransaction(null).isRollbackOnly();
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

    private void assertFirstOccurrence(DiseaseOccurrence occurrence) {
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

    private void assertSecondOccurrence(DiseaseOccurrence occurrence) {
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
        assertThat(occurrence2Alert.getUrl()).isNull();
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
                "\"original_url\": \"\",\n" +
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

    private void createAndSaveTestModelRun(int diseaseGroupId) throws Exception {
        ModelRun modelRun = new ModelRun("test" + diseaseGroupId, diseaseGroupId, DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRunService.saveModelRun(modelRun);

        byte[] gdalRaster = FileUtils.readFileToByteArray(new File(LARGE_RASTER_FILENAME));
        modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), gdalRaster);
    }

    private void insertTestDiseaseExtent(int diseaseGroupId, Geometry geom) {
        executeSQLUpdate("INSERT INTO disease_extent VALUES(:diseaseGroupId, :geom)", "diseaseGroupId", diseaseGroupId,
                "geom", geom);
    }

    private Polygon getFivePointedPolygon() {
        return GeometryUtils.createPolygon(3, 4, 5, 11, 12, 8, 9, 5, 5, 6, 3, 4);
    }

    /**
     * Matches a DiseaseGroup by ID.
     */
    private class DiseaseGroupIdMatcher extends ArgumentMatcher<DiseaseGroup> {
        private Integer expectedId;

        public DiseaseGroupIdMatcher(int expectedId) {
            this.expectedId = expectedId;
        }

        @Override
        public boolean matches(Object actualDiseaseGroup) {
            return expectedId.equals(((DiseaseGroup) actualDiseaseGroup).getId());
        }
    }

    /**
     * Used to assert the size of a list that is passed in as a parameter.
     */
    private class ListSizeMatcher<T> extends ArgumentMatcher<List<T>> {
        private Integer expectedSize;

        public ListSizeMatcher(int expectedSize) {
            this.expectedSize = expectedSize;
        }

        @Override
        public boolean matches(Object actualCollection) {
            return expectedSize.equals(((Collection) actualCollection).size());
        }
    }

    /**
     * Used to assert the size of a map that is passed in as a parameter.
     */
    private class MapSizeMatcher<K, V> extends ArgumentMatcher<Map<K, V>> {
        private Integer expectedSize;

        public MapSizeMatcher(int expectedSize) {
            this.expectedSize = expectedSize;
        }

        @Override
        public boolean matches(Object actualMap) {
            return expectedSize.equals(((Map) actualMap).size());
        }
    }
}
