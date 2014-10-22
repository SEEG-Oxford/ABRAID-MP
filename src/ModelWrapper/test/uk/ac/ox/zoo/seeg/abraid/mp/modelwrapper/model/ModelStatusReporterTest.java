package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.filter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.ZipFileAssert.assertThatZip;

/**
 * Tests for ModelStatusReporterImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelStatusReporterTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private static final String TEST_DATA_FOLDER = "ModelWrapper/test/uk/ac/ox/zoo/seeg/abraid/mp/modelwrapper/model/testdata";

    private static final File MEAN_PREDICTION_RASTER_TEST_FILE = new File(TEST_DATA_FOLDER, ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME);
    private static final File PREDICTION_UNCERTAINTY_RASTER_TEST_FILE = new File(TEST_DATA_FOLDER, ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME);
    private static final File VALIDATION_STATS_TEST_FILE = new File(TEST_DATA_FOLDER, ModelOutputConstants.VALIDATION_STATISTICS_FILENAME);
    private static final File RELATIVE_INFLUENCE_TEST_FILE = new File(TEST_DATA_FOLDER, ModelOutputConstants.RELATIVE_INFLUENCE_FILENAME);
    private static final File EFFECT_CURVES_TEST_FILE = new File(TEST_DATA_FOLDER, ModelOutputConstants.EFFECT_CURVES_FILENAME);
    private static final List<File> RESULTS_FILES = Arrays.asList(MEAN_PREDICTION_RASTER_TEST_FILE, PREDICTION_UNCERTAINTY_RASTER_TEST_FILE, VALIDATION_STATS_TEST_FILE, RELATIVE_INFLUENCE_TEST_FILE, EFFECT_CURVES_TEST_FILE);

    private static final File COMPLETED_METADATA_JSON_TEST_FILE = new File(TEST_DATA_FOLDER + "/completed", ModelOutputConstants.METADATA_JSON_FILENAME);
    private static final File FAILED_METADATA_JSON_TEST_FILE = new File(TEST_DATA_FOLDER + "/failed", ModelOutputConstants.METADATA_JSON_FILENAME);

    private static final String MODEL_RUN_NAME = "deng_2014-05-13-14-49-14_652cc144-3836-4819-b489-e271212a96ed";

    @Test
    public void reportSendsCorrectOutputsForCompletedStatus() throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        String outputText = "test output text";
        String errorText = "test error text";

        addResultsToWorkspace(RESULTS_FILES, workingDirectory);

        List<File> expectedFiles = ListUtils.union(RESULTS_FILES, Arrays.asList(COMPLETED_METADATA_JSON_TEST_FILE));

        // This file will have been deleted by the end of "act" so if we want to check it we need to make a copy at the moment when handleOutputs is called
        File handledZip = setupModelOutputServiceHandleOutputsFileRetention(mockOutputServiceClient);

        // Act
        target.report(ModelRunStatus.COMPLETED, outputText, errorText);

        // Assert
        assertThatZip(handledZip).hasContentFiles(expectedFiles, testFolder);
        verify(logger, times(1)).info(eq("Successfully sent model outputs to model output handler."));
    }

    @Test
    public void reportDeletesWorkspaceIfResultsSent() throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());

        String outputText = "test output text";
        String errorText = "test error text";

        addResultsToWorkspace(RESULTS_FILES, workingDirectory);

        // Act
        target.report(ModelRunStatus.COMPLETED, outputText, errorText);

        // Assert
        assertThat(workingDirectory).doesNotExist();
    }

    @Test
    public void reportSendsCorrectOutputsForFailedStatus() throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        String outputText = "test output text";
        String errorText = "test error text";

        List<File> expectedFiles = Arrays.asList(FAILED_METADATA_JSON_TEST_FILE);

        // This file will have been deleted by the end of "act" so if we want to check it we need to make a copy at the moment when handleOutputs is called
        File handledZip = setupModelOutputServiceHandleOutputsFileRetention(mockOutputServiceClient);

        // Act
        target.report(ModelRunStatus.FAILED, outputText, errorText);

        // Assert
        assertThatZip(handledZip).hasContentFiles(expectedFiles, testFolder);
        verify(logger, times(1)).info(eq("Successfully sent model outputs to model output handler."));
    }

    @Test
    public void reportLogsErrorIfWebServiceReturnsErrorText() throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        addResultsToWorkspace(RESULTS_FILES, workingDirectory);

        String webServiceResponseMessage = "WebService error text";
        when(mockOutputServiceClient.handleOutputs(any(File.class))).thenReturn(webServiceResponseMessage);

        // Act
        target.report(ModelRunStatus.COMPLETED, "", "");

        // Assert
        verify(logger, times(1)).error(eq("Error received from model output handler: " + webServiceResponseMessage));
    }

    @Test
    public void reportLogsErrorIfWebServiceThrowsException() throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        addResultsToWorkspace(RESULTS_FILES, workingDirectory);

        String webServiceResponseMessage = "WebService error text";
        IOException ioException = new IOException(webServiceResponseMessage);
        when(mockOutputServiceClient.handleOutputs(any(File.class))).thenThrow(ioException);

        // Act
        target.report(ModelRunStatus.COMPLETED, "", "");

        // Assert
        verify(logger, times(1)).fatal("Error sending model outputs for handling: " + webServiceResponseMessage, ioException);
    }

    @Test
    public void reportDoesNotDeleteWorkspaceIfExceptionThrown() throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        addResultsToWorkspace(RESULTS_FILES, workingDirectory);

        String webServiceResponseMessage = "WebService error text";
        IOException ioException = new IOException(webServiceResponseMessage);
        when(mockOutputServiceClient.handleOutputs(any(File.class))).thenThrow(ioException);

        // Act
        target.report(ModelRunStatus.COMPLETED, "", "");

        // Assert
        assertThat(workingDirectory).exists();
    }

    @Test
    public void reportLogsErrorIfWorkingDirectoryDoesNotExist() throws Exception {
        // Arrange
        File workingDirectory = new File("this path does not exist");

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        // Act
        target.report(ModelRunStatus.COMPLETED, "", "");

        // Assert
        verify(logger, times(1)).fatal(
                startsWith("Error sending model outputs for handling: working directory"),
                any(IllegalArgumentException.class));
    }

    @Test
    public void reportLogsErrorIfMeanPredictionRasterDoesNotExist() throws Exception {
        reportLogsErrorIfResultFileIsMissing(MEAN_PREDICTION_RASTER_TEST_FILE);
    }

    @Test
    public void reportLogsErrorIfPredictionUncertaintyRasterDoesNotExist() throws Exception {
        reportLogsErrorIfResultFileIsMissing(PREDICTION_UNCERTAINTY_RASTER_TEST_FILE);
    }

    @Test
    public void reportLogsErrorIfValidationStatsFileDoesNotExist() throws Exception {
        reportLogsErrorIfResultFileIsMissing(VALIDATION_STATS_TEST_FILE);
    }

    @Test
    public void reportLogsErrorIfRelativeInfluenceFileDoesNotExist() throws Exception {
        reportLogsErrorIfResultFileIsMissing(RELATIVE_INFLUENCE_TEST_FILE);
    }

    private void reportLogsErrorIfResultFileIsMissing(File missingFile) throws Exception {
        // Arrange
        File workingDirectory = testFolder.newFolder();

        ModelOutputHandlerWebService mockOutputServiceClient = mock(ModelOutputHandlerWebService.class);
        ModelStatusReporter target = new ModelStatusReporterImpl(MODEL_RUN_NAME, workingDirectory.toPath(), mockOutputServiceClient, new AbraidJsonObjectMapper());
        Logger logger = GeneralTestUtils.createMockLogger(target);

        addResultsToWorkspace(filter(not(missingFile), RESULTS_FILES), workingDirectory);

        // Act
        target.report(ModelRunStatus.COMPLETED, "", "");

        // Assert
        verify(logger, times(1)).fatal(
                startsWith("Error sending model outputs for handling: File does not exist"),
                any(ZipException.class));
    }

    private void addResultsToWorkspace(List<File> files, File workspace) throws IOException {
        // Arrange - copy outputs to test folder
        File resultsDir = new File(workspace, "results");
        resultsDir.mkdir();
        for (File file : files) {
            FileUtils.copyFileToDirectory(file, resultsDir);
        }
    }

    private File setupModelOutputServiceHandleOutputsFileRetention(ModelOutputHandlerWebService mockOutputServiceClient) throws IOException {
        final File destination = testFolder.newFile();
        when(mockOutputServiceClient.handleOutputs(any(File.class))).thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                FileUtils.copyFile((File) invocationOnMock.getArguments()[0], destination);
                return null;
            }
        });
        return destination;
    }
}
