package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.WrapWithSpy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.*;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.BaseWebIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests that wire together a near complete setup of ModelWrapper.
 * Tests that model runs are correctly performed in response to a HTTP POST to '/model/run' with realistic data.
 * Configured to try both a real run and a dry run.
 *
 * Copyright (c) 2014 University of Oxford
 *
 * NOTICE:
 *  These tests are intentionally excluded from automatically run test sets. They are slow (multiple minutes) as they
 *  attempt to perform a real model run. They also require external data sets (not in repository); Oxford Zoology
 *  members can find these on the shared data drive.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelWrapper/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelWrapper/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelWrapper/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ModuleTests extends BaseWebIntegrationTests {
    private static final int SUCCESSFUL = 0;
    private static final String TEST_DATA_DIR = "C:\\Temp\\ModuleTests_Data\\ModelWrapper"; // The directory in which the external data files can be found
    private static final String TEST_REPO_PATH = "C:\\Temp\\sdm"; // A repository clone on the file system is preferable (don't overburden github)
    private static final String TEST_REPO_TAG = "test"; // The version tag of the model repository code to use
    private static final String TEST_JSON_FILE = "model_run_dengue.json"; // The json to post to '/model/run' (in TEST_DATA_DIR). Must match TEST_DISEASE_ID.
    private static final int TEST_DISEASE_ID = 87; // The disease id from the json

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private MockMvc mockMvc;

    @Autowired
    private SourceCodeManager sourceCodeManager;

    @WrapWithSpy
    @Autowired
    private WorkspaceProvisioner workspaceProvisioner;

    @WrapWithSpy
    @Autowired
    private ModelRunnerAsyncWrapper modelRunnerAsyncWrapper;

    @WrapWithSpy
    @Autowired
    private ConfigurationService configurationService;

    @ReplaceWithMock
    @Autowired
    private WebServiceClient webServiceClient;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Future<ModelProcessHandler> lastTriggeredModelRun;

    @Before
    public void setup() throws IOException, ProcessException {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // Override behaviour of configuration service for items where we don't want the defaults
        setupConfigurationServiceSpy();

        // Track the async stuff for the most recently triggered model run, so that test can wait for completion
        setupFutureTrackingForTriggeredModelRuns();

        // Setup copying of files into workspace
        setupCopyingFileToWorkspace();

        // Make sure we have the model code to run test
        sourceCodeManager.updateRepository();
    }

    @Test
    public void canDoDryRunOfModelFromJsonPOST() throws Exception {
        runTest(true);
    }

    @Test
    public void canDoRealRunOfModelFromJsonPOST() throws Exception {
        runTest(false);
    }

    private void runTest(boolean dryRun) throws Exception {
        // Arrange
        doReturn(dryRun).when(configurationService).getDryRunFlag();

        // Act
        // Send a post to the run url with json data
        this.mockMvc
                .perform(post("/model/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getInputJson()))
                .andExpect(status().isOk());

        // Get the model setup future
        Future<ModelProcessHandler> setupProcess = getFutureForLastTriggeredModelRun();

        // Wait for model setup to finish
        ModelProcessHandler rProcess = setupProcess.get();

        // Wait for the R code to finish
        int exitCodeOfRScript = rProcess.waitForCompletion();

        // Assert
        printDebuggingInfo(rProcess, exitCodeOfRScript);
        assertThat(exitCodeOfRScript).isEqualTo(SUCCESSFUL);
    }

    private void printDebuggingInfo(ModelProcessHandler rProcess, int exitCodeOfRScript) {
        System.out.println(exitCodeOfRScript);
        System.out.println(this.configurationService.getCacheDirectory());
        System.out.println(rProcess.getOutputStream().toString() + " " + rProcess.getErrorStream().toString());
    }

    private static String getInputJson() throws IOException {
        return FileUtils.readFileToString(Paths.get(TEST_DATA_DIR, TEST_JSON_FILE).toFile());
    }

    private Future<ModelProcessHandler> getFutureForLastTriggeredModelRun() {
        return this.lastTriggeredModelRun;
    }

    private void setupFutureTrackingForTriggeredModelRuns() {
        // When the ModelRunnerAsyncWrapper
        doAnswer(new Answer<Future<ModelProcessHandler>>() {
            @Override
            public Future<ModelProcessHandler> answer(InvocationOnMock invocationOnMock) throws Throwable {
                lastTriggeredModelRun = (Future<ModelProcessHandler>) invocationOnMock.callRealMethod();
                return lastTriggeredModelRun;
            }
        }).when(modelRunnerAsyncWrapper).startModel(
                any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class), any(ModelStatusReporter.class));
    }

    private void setupConfigurationServiceSpy() throws IOException {
        // Use temp directory - ie somewhere that will be deleted by JUnit, so we don't have to worry about large files.
        doReturn(testFolder.newFolder().toString()).when(configurationService).getCacheDirectory();

        // Flags
        doReturn(true).when(configurationService).getModelVerboseFlag();
        doReturn(1).when(configurationService).getMaxCPUs();

        // Rasters
        doReturn(Paths.get(TEST_DATA_DIR, "admin_tropical.asc").toString()).when(configurationService).getTropicalRasterFile();
        doReturn(Paths.get(TEST_DATA_DIR, "admin_global.asc").toString()).when(configurationService).getGlobalRasterFile();
        doReturn("external/admin/admin1qc.asc").when(configurationService).getAdmin1RasterFile();
        doReturn("external/admin/admin2qc.asc").when(configurationService).getAdmin2RasterFile();

        // Covariates
        doReturn("external/covariates").when(configurationService).getCovariateDirectory();
        doReturn(createTestCovariateConfig()).when(configurationService).getCovariateConfiguration();

        // Repo
        doReturn(TEST_REPO_PATH).when(configurationService).getModelRepositoryUrl();
        doReturn(TEST_REPO_TAG).when(configurationService).getModelRepositoryVersion();

        // Output
        doReturn("foo").when(configurationService).getModelOutputHandlerRootUrl();
    }

    private JsonCovariateConfiguration createTestCovariateConfig() {
        return new JsonCovariateConfiguration(
            Arrays.asList(
                new JsonDisease(TEST_DISEASE_ID, "foo")
            ),
            Arrays.asList(
                new JsonCovariateFile("rand1.asc", "", null, false, Arrays.asList(87)),
                new JsonCovariateFile("rand2.asc", "", null, false, Arrays.asList(87)),
                new JsonCovariateFile("rand3.asc", "", null, false, Arrays.asList(87))
            )
        );
    }

    private void setupCopyingFileToWorkspace() throws IOException {
        doAnswer(new Answer() {
            @Override
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                // Do the real workspace setup
                File result = (File) invocationOnMock.callRealMethod();

                // When the workspace provisioner runs, copy any extra files we need into a "external" subdirectory of the workspace
                copyFilesIntoWorkspace(result.getParentFile());

                return result;
            }
        }).when(workspaceProvisioner).provisionWorkspace(
                any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class));
    }

    private static void copyFilesIntoWorkspace(File workingDir) throws IOException {
        // The files directly accessed by the R code should be copied into the "external" directory.
        // This is to help with debugging of runABRAID.
        copyFileToExternal(workingDir, "admin1qc.asc", "admin", "admin1qc.asc");
        copyFileToExternal(workingDir, "admin2qc.asc", "admin", "admin2qc.asc");
        copyFileToExternal(workingDir, "rand1.asc", "covariates", "rand1.asc");
        copyFileToExternal(workingDir, "rand2.asc", "covariates", "rand2.asc");
        copyFileToExternal(workingDir, "rand3.asc", "covariates", "rand3.asc");
    }

    private static void copyFileToExternal(File workingDir, String source, String targetDir, String targetName) throws IOException {
        File targetFile = Paths.get(workingDir.toString(), "external", targetDir, targetName).toFile();
        File sourceFile = Paths.get(TEST_DATA_DIR, source).toFile();
        FileUtils.copyFile(sourceFile , targetFile);
    }
}
