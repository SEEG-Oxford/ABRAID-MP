package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the ConfigurationService class.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier
    public static final String TEST_COVARIATE_JSON = "{\n" +
            "  \"diseases\" : [ {\n" +
            "    \"id\" : 22,\n" +
            "    \"name\" : \"Ascariasis\"\n" +
            "  }, {\n" +
            "    \"id\" : 64,\n" +
            "    \"name\" : \"Cholera\"\n" +
            "  } ],\n" +
            "  \"files\" : [ {\n" +
            "    \"path\" : \"f1\",\n" +
            "    \"name\" : \"a\",\n" +
            "    \"info\" : null,\n" +
            "    \"hide\" : false,\n" +
            "    \"enabled\" : [ 22 ]\n" +
            "  }, {\n" +
            "    \"path\" : \"f2\",\n" +
            "    \"name\" : \"\",\n" +
            "    \"info\" : null,\n" +
            "    \"hide\" : true,\n" +
            "    \"enabled\" : [ ]\n" +
            "  } ]\n" +
            "}";

    private void writeStandardSimpleProperties(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultModelVersion)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(testFile, "UTF-8");
        writer.println("auth.username = " + defaultUsername);
        writer.println("auth.password_hash = " + defaultPasswordHash);
        writer.println("model.repo.url = " + defaultRepoUrl);
        writer.println("model.repo.version = " + defaultModelVersion);
        writer.close();
    }

    private void writeStandardSimplePropertiesWithExtra(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultModelVersion, String extraKey, String extraValue)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash, defaultRepoUrl, defaultModelVersion);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        writer.println(extraKey + " = " + StringEscapeUtils.escapeJava(extraValue));
        writer.close();
    }

    private void writeStandardSimplePropertiesWithExtra(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultModelVersion, Map<String, String> extraPairs)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash, defaultRepoUrl, defaultModelVersion);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        for (String key : extraPairs.keySet()) {
            writer.println(key + " = " + StringEscapeUtils.escapeJava(extraPairs.get(key)));

        }
        writer.close();
    }

    @Test
    public void setAuthenticationDetailsUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        String expectedUserName = "foo";
        String expectedPasswordHash = "bar";

        // Act
        target.setAuthenticationDetails(expectedUserName, expectedPasswordHash);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("auth.username = " + expectedUserName + System.lineSeparator() +
                "auth.password_hash = " + expectedPasswordHash);
    }

    @Test
    public void setModelRepositoryUrlUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        String expectedUrl = "foo";

        // Act
        target.setModelRepositoryUrl(expectedUrl);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("model.repo.url = " + expectedUrl);
    }

    @Test
    public void setModelRepositoryVersionUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        String expectedVersion = "foo";

        // Act
        target.setModelRepositoryVersion(expectedVersion);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("model.repo.version = " + expectedVersion);
    }

    @Test
    public void getAuthenticationUsernameReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedUserName = "foo";
        writeStandardSimpleProperties(testFile, expectedUserName, "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        // Act
        String result = target.getAuthenticationUsername();

        // Assert
        assertThat(result).isEqualTo(expectedUserName);
    }

    @Test
    public void getAuthenticationPasswordHashReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedPasswordHash = "foo";
        writeStandardSimpleProperties(testFile, "initialValue1", expectedPasswordHash, "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        // Act
        String result = target.getAuthenticationPasswordHash();

        // Assert
        assertThat(result).isEqualTo(expectedPasswordHash);
    }

    @Test
    public void getModelRepositoryUrlReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedUrl = "foo";
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", expectedUrl, "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        // Act
        String result = target.getModelRepositoryUrl();

        // Assert
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    public void getModelRepositoryVersionReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedVersion = "foo";
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", expectedVersion);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        // Act
        String result = target.getModelRepositoryVersion();

        // Assert
        assertThat(result).isEqualTo(expectedVersion);
    }

    @Test
    public void getCacheDirectoryReturnsCorrectDefaultOnWindows() throws Exception {
        // Arrange
        OSChecker osChecker = mock(OSChecker.class);
        when(osChecker.isWindows()).thenReturn(true);
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, osChecker);

        // Act
        String result = target.getCacheDirectory();

        // Assert
        // Note when this test runs on travis/linux the LOCALAPPDATA environment variable will be empty, but this shouldn't effect the test.
        assertThat(result).isEqualTo(System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper");
    }

    @Test
    public void getCacheDirectoryReturnsCorrectDefaultOnLinux() throws Exception {
        // Arrange
        OSChecker osChecker = mock(OSChecker.class);
        when(osChecker.isWindows()).thenReturn(false);
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, osChecker);

        // Act
        String result = target.getCacheDirectory();

        // Assert
        assertThat(result).isEqualTo("/var/lib/abraid/modelwrapper");
    }

    @Test
    public void getCacheDirectoryReturnsCorrectValue() throws Exception {
        // Arrange
        OSChecker osChecker = mock(OSChecker.class);
        File testFile = testFolder.newFile();
        String expectedDir = "foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", "cache.data.dir", expectedDir);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, osChecker);

        // Act
        String result = target.getCacheDirectory();

        // Assert
        assertThat(result).isEqualTo(expectedDir);
    }

    @Test
    public void getRPathReturnsCorrectDefaultOnLinux() throws Exception {
        // Arrange
        OSChecker osChecker = mock(OSChecker.class);
        when(osChecker.isWindows()).thenReturn(false);
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, osChecker);
        if (Paths.get("/usr/bin/R").toFile().exists()) {
            // Act
            String result = target.getRExecutablePath();

            // Assert
            assertThat(result).isEqualTo("/usr/bin/R");
        } else {
            // Act
            catchException(target).getRExecutablePath();

            // Assert
            assertThat(caughtException()).isInstanceOf(ConfigurationException.class);
        }
    }

    @Test
    public void getRPathReturnsCorrectDefaultOnWindows() throws Exception {
        // Arrange
        OSChecker osChecker = mock(OSChecker.class);
        when(osChecker.isWindows()).thenReturn(true);
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, osChecker);
        String defaultWindowsRPath = System.getenv("R_HOME") + "\\bin\\x64\\R.exe";
        if (Paths.get(defaultWindowsRPath).toFile().exists()) {
            // Act
            String result = target.getRExecutablePath();

            // Assert
            assertThat(result).isEqualTo(defaultWindowsRPath);
        } else {
            // Act
            catchException(target).getRExecutablePath();

            // Assert
            assertThat(caughtException()).isInstanceOf(ConfigurationException.class);
        }
    }

    @Test
    public void getRPathReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", "r.executable.path", expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getRExecutablePath();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void setRExecutablePathUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        String expectedValue = "foo";

        // Act
        target.setRExecutablePath(expectedValue);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("r.executable.path = " + expectedValue);
    }

    @Test
    public void getMaxModelRunDurationReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        int result = target.getMaxModelRunDuration();

        // Assert
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void getMaxModelRunDurationReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        int expectedValue = 1234;
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", "r.max.duration", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        int result = target.getMaxModelRunDuration();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void setMaxModelRunDurationUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        int expectedValue = 123;

        // Act
        target.setMaxModelRunDuration(expectedValue);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("r.max.duration = " + expectedValue);
    }

    @Test
    public void getGlobalRasterFileReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getGlobalRasterFile();

        // Assert
        String expectedPath = Paths.get(target.getCacheDirectory(), "rasters", "admin_global.tif").toString();
        assertThat(result).isEqualTo(expectedPath);
    }

    @Test
    public void getGlobalRasterFileReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "Foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "raster.file.global", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getGlobalRasterFile();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getTropicalRasterFileReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getTropicalRasterFile();

        // Assert
        String expectedPath = Paths.get(target.getCacheDirectory(), "rasters", "admin_tropical.tif").toString();
        assertThat(result).isEqualTo(expectedPath);
    }

    @Test
    public void getTropicalRasterFileReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "Foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "raster.file.tropical", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getTropicalRasterFile();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getAdmin1RasterFileReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getAdmin1RasterFile();

        // Assert
        String expectedPath = Paths.get(target.getCacheDirectory(), "rasters", "admin1qc.tif").toString();
        assertThat(result).isEqualTo(expectedPath);
    }

    @Test
    public void getAdmin1RasterFileReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "Foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "raster.file.admin1", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getAdmin1RasterFile();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getAdmin2RasterFileReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getAdmin2RasterFile();

        // Assert
        String expectedPath = Paths.get(target.getCacheDirectory(), "rasters", "admin2qc.tif").toString();
        assertThat(result).isEqualTo(expectedPath);
    }

    @Test
    public void getAdmin2RasterFileReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "Foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "raster.file.admin2", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getAdmin2RasterFile();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getMaxCPUsReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        Integer result = target.getMaxCPUs();

        // Assert
        assertThat(result).isEqualTo(64);
    }

    @Test
    public void getMaxCPUsReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        Integer expectedValue = 4;
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "model.max.cpu", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        Integer result = target.getMaxCPUs();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
         public void getDryRunFlagReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        Boolean result = target.getDryRunFlag();

        // Assert
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void getDryRunFlagReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        Boolean expectedValue = true;
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "model.dry.run", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        Boolean result = target.getDryRunFlag();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getModelVerboseFlagReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        Boolean result = target.getModelVerboseFlag();

        // Assert
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void getModelVerboseFlagReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        Boolean expectedValue = true;
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "model.verbose", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        Boolean result = target.getModelVerboseFlag();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getCovariateDirectoryReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, new OSCheckerImpl());

        // Act
        String result = target.getCovariateDirectory();

        // Assert
        assertThat(new File(result).getParentFile().getAbsolutePath()).isEqualTo(target.getCacheDirectory());
        assertThat(new File(result).getName()).isEqualTo("covariates");
    }

    @Test
    public void getCovariateDirectoryReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", "covariate.dir", expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getCovariateDirectory();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void setCovariateDirectoryUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, null);

        String expectedValue = "bar";

        // Act
        target.setCovariateDirectory(expectedValue);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("covariate.dir = " + expectedValue);
    }

    @Test
    public void getCovariateConfigurationReturnsCorrectObject() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        File covariateDir = testFolder.newFolder();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", covariateDir.toString());

        File confFile = Paths.get(covariateDir.toString(), "abraid.json").toFile();
        FileUtils.writeStringToFile(confFile, TEST_COVARIATE_JSON);

        JsonCovariateConfiguration conf = createJsonCovariateConfig();
        sortConfig(conf);

        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        JsonCovariateConfiguration result = target.getCovariateConfiguration();

        // Assert
        assertThat(result).isEqualTo(conf);
    }

    private void sortConfig(JsonCovariateConfiguration conf) {
        Collections.sort(conf.getDiseases(), new Comparator<JsonDisease>() {
            @Override
            public int compare(JsonDisease o1, JsonDisease o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });

        Collections.sort(conf.getFiles(), new Comparator<JsonCovariateFile>() {
            @Override
            public int compare(JsonCovariateFile o1, JsonCovariateFile o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
    }

    @Test
    public void getCovariateConfigurationReturnsCorrectFallbackObject() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        File covariateDir = testFolder.newFolder();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", "nonsense");

        File confFile = Paths.get(covariateDir.toString(), "abraid.json").toFile();
        FileUtils.writeStringToFile(confFile, TEST_COVARIATE_JSON);

        JsonCovariateConfiguration conf = createJsonCovariateConfig();
        sortConfig(conf);

        ConfigurationService target = new ConfigurationServiceImpl(testFile, confFile, mock(OSChecker.class));

        // Act
        JsonCovariateConfiguration result = target.getCovariateConfiguration();

        // Assert
        assertThat(result).isEqualTo(conf);
    }

    @Test
    public void getCovariateConfigurationAddsNewFilesCorrectly() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        File covariateDir = testFolder.newFolder();
        File covariateConfFile = testFolder.newFile();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", covariateDir.toString());

        FileUtils.writeStringToFile(covariateConfFile, TEST_COVARIATE_JSON);

        FileUtils.writeStringToFile(Paths.get(covariateDir.toString(), "1").toFile(), "foo");
        FileUtils.writeStringToFile(Paths.get(covariateDir.toString(), "2").toFile(), "foo");
        FileUtils.writeStringToFile(Paths.get(covariateDir.toString(), "3").toFile(), "foo");

        ConfigurationService target = new ConfigurationServiceImpl(testFile, covariateConfFile, mock(OSChecker.class));

        // Act
        JsonCovariateConfiguration result = target.getCovariateConfiguration();

        // Assert
        assertThat(result.getFiles().size()).isEqualTo(2 + 3);
    }

    @Test
    public void getCovariateConfigurationThrowsIfFilesNotReadable() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", "nonsense");

        ConfigurationService target = new ConfigurationServiceImpl(testFile, new File("nonsense"), mock(OSChecker.class));

        // Act
        catchException(target).getCovariateConfiguration();

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Failed to read and parse covariate config file from disk.");
    }

    @Test
    public void getCovariateConfigurationThrowsIfConfigurationNoValid() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        File covariateConfFile = testFolder.newFile();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", "nonsense");

        FileUtils.writeStringToFile(covariateConfFile, "{}");

        ConfigurationService target = new ConfigurationServiceImpl(testFile, covariateConfFile, mock(OSChecker.class));

        // Act
        catchException(target).getCovariateConfiguration();

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Covariate config file on disk is not valid.");
    }

    @Test
    public void setCovariateConfigurationWritesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        File covariateDir = testFolder.newFolder();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", covariateDir.toString());

        JsonCovariateConfiguration conf = createJsonCovariateConfig();

        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        target.setCovariateConfiguration(conf);
        String result = FileUtils.readFileToString(Paths.get(covariateDir.toString(), "abraid.json").toFile());
        // In abraid.json the covariates files are sorted by path, and diseases by id. In "conf" they are not sorted.

        // Assert
        assertThat(result.replaceAll("[\\r\\n]+", "")).isEqualTo(TEST_COVARIATE_JSON.replaceAll("[\\r\\n]+", ""));
    }

    @Test
    public void setCovariateConfigurationThrowsIfDirectoryCanNotBeCreated() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        File notADir = testFolder.newFile();
        writeStandardSimplePropertiesWithExtra(testFile,
                "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "covariate.dir", notADir.toString());

        JsonCovariateConfiguration conf = createJsonCovariateConfig();
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        catchException(target).setCovariateConfiguration(conf);

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessageStartingWith("Cannot store covariate config.");
    }

    @Test
    public void getModelOutputHandlerRootUrlReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        Map pairs = new HashMap();
        pairs.put("model.output.api.key", "password");
        pairs.put("model.output.handler.host", "localhost:8080");
        pairs.put("model.output.handler.path", "/modeloutputhandler");
        pairs.put("model.output.handler.protocol", "http");
        pairs.put("model.output.handler.root.url", "${model.output.handler.protocol}://api:${model.output.api.key}@${model.output.handler.host}${model.output.handler.path}");

        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", pairs);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null, mock(OSChecker.class));

        // Act
        String result = target.getModelOutputHandlerRootUrl();

        // Assert
        assertThat(result).isEqualTo("http://api:password@localhost:8080/modeloutputhandler");
    }

    private static JsonCovariateConfiguration createJsonCovariateConfig() {
        JsonCovariateConfiguration conf = new JsonCovariateConfiguration();
        conf.setDiseases(Arrays.asList(
                new JsonDisease(64, "Cholera"),
                new JsonDisease(22, "Ascariasis")
        ));
        conf.setFiles(Arrays.asList(
                new JsonCovariateFile("f2", "", null, true, new ArrayList<Integer>()),
                new JsonCovariateFile("f1", "a", null, false, Arrays.asList(22))
        ));
        return conf;
    }
}
