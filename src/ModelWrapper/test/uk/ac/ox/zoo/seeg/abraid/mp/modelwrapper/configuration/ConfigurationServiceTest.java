package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;

import java.io.*;
import java.nio.file.Paths;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
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

    private void writeStandardSimplePropertiesWithCacheDir(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultModelVersion, String defaultCacheDir)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash, defaultRepoUrl, defaultModelVersion);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        writer.println("cache.data.dir = " + defaultCacheDir);
        writer.close();
    }

    private void writeStandardSimplePropertiesWithRPath(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultModelVersion, String defaultRPath)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash, defaultRepoUrl, defaultModelVersion);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        writer.println("r.executable.path = " + defaultRPath);
        writer.close();
    }

    private void writeStandardSimplePropertiesWithRunDuration(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultModelVersion, String defaultRunDuration)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash, defaultRepoUrl, defaultModelVersion);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        writer.println("r.max.duration = " + defaultRunDuration);
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
        writeStandardSimplePropertiesWithCacheDir(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", expectedDir);
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
        if (Paths.get(System.getenv("R_HOME") + "\\bin\\R.exe").toFile().exists()) {
            // Act
            String result = target.getRExecutablePath();

            // Assert
            assertThat(result).isEqualTo(System.getenv("R_HOME") + "\\bin\\R.exe");
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
        writeStandardSimplePropertiesWithRPath(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", expectedValue);
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
        writeStandardSimplePropertiesWithRunDuration(testFile, "initialValue1", "initialValue2", "initialValue3", "initialValue4", "" + expectedValue);
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
}
