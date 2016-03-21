package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.kubek2k.springockito.annotations.WrapWithSpy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.GitSourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunOccurrencesSelectorHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClientException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contains integration tests for the ModelRunRequester class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class,
                      locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelRunRequesterIntegrationTest extends AbstractCommonSpringIntegrationTests {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private GeometryService geometryService;

    @ReplaceWithMock
    @Autowired
    protected CovariateService covariateService;

    @ReplaceWithMock
    @Autowired
    protected WebServiceClient webServiceClient;

    @WrapWithSpy
    @Autowired
    protected ModelWrapperWebService modelWrapperWebService;

    @Autowired
    private ModelRunRequester modelRunRequester;

    @Autowired
    private ModelRunDao modelRunDao;

    @Autowired
    private GitSourceCodeManager gitSourceCodeManager;

    @ReplaceWithMock
    @Autowired
    private EmailService emailService;

    @ReplaceWithMock
    @Autowired
    private ConfigurationService configurationService;

    @ReplaceWithMock
    @Autowired
    private RasterFilePathFactory rasterFilePathFactory;

    private static final String DATA_DIR = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/runrequest/data/testdata/";
    private static final String URL = "http://api:key-to-access-model-wrapper@localhost:8080/modelwrapper/api/model/run";

    @Before
    public void before() throws Exception {
        when(configurationService.getModelRepositoryUrl()).thenReturn("https://github.com/SEEG-Oxford/seegSDM.git");
        when(configurationService.getModelRepositoryVersion()).thenReturn("0.1-9");
        gitSourceCodeManager.updateRepository();
        when(rasterFilePathFactory.getAdminRaster(0)).thenReturn(new File(DATA_DIR + "admin/a0.tif"));
        when(rasterFilePathFactory.getAdminRaster(1)).thenReturn(new File(DATA_DIR + "admin/a1.tif"));
        when(rasterFilePathFactory.getAdminRaster(2)).thenReturn(new File(DATA_DIR + "admin/a2.tif"));
        when(rasterFilePathFactory.getExtentGaulRaster(false)).thenReturn(new File(DATA_DIR + "SmallRaster.tif"));
        when(rasterFilePathFactory.getExtentGaulRaster(true)).thenReturn(new File("doesnt exist"));
    }

    @Test
    public void requestModelRunSucceedsWithBatching() throws IOException {
        requestModelRunSucceeds(DateTime.now(), DateTime.now().plusDays(2));
    }

    @Test
    public void requestModelRunSucceedsWithoutBatching() throws IOException {
        requestModelRunSucceeds(null, null);
    }

    private void requestModelRunSucceeds(DateTime batchStartDate, DateTime batchEndDate) throws IOException {
        // Arrange
        int diseaseGroupId = 87;
        setDiseaseGroupParametersToEnsureSelectorReturnsOccurrences(diseaseGroupId);
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        String responseJson = "{}";
        mockPostRequest(responseJson); // Note that this includes code to assert the request JSON

        // Act
        List<DiseaseOccurrence> occurrences = selectOccurrencesForModelRun(diseaseGroupId);
        modelRunRequester.requestModelRun(diseaseGroupId, occurrences, batchStartDate, batchEndDate);

        // Assert
        List<ModelRun> modelRuns = modelRunDao.getAll();
        assertThat(modelRuns).hasSize(1);
        ModelRun modelRun = modelRuns.get(0);
        assertThat(modelRun.getName()).startsWith("deng_");
        assertThat(modelRun.getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(modelRun.getRequestServer()).isEqualTo(URI.create(URL).getHost());
        assertThat(modelRun.getRequestDate()).isEqualTo(now);
        assertThat(modelRun.getBatchStartDate()).isEqualTo(batchStartDate);
        assertThat(modelRun.getBatchEndDate()).isEqualTo(batchEndDate);
        assertThat(modelRun.getOccurrenceDataRangeStartDate().isEqual(DateTime.parse("2014-02-24T17:35:29.000Z"))).isTrue();
        assertThat(modelRun.getOccurrenceDataRangeEndDate().isEqual(DateTime.parse("2014-02-27T08:06:46.000Z"))).isTrue();
    }

    private void setDiseaseGroupParametersToEnsureSelectorReturnsOccurrences(int diseaseGroupId) throws IOException {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(27);
        diseaseGroup.setOccursInAfrica(null);
        diseaseGroup.setModelMode("Bhatt2013");
        List<CovariateFile> covariateFiles = Arrays.asList(
                createMockCovariateFile("a"),
                createMockCovariateFile("b"),
                createMockCovariateFile("c/d")
        );
        createMockCovariateFile("e");
        when(covariateService.getCovariateFilesByDiseaseGroup(diseaseGroup)).thenReturn(covariateFiles);
        when(covariateService.getCovariateDirectory()).thenReturn(testFolder.getRoot().getAbsolutePath());
    }

    private CovariateFile createMockCovariateFile(String path) throws IOException {
        CovariateFile covariateFile = mock(CovariateFile.class);
        CovariateSubFile subObj = mock(CovariateSubFile.class);
        when(subObj.getFile()).thenReturn(path);
        when(covariateFile.getFiles()).thenReturn(Arrays.asList(subObj));
        File file = Paths.get(testFolder.getRoot().getAbsolutePath(), path).toFile();
        FileUtils.writeStringToFile(file, path);
        return covariateFile;
    }

    @Test
    public void requestModelRunWithErrorReturnedByModelThrowsException() throws IOException {
        // Arrange
        int diseaseGroupId = 87;
        setDiseaseGroupParametersToEnsureSelectorReturnsOccurrences(diseaseGroupId);
        String responseJson = "{\"errorText\":\"testerror\"}";
        mockPostRequest(responseJson); // Note that this includes code to assert the request JSON

        // Act
        List<DiseaseOccurrence> occurrences = selectOccurrencesForModelRun(diseaseGroupId);
        catchException(modelRunRequester).requestModelRun(diseaseGroupId, occurrences, null, null);

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
    }

    @Test
    public void requestModelRunWithWebClientExceptionThrowsException() throws IOException {
        // Arrange
        int diseaseGroupId = 87;
        setDiseaseGroupParametersToEnsureSelectorReturnsOccurrences(diseaseGroupId);
        String exceptionMessage = "Web service failed";
        WebServiceClientException thrownException = new WebServiceClientException(exceptionMessage);
        when(webServiceClient.makePostRequestWithJSON(eq(URL), anyString())).thenThrow(thrownException);

        // Act
        List<DiseaseOccurrence> occurrences = selectOccurrencesForModelRun(diseaseGroupId);
        catchException(modelRunRequester).requestModelRun(diseaseGroupId, occurrences, null, null);

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
    }

    @Test
    public void requestModelRunWithNoDiseaseOccurrencesThrowsException() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        catchException(modelRunRequester).requestModelRun(diseaseGroupId, occurrences, null, null);

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        assertThat(caughtException()).hasMessage("Cannot request a model run because there are no occurrences");
    }

    private List<DiseaseOccurrence> selectOccurrencesForModelRun(int diseaseGroupId) {
        ModelRunOccurrencesSelectorHelper selector = new ModelRunOccurrencesSelectorHelper(diseaseService, geometryService,
                emailService, diseaseGroupId, false);
        return selector.selectModelRunDiseaseOccurrences();
    }

    private void mockPostRequest(final String responseJson) {
        when(webServiceClient.makePostRequestWithBinary(eq(URL), any(File.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws IOException, ZipException {
                File data = (File) invocationOnMock.getArguments()[1];
                ZipFile zipFile = new ZipFile(data);
                File unzipped = testFolder.newFile();
                unzipped.delete();
                zipFile.extractAll(unzipped.getAbsolutePath());
                assertRequestMetadataJson(FileUtils.readFileToString(Paths.get(unzipped.getAbsolutePath(), "metadata.json").toFile()));
                assertRequestCovariates(Paths.get(unzipped.getAbsolutePath(), "covariates").toFile());
                assertRequestOccurrenceCsv(Paths.get(unzipped.getAbsolutePath(), "data/occurrences.csv").toFile());
                assertRequestExtentTif(Paths.get(unzipped.getAbsolutePath(), "data/extent.tif").toFile());
                assertRequestAdminRasters(Paths.get(unzipped.getAbsolutePath(), "admins").toFile());
                return responseJson;
            }
        });
    }

    private void assertRequestOccurrenceCsv(File csv) throws IOException {
        List<String> splitFeatures = getSplitFeatures(FileUtils.readFileToString(csv));
        assertSplitFeatures(splitFeatures);
    }

    private void assertRequestMetadataJson(String requestJson) {
        Pattern regexp = Pattern.compile("\\{\"disease\"\\:\\{(.+?)},\"runName\"\\:\"(.+?)\"\\}");
        Matcher matcher = regexp.matcher(requestJson);

        assertThat(matcher.find()).isTrue();
        assertThat(matcher.groupCount()).isEqualTo(2);
        assertThat(matcher.group(1).split(",")).containsOnly("\"id\":87", "\"name\":\"Dengue\"", "\"abbreviation\":\"deng\"", "\"global\":false");
        assertThat(matcher.group(2)).startsWith("deng_");
    }

    private void assertRequestCovariates(File covariatesDir) {
        Collection<File> files = FileUtils.listFiles(covariatesDir, null, true);
        List<File> indexable = new ArrayList<>(files);
        assertThat(files).hasSize(3);
        assertThat(indexable.get(0).getAbsolutePath()).isEqualTo(Paths.get(covariatesDir.getAbsolutePath(), "a").toString());
        assertThat(indexable.get(0)).hasContent("a");
        assertThat(indexable.get(1).getAbsolutePath()).isEqualTo(Paths.get(covariatesDir.getAbsolutePath(), "b").toString());
        assertThat(indexable.get(1)).hasContent("b");
        assertThat(indexable.get(2).getAbsolutePath()).isEqualTo(Paths.get(covariatesDir.getAbsolutePath(), "c", "d").toString());
        assertThat(indexable.get(2)).hasContent("c/d");
    }

    private List<String> getSplitFeatures(String features) {
        return Arrays.asList(features.split("\n"));
    }

    private void assertSplitFeatures(List<String> splitFeatures) {
        assertThat(splitFeatures).hasSize(27 + 1);
        assertThat(splitFeatures).contains(
                "Longitude,Latitude,Weight,Admin,GAUL,Disease,Date",
                "121.06667,14.53333,0.95,-999,NA,87,2014-02-27",
                "-46.60972,-20.71889,0.825,-999,NA,87,2014-02-26",
                "-42.91651,-22.17062,0.775,2,9970,87,2014-02-26",
                "-42.66564,-22.18996,0.675,1,683,87,2014-02-26",
                "-43.04112,-22.81555,0.85,2,9966,87,2014-02-26",
                "-54.66252,-28.05186,0.775,2,10593,87,2014-02-26",
                "-54.0,-30.0,0.625,1,685,87,2014-02-26",
                "-67.81,-9.97472,0.8,-999,NA,87,2014-02-26",
                "-76.42313,8.84621,0.925,-999,NA,87,2014-02-26",
                "73.85674,18.52043,0.975,-999,NA,87,2014-02-26",
                "102.25616,2.20569,0.975,-999,NA,87,2014-02-26",
                "-45.88694,-23.17944,0.8,-999,NA,87,2014-02-25",
                "114.0,1.0,1.0,-999,NA,87,2014-02-25",
                "-47.09179,-21.76979,0.775,-999,NA,87,2014-02-25",
                "-49.06055,-22.31472,0.9,-999,NA,87,2014-02-25",
                "103.80805,1.29162,0.875,-999,NA,87,2014-02-25",
                "126.08934,7.30416,0.7,1,67161,87,2014-02-25",
                "126.33333,7.16667,0.85,2,24269,87,2014-02-25",
                "126.0,7.5,0.75,2,24266,87,2014-02-25",
                "126.17626,7.51252,0.975,-999,NA,87,2014-02-25",
                "-98.28333,26.08333,0.9,-999,NA,87,2014-02-25",
                "39.21917,21.51694,0.85,-999,NA,87,2014-02-24",
                "-51.38889,-22.12556,0.85,-999,NA,87,2014-02-24",
                "177.46666,-17.61667,0.825,-999,NA,87,2014-02-24",
                "177.41667,-17.8,0.925,-999,NA,87,2014-02-24",
                "-61.5,-17.5,0.7,1,40449,87,2014-02-24",
                "-80.63333,-5.2,0.875,-999,NA,87,2014-02-24"
        );
    }

    private void assertRequestExtentTif(File extent) {
        assertThat(extent).hasContentEqualTo(new File(DATA_DIR + "integration_extent.tif"));
    }

    private void assertRequestAdminRasters(File dir) {
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        List<File> indexable = new ArrayList<>(files);
        assertThat(files).hasSize(3);
        assertThat(indexable.get(0).getAbsolutePath()).isEqualTo(Paths.get(dir.toString(), "admin0.tif").toString());
        assertThat(indexable.get(0)).hasContentEqualTo(Paths.get(DATA_DIR, "admin", "a0.tif").toFile());
        assertThat(indexable.get(1).getAbsolutePath()).isEqualTo(Paths.get(dir.toString(), "admin1.tif").toString());
        assertThat(indexable.get(1)).hasContentEqualTo(Paths.get(DATA_DIR, "admin", "a1.tif").toFile());
        assertThat(indexable.get(2).getAbsolutePath()).isEqualTo(Paths.get(dir.toString(), "admin2.tif").toString());
        assertThat(indexable.get(2)).hasContentEqualTo(Paths.get(DATA_DIR, "admin", "a2.tif").toFile());
    }
}
