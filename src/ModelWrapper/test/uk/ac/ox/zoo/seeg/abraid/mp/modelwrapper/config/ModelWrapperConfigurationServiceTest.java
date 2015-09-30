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
* Tests the ModelWrapperConfigurationService class.
* Copyright (c) 2014 University of Oxford
*/
public class ModelWrapperConfigurationServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private void writeStandardSimpleProperties(File testFile, String defaultUsername, String defaultPasswordHash)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(testFile, "UTF-8");
        writer.println("auth.username = " + defaultUsername);
        writer.println("auth.password_hash = " + defaultPasswordHash);
        writer.close();
    }

    private void writeStandardSimplePropertiesWithExtra(File testFile, String defaultUsername, String defaultPasswordHash, String extraKey, String extraValue)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        writer.println(extraKey + " = " + StringEscapeUtils.escapeJava(extraValue));
        writer.close();
    }

    private void writeStandardSimplePropertiesWithExtra(File testFile, String defaultUsername, String defaultPasswordHash, Map<String, String> extraPairs)
            throws IOException {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash);
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
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, null);

        String expectedUserName = "foo";
        String expectedPasswordHash = "bar";

        // Act
        target.setAuthenticationDetails(expectedUserName, expectedPasswordHash);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("auth.username = " + expectedUserName + System.lineSeparator() +
                "auth.password_hash = " + expectedPasswordHash);
    }

    @Test
    public void getAuthenticationUsernameReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedUserName = "foo";
        writeStandardSimpleProperties(testFile, expectedUserName, "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, null);

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
        writeStandardSimpleProperties(testFile, "initialValue1", expectedPasswordHash);
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, null);

        // Act
        String result = target.getAuthenticationPasswordHash();

        // Assert
        assertThat(result).isEqualTo(expectedPasswordHash);
    }

    @Test
    public void getCacheDirectoryReturnsCorrectDefaultOnWindows() throws Exception {
        // Arrange
        OSChecker osChecker = mock(OSChecker.class);
        when(osChecker.isWindows()).thenReturn(true);
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, osChecker);

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
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, osChecker);

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
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "cache.data.dir", expectedDir);
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, osChecker);

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
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, osChecker);
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
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, osChecker);
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
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "r.executable.path", expectedValue);
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        String result = target.getRExecutablePath();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void setRExecutablePathUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, null);

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
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, mock(OSChecker.class));

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
        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", "r.max.duration", "" + expectedValue);
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        int result = target.getMaxModelRunDuration();

        // Assert
        assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    public void setMaxModelRunDurationUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, null);

        int expectedValue = 123;

        // Act
        target.setMaxModelRunDuration(expectedValue);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("r.max.duration = " + expectedValue);
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

        writeStandardSimplePropertiesWithExtra(testFile, "initialValue1", "initialValue2", pairs);
        ModelWrapperConfigurationService target = new ModelWrapperConfigurationServiceImpl(testFile, mock(OSChecker.class));

        // Act
        String result = target.getModelOutputHandlerRootUrl();

        // Assert
        assertThat(result).isEqualTo("http://api:password@localhost:8080/modeloutputhandler");
    }
}
