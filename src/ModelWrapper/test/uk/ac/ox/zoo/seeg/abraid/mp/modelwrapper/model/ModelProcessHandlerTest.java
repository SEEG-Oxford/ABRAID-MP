package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatcher;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.ModelOutputConstants;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelProcessHandler.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelProcessHandlerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private RunConfiguration runConfiguration = mock(RunConfiguration.class);
    private ModelOutputHandlerWebService modelOutputHandlerWebService = mock(ModelOutputHandlerWebService.class);

    private static final String TEST_DATA_FOLDER = "ModelWrapper/test/uk/ac/ox/zoo/seeg/abraid/mp/modelwrapper/model/testdata";

    @Test
    public void onProcessCompleteSendsCorrectOutputs() throws Exception {
        // Arrange - construct main objects
        ConfigurationService configurationService = mock(ConfigurationService.class);
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        modelOutputHandlerWebService = new ModelOutputHandlerWebService(webServiceClient, configurationService);
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);
        Logger logger = GeneralTestUtils.createMockLogger(target);
        File workingDirectory = testFolder.getRoot();

        // Arrange - string constants
        String rootUrl = "http://localhost:8080/ModelOutputHandler/";
        String url = rootUrl + "modeloutputhandler/handleoutputs";
        String modelRunName = "deng_2014-05-13-14-49-14_652cc144-3836-4819-b489-e271212a96ed";
        String outputText = "test output text";

        target.getOutputStream().write(outputText.getBytes());

        // Arrange - stub methods
        when(configurationService.getModelOutputHandlerRootUrl()).thenReturn(rootUrl);
        when(runConfiguration.getWorkingDirectoryPath()).thenReturn(Paths.get(workingDirectory.toURI()));
        when(runConfiguration.getRunName()).thenReturn(modelRunName);
        when(webServiceClient.makePostRequest(anyString(), any(byte[].class))).thenReturn("");

        // Arrange - create matcher for zip file
        File meanPredictionRasterTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME);
        File predictionUncertaintyRasterTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME);
        File metadataJsonTestFile = new File(TEST_DATA_FOLDER + "/completed", ModelOutputConstants.METADATA_JSON_FILENAME);
        File validationStatsTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.VALIDATION_STATISTICS_FILENAME);
        File relativeInfluenceTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.RELATIVE_INFLUENCE_FILENAME);

        ZipFileMatcher zipFileMatcher = new ZipFileMatcher(Arrays.asList(meanPredictionRasterTestFile,
                predictionUncertaintyRasterTestFile, metadataJsonTestFile, validationStatsTestFile, relativeInfluenceTestFile));

        // Arrange - copy outputs to test folder
        File resultsDir = new File(workingDirectory, "results");
        resultsDir.mkdir();
        FileUtils.copyFileToDirectory(meanPredictionRasterTestFile, resultsDir);
        FileUtils.copyFileToDirectory(predictionUncertaintyRasterTestFile, resultsDir);
        FileUtils.copyFileToDirectory(validationStatsTestFile, resultsDir);
        FileUtils.copyFileToDirectory(relativeInfluenceTestFile, resultsDir);

        // Act
        target.onProcessComplete();

        // Assert
        verify(webServiceClient, times(1)).makePostRequest(eq(url), argThat(zipFileMatcher));
        verify(logger, times(1)).info(eq("Successfully sent model outputs to model output handler."));
    }

    @Test
    public void onProcessCompleteLogsErrorIfWorkingDirectoryDoesNotExist() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);
        Logger logger = GeneralTestUtils.createMockLogger(target);

        when(runConfiguration.getWorkingDirectoryPath()).thenReturn(Paths.get("this path does not exist"));

        // Act
        target.onProcessComplete();

        // Assert
        verify(logger, times(1)).fatal(
                startsWith("Error sending model outputs for handling: working directory"),
                any(IllegalArgumentException.class));
    }

    @Test
    public void onProcessCompleteLogsErrorIfMeanPredictionRasterDoesNotExist() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);
        Logger logger = GeneralTestUtils.createMockLogger(target);
        File workingDirectory = testFolder.getRoot();

        when(runConfiguration.getWorkingDirectoryPath()).thenReturn(Paths.get(workingDirectory.toURI()));

        File predictionUncertaintyRasterTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME);
        FileUtils.copyFileToDirectory(predictionUncertaintyRasterTestFile, workingDirectory);

        // Act
        target.onProcessComplete();

        // Assert
        verify(logger, times(1)).fatal(
                eq("Error sending model outputs for handling: input file does not exist"),
                any(ZipException.class));
    }

    @Test
    public void onProcessCompleteLogsErrorIfPredictionUncertaintyRasterDoesNotExist() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);
        Logger logger = GeneralTestUtils.createMockLogger(target);
        File workingDirectory = testFolder.getRoot();

        when(runConfiguration.getWorkingDirectoryPath()).thenReturn(Paths.get(workingDirectory.toURI()));

        File meanPredictionRasterTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME);
        FileUtils.copyFileToDirectory(meanPredictionRasterTestFile, workingDirectory);

        // Act
        target.onProcessComplete();

        // Assert
        verify(logger, times(1)).fatal(
                eq("Error sending model outputs for handling: input file does not exist"),
                any(ZipException.class));
    }

    @Test
    public void onProcessCompleteLogsErrorIfWebServiceReturnsError() throws Exception {
        // Arrange - general
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);
        Logger logger = GeneralTestUtils.createMockLogger(target);
        File workingDirectory = testFolder.getRoot();
        String webServiceResponseMessage = "Error message";

        when(runConfiguration.getWorkingDirectoryPath()).thenReturn(Paths.get(workingDirectory.toURI()));
        when(modelOutputHandlerWebService.handleOutputs(any(File.class))).thenReturn(webServiceResponseMessage);

        // Arrange - copy test files to working directory
        File meanPredictionRasterTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.MEAN_PREDICTION_RASTER_FILENAME);
        File predictionUncertaintyRasterTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.PREDICTION_UNCERTAINTY_RASTER_FILENAME);
        File validationStatsTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.VALIDATION_STATISTICS_FILENAME);
        File relativeInfluenceTestFile = new File(TEST_DATA_FOLDER, ModelOutputConstants.RELATIVE_INFLUENCE_FILENAME);
        File resultsDir = new File(workingDirectory, "results");

        resultsDir.mkdir();
        FileUtils.copyFileToDirectory(meanPredictionRasterTestFile, resultsDir);
        FileUtils.copyFileToDirectory(predictionUncertaintyRasterTestFile, resultsDir);
        FileUtils.copyFileToDirectory(validationStatsTestFile, resultsDir);
        FileUtils.copyFileToDirectory(relativeInfluenceTestFile, resultsDir);

        // Act
        target.onProcessComplete();

        // Assert
        verify(logger, times(1)).error(eq("Error received from model output handler: " + webServiceResponseMessage));
    }

    @Test
    public void onProcessFailedSendsCorrectOutputs() throws Exception {
        // Arrange - construct main objects
        ConfigurationService configurationService = mock(ConfigurationService.class);
        WebServiceClient webServiceClient = mock(WebServiceClient.class);
        modelOutputHandlerWebService = new ModelOutputHandlerWebService(webServiceClient, configurationService);
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);
        Logger logger = GeneralTestUtils.createMockLogger(target);
        File workingDirectory = testFolder.getRoot();

        // Arrange - string constants
        String rootUrl = "http://localhost:8080/ModelOutputHandler/";
        String url = rootUrl + "modeloutputhandler/handleoutputs";
        String modelRunName = "deng_2014-05-13-14-49-14_652cc144-3836-4819-b489-e271212a96ed";
        String outputText = "test output text";
        String errorMessage = "test error message";
        String errorStreamText = "test error stream text";

        target.getOutputStream().write(outputText.getBytes());
        target.getErrorStream().write(errorStreamText.getBytes());

        // Arrange - stub methods
        when(configurationService.getModelOutputHandlerRootUrl()).thenReturn(rootUrl);
        when(runConfiguration.getWorkingDirectoryPath()).thenReturn(Paths.get(workingDirectory.toURI()));
        when(runConfiguration.getRunName()).thenReturn(modelRunName);
        when(webServiceClient.makePostRequest(anyString(), any(byte[].class))).thenReturn("");

        // Arrange - create matcher for zip file
        File metadataJsonTestFile = new File(TEST_DATA_FOLDER + "/failed", ModelOutputConstants.METADATA_JSON_FILENAME);

        ZipFileMatcher zipFileMatcher = new ZipFileMatcher(Arrays.asList(metadataJsonTestFile));

        // Act
        target.onProcessFailed(new ProcessException(errorMessage, new IllegalAccessException()));

        // Assert
        verify(webServiceClient, times(1)).makePostRequest(eq(url), argThat(zipFileMatcher));
        verify(logger, times(1)).info(eq("Successfully sent model outputs to model output handler."));
    }

    @Test
    public void getOutputStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);

        // Act
        OutputStream result = target.getOutputStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void getInputStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);

        // Act
        InputStream result = target.getInputStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void getErrorStreamReturnsValidStream() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);

        // Act
        OutputStream result = target.getErrorStream();

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    public void waitForCompletionShouldThrowIfWaiterHasNotBeenSet() throws Exception {
        // Arrange
        ModelProcessHandler target = new ModelProcessHandler(runConfiguration, modelOutputHandlerWebService);

        // Act
        catchException(target).waitForCompletion();
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IllegalStateException.class);
    }


    /**
     * Matcher for a zip file. We cannot compare zip files directly (via a byte-by-byte comparison) because there may
     * be trivial differences such as file order, last modified date of files, compression parameters etc.
     * Instead we compare the unzipped contents with existing files.
     */
    class ZipFileMatcher extends ArgumentMatcher<byte[]> {
        private final List<File> expectedZipFileContents;

        public ZipFileMatcher(List<File> expectedZipFileContents) {
            this.expectedZipFileContents = expectedZipFileContents;
        }

        @Override
        public boolean matches(Object actual) {
            try {
                // Write out the zip
                File file = testFolder.newFile();
                FileUtils.writeByteArrayToFile(file, (byte[]) actual);

                // Assert that the zip has the expected number of files
                ZipFile zipFile = new ZipFile(file.getAbsolutePath());
                int expectedZipFileCount = expectedZipFileContents.size();
                int actualZipFileCount = zipFile.getFileHeaders().size();
                if (actualZipFileCount != expectedZipFileCount) {
                    // Number of files in zip file is not as expected
                    System.out.println(String.format("Expected %d files in zip, actual %d files", expectedZipFileCount,
                            actualZipFileCount));
                    return false;
                }

                // Extract all of the files into a temporary folder
                File unzipFolder = testFolder.newFolder();
                zipFile.extractAll(unzipFolder.getAbsolutePath());

                // Compare each of the files with those expected
                for (File expectedFile : expectedZipFileContents) {
                    String expectedFileName = expectedFile.getName();
                    File actualFile = new File(unzipFolder, expectedFileName);
                    if (!actualFile.exists()) {
                        // Expected file does not exist in zip file
                        System.out.println(String.format("Expected file %s does not exist in zip", expectedFileName));
                    }

                    String expectedFileContents = FileUtils.readFileToString(expectedFile);
                    String actualFileContents = FileUtils.readFileToString(actualFile);
                    if (!expectedFileContents.equals(actualFileContents)) {
                        // Expected and actual files are not equal
                        System.out.println(String.format("Unzipped files named %s are not equal.\nExpected: %s\nActual:   %s",
                                expectedFileName, expectedFileContents, actualFileContents));
                        return false;
                    }
                }

                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
