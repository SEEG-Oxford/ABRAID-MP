package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvEffectCurveCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvSubmodelStatistic;
import uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver.GeoserverRestService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contains integration tests for the MainController class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelOutputHandler/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelOutputHandler/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelOutputHandler/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MainControllerIntegrationTest extends AbstractSpringIntegrationTests {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private static final String OUTPUT_HANDLER_PATH = "/handleoutputs";
    private static final String TEST_DATA_PATH = "ModelOutputHandler/test/uk/ac/ox/zoo/seeg/abraid/mp/modeloutputhandler/web/testdata";

    private static final String TEST_MODEL_RUN_NAME = "deng_2014-05-16-13-28-57_482ae3ca-ab30-414d-acce-388baae7d83c";
    private static final int TEST_MODEL_RUN_DISEASE_GROUP_ID = 87;

    private MockMvc mockMvc;

    @ReplaceWithMock
    @Autowired
    private File rasterFileDirectory;

    @ReplaceWithMock
    @Autowired
    private GeoserverRestService geoserverRestService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MainController controller;

    @Autowired
    private ModelRunDao modelRunDao;

    @Before
    public void setup() {
        when(rasterFileDirectory.getAbsolutePath()).thenReturn(testFolder.getRoot().getAbsolutePath());

        // Set up Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void handleModelOutputsRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(get(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(put(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(delete(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(patch(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void handleModelOutputsStoresValidCompletedOutputs() throws Exception {
        // Arrange
        DateTime expectedResponseDate = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(expectedResponseDate.getMillis());
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("valid_completed_outputs.zip");
        String expectedOutputText = "test output text";

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Assert
        ModelRun run = modelRunDao.getByName(TEST_MODEL_RUN_NAME);
        assertThat(run.getStatus()).isEqualTo(ModelRunStatus.COMPLETED);
        assertThat(run.getResponseDate()).isEqualTo(expectedResponseDate);
        assertThat(run.getOutputText()).isEqualTo(expectedOutputText);
        assertThat(run.getErrorText()).isNullOrEmpty();
        assertThatStatisticsInDatabaseMatchesFile(run, "statistics.csv");
        assertThatRelativeInfluencesInDatabaseMatchesFile(run, "relative_influence.csv");
        assertThatEffectCurvesInDatabaseMatchesFile(run, "effect_curves.csv");

        assertThatRasterWrittenToFile(run, "mean_prediction.tif", "mean");
        assertThatRasterPublishedToGeoserver(run, "mean");
        assertThatRasterWrittenToFile(run, "prediction_uncertainty.tif", "uncertainty");
        assertThatRasterPublishedToGeoserver(run, "uncertainty");
    }

    @Test
    public void handleModelOutputsStoresValidFailedOutputsWithResults() throws Exception {
        // Arrange
        DateTime expectedResponseDate = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(expectedResponseDate.getMillis());
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("valid_failed_outputs_with_results.zip");
        String expectedErrorText = "test error text";

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Assert
        ModelRun run = modelRunDao.getByName(TEST_MODEL_RUN_NAME);
        assertThat(run.getStatus()).isEqualTo(ModelRunStatus.FAILED);
        assertThat(run.getResponseDate()).isEqualTo(expectedResponseDate);
        assertThat(run.getOutputText()).isNullOrEmpty();
        assertThat(run.getErrorText()).isEqualTo(expectedErrorText);
        assertThatStatisticsInDatabaseMatchesFile(run, "statistics.csv");
        assertThatRelativeInfluencesInDatabaseMatchesFile(run, "relative_influence.csv");
        assertThatEffectCurvesInDatabaseMatchesFile(run, "effect_curves.csv");

        assertThatRasterWrittenToFile(run, "mean_prediction.tif", "mean");
        assertThatRasterWrittenToFile(run, "prediction_uncertainty.tif", "uncertainty");
        assertThatNoRastersPublishedToGeoserver();
    }

    @Test
    public void handleModelOutputsStoresValidFailedOutputsWithoutResults() throws Exception {
        // Arrange
        DateTime expectedResponseDate = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(expectedResponseDate.getMillis());
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("valid_failed_outputs_without_results.zip");
        String expectedErrorText = "test error text";

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Assert
        ModelRun run = modelRunDao.getByName(TEST_MODEL_RUN_NAME);
        assertThat(run.getStatus()).isEqualTo(ModelRunStatus.FAILED);
        assertThat(run.getResponseDate()).isEqualTo(expectedResponseDate);
        assertThat(run.getOutputText()).isNullOrEmpty();
        assertThat(run.getErrorText()).isEqualTo(expectedErrorText);
        assertThatNoRastersPublishedToGeoserver();
    }

    @Test
    public void handleModelOutputsRejectsMalformedZipFile() throws Exception {
        // Arrange
        byte[] malformedZipFile = "This is not a zip file".getBytes();

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(malformedZipFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"Probably not a zip file or a corrupted zip file\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingMetadata() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("missing_metadata.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File metadata.json missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsIncorrectModelRunName() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("incorrect_model_run_name.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"Model run with name deng_2014-05-13-11-26-37_0469aac2-d9b2-4104-907e-2886eff11682 does not exist\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingMeanPredictionRasterIfStatusIsCompleted() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("missing_mean_prediction.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File mean_prediction.tif missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingPredictionUncertaintyRasterIfStatusIsCompleted() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("missing_prediction_uncertainty.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File prediction_uncertainty.tif missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingStatisticsIfStatusIsCompleted() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("missing_statistics.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File statistics.csv missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingEffectCurvesIfStatusIsCompleted() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("missing_effect_curves.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File effect_curves.csv missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingRelativeInfluencesIfStatusIsCompleted() throws Exception {
        // Arrange
        insertModelRun(TEST_MODEL_RUN_NAME);
        byte[] body = loadTestFile("missing_influence.zip");

        // Act and assert
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File relative_influence.csv missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    private void insertModelRun(String name) {
        ModelRun modelRun = new ModelRun(name, TEST_MODEL_RUN_DISEASE_GROUP_ID, "host", DateTime.now());
        modelRunDao.save(modelRun);
    }

    private byte[] loadTestFile(String fileName) throws IOException {
        return FileUtils.readFileToByteArray(new File(TEST_DATA_PATH, fileName));
    }

    private void assertThatRasterWrittenToFile(ModelRun run, String expectedFileName, String type) throws IOException {
        byte[] expectedRaster = loadTestFile(expectedFileName);
        byte[] actualRaster = FileUtils.readFileToByteArray(Paths.get(testFolder.getRoot().getAbsolutePath(), run.getName() + "_" + type + ".tif").toFile());

        assertThat(new String(actualRaster)).isEqualTo(new String(expectedRaster));
    }

    private void assertThatRasterPublishedToGeoserver(ModelRun run, String type) throws IOException, TemplateException {
        verify(geoserverRestService, times(1)).publishGeoTIFF(Paths.get(testFolder.getRoot().getAbsolutePath(), run.getName() + "_" + type + ".tif").toFile());
    }
    private void assertThatNoRastersPublishedToGeoserver() throws IOException, TemplateException {
        verify(geoserverRestService, times(0)).publishGeoTIFF(any(File.class));
    }

    private void assertThatStatisticsInDatabaseMatchesFile(final ModelRun run, String path) throws IOException {
        List<SubmodelStatistic> database = run.getSubmodelStatistics();
        List<CsvSubmodelStatistic> file = CsvSubmodelStatistic.readFromCSV(FileUtils.readFileToString(new File(TEST_DATA_PATH, path)));

        Collections.sort(database, new Comparator<SubmodelStatistic>() {
            @Override
            public int compare(SubmodelStatistic o1, SubmodelStatistic o2) {
                return o1.getDeviance().compareTo(o2.getDeviance());
            }
        });

        Collections.sort(file, new Comparator<CsvSubmodelStatistic>() {
            @Override
            public int compare(CsvSubmodelStatistic o1, CsvSubmodelStatistic o2) {
                return o1.getDeviance().compareTo(o2.getDeviance());
            }
        });

        assertThat(extractProperty("deviance").from(database)).isEqualTo(extractProperty("deviance").from(file));
        assertThat(extractProperty("rootMeanSquareError").from(database)).isEqualTo(extractProperty("rootMeanSquareError").from(file));
        assertThat(extractProperty("kappa").from(database)).isEqualTo(extractProperty("kappa").from(file));
        assertThat(extractProperty("areaUnderCurve").from(database)).isEqualTo(extractProperty("areaUnderCurve").from(file));
        assertThat(extractProperty("sensitivity").from(database)).isEqualTo(extractProperty("sensitivity").from(file));
        assertThat(extractProperty("specificity").from(database)).isEqualTo(extractProperty("specificity").from(file));
        assertThat(extractProperty("proportionCorrectlyClassified").from(database)).isEqualTo(extractProperty("proportionCorrectlyClassified").from(file));
        assertThat(extractProperty("kappaStandardDeviation").from(database)).isEqualTo(extractProperty("kappaStandardDeviation").from(file));
        assertThat(extractProperty("areaUnderCurveStandardDeviation").from(database)).isEqualTo(extractProperty("areaUnderCurveStandardDeviation").from(file));
        assertThat(extractProperty("sensitivityStandardDeviation").from(database)).isEqualTo(extractProperty("sensitivityStandardDeviation").from(file));
        assertThat(extractProperty("specificityStandardDeviation").from(database)).isEqualTo(extractProperty("specificityStandardDeviation").from(file));
        assertThat(extractProperty("proportionCorrectlyClassifiedStandardDeviation").from(database)).isEqualTo(extractProperty("proportionCorrectlyClassifiedStandardDeviation").from(file));
        assertThat(extractProperty("threshold").from(database)).isEqualTo(extractProperty("threshold").from(file));
    }

    private void assertThatRelativeInfluencesInDatabaseMatchesFile(final ModelRun run, String path) throws IOException {
        List<CovariateInfluence> database = run.getCovariateInfluences();
        List<CsvCovariateInfluence> file = CsvCovariateInfluence.readFromCSV(FileUtils.readFileToString(new File(TEST_DATA_PATH, path)));

        Collections.sort(database, new Comparator<CovariateInfluence>() {
            @Override
            public int compare(CovariateInfluence o1, CovariateInfluence o2) {
                return o1.getMeanInfluence().compareTo(o2.getMeanInfluence());
            }
        });

        Collections.sort(file, new Comparator<CsvCovariateInfluence>() {
            @Override
            public int compare(CsvCovariateInfluence o1, CsvCovariateInfluence o2) {
                return o1.getMeanInfluence().compareTo(o2.getMeanInfluence());
            }
        });

        assertThat(extractProperty("covariateName").from(database)).isEqualTo(extractProperty("covariateName").from(file));
        assertThat(extractProperty("covariateDisplayName").from(database)).isEqualTo(extractProperty("covariateDisplayName").from(file));
        assertThat(extractProperty("meanInfluence").from(database)).isEqualTo(extractProperty("meanInfluence").from(file));
        assertThat(extractProperty("upperQuantile").from(database)).isEqualTo(extractProperty("upperQuantile").from(file));
        assertThat(extractProperty("lowerQuantile").from(database)).isEqualTo(extractProperty("lowerQuantile").from(file));
    }

    private void assertThatEffectCurvesInDatabaseMatchesFile(final ModelRun run, String path) throws IOException {
        List<EffectCurveCovariateInfluence> database = run.getEffectCurveCovariateInfluences();
        List<CsvEffectCurveCovariateInfluence> file = CsvEffectCurveCovariateInfluence.readFromCSV(FileUtils.readFileToString(new File(TEST_DATA_PATH, path)));

        Collections.sort(database, new Comparator<EffectCurveCovariateInfluence>() {
            @Override
            public int compare(EffectCurveCovariateInfluence o1, EffectCurveCovariateInfluence o2) {
                return new CompareToBuilder()
                        .append(o1.getCovariateName(), o2.getCovariateName())
                        .append(o1.getCovariateValue(), o2.getCovariateValue())
                        .toComparison();
            }
        });

        Collections.sort(file, new Comparator<CsvEffectCurveCovariateInfluence>() {
            @Override
            public int compare(CsvEffectCurveCovariateInfluence o1, CsvEffectCurveCovariateInfluence o2) {
                return new CompareToBuilder()
                        .append(o1.getCovariateName(), o2.getCovariateName())
                        .append(o1.getCovariateValue(), o2.getCovariateValue())
                        .toComparison();
            }
        });

        assertThat(extractProperty("covariateName").from(database)).isEqualTo(extractProperty("covariateName").from(file));
        assertThat(extractProperty("covariateDisplayName").from(database)).isEqualTo(extractProperty("covariateDisplayName").from(file));
        assertThat(extractProperty("covariateValue").from(database)).isEqualTo(extractProperty("covariateValue").from(file));
        assertThat(extractProperty("meanInfluence").from(database)).isEqualTo(extractProperty("meanInfluence").from(file));
        assertThat(extractProperty("upperQuantile").from(database)).isEqualTo(extractProperty("upperQuantile").from(file));
        assertThat(extractProperty("lowerQuantile").from(database)).isEqualTo(extractProperty("lowerQuantile").from(file));
    }
}
