package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.apache.commons.exec.OS;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ConfigurationService class.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void setAuthenticationDetailsUpdatesFile() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3");
        ConfigurationService target = new ConfigurationServiceImpl(testFile);

        String expectedUserName = "foo";
        String expectedPasswordHash = "bar";

        // Act
        target.setAuthenticationDetails(expectedUserName, expectedPasswordHash);

        // Assert
        assertThat(FileUtils.readFileToString(testFile)).contains("auth.username=" + expectedUserName + System.lineSeparator() +
                "auth.password_hash=" + expectedPasswordHash);
    }

    private void writeStandardSimpleProperties(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(testFile, "UTF-8");
        writer.println("auth.username=" + defaultUsername);
        writer.println("auth.password_hash=" + defaultPasswordHash);
        writer.println("model.repo.url=" + defaultRepoUrl);
        writer.close();
    }

    private void writeStandardSimplePropertiesWithCacheDir(File testFile, String defaultUsername, String defaultPasswordHash, String defaultRepoUrl, String defaultCacheDir) throws Exception {
        writeStandardSimpleProperties(testFile, defaultUsername, defaultPasswordHash, defaultRepoUrl);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(testFile, true)));
        writer.println("cache.data.dir=" + defaultCacheDir);
        writer.close();
    }

    @Test
    public void getAuthenticationUsernameReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedUserName = "foo";
        writeStandardSimpleProperties(testFile, expectedUserName, "initialValue2", "initialValue3");
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        // Act
        String result = s.getAuthenticationUsername();

        // Assert
        assertThat(result).isEqualTo(expectedUserName);
    }

    @Test
    public void getAuthenticationPasswordHashReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedPasswordHash = "foo";
        writeStandardSimpleProperties(testFile, "initialValue1", expectedPasswordHash, "initialValue3");
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        // Act
        String result = s.getAuthenticationPasswordHash();

        // Assert
        assertThat(result).isEqualTo(expectedPasswordHash);
    }

    @Test
    public void getModelRepositoryUrlReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedUrl = "foo";
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", expectedUrl);
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        // Act
        String result = s.getModelRepositoryUrl();

        // Assert
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    public void getCacheDirectoryReturnsCorrectDefault() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2", "initialValue3");
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        // Act
        String result = s.getCacheDirectory();

        // Assert
        assertThat(result).isEqualTo(OS.isFamilyWindows() ?
                System.getenv("LOCALAPPDATA") + "\\abraid\\modelwrapper" :
                "/var/lib/abraid/modelwrapper");
    }

    @Test
    public void getCacheDirectoryReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedDir = "foo";
        writeStandardSimplePropertiesWithCacheDir(testFile, "initialValue1", "initialValue2", "initialValue3", expectedDir);
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        // Act
        String result = s.getCacheDirectory();

        // Assert
        assertThat(result).isEqualTo(expectedDir);
    }
}
