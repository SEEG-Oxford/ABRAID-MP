package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, osChecker);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, osChecker);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, osChecker);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, osChecker);
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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, osChecker);
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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, null);

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        String result = target.getTropicalRasterFile();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void getAdmin0RasterFileReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        String result = target.getAdmin0RasterFile();

        // Assert
        String expectedPath = Paths.get(target.getCacheDirectory(), "rasters", "admin0qc.tif").toString();
        assertThat(result).isEqualTo(expectedPath);
    }

    @Test
    public void getAdmin0RasterFileReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedValue = "Foo";
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4",
                "raster.file.admin0", "" + expectedValue);
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        String result = target.getAdmin0RasterFile();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }


    @Test
    public void getAdmin1RasterFileReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4");
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        Boolean result = target.getModelVerboseFlag();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
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
        ConfigurationService target = new ConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        String result = target.getModelOutputHandlerRootUrl();

        // Assert
        assertThat(result).isEqualTo("http://api:password@localhost:8080/modeloutputhandler");
    }
}
