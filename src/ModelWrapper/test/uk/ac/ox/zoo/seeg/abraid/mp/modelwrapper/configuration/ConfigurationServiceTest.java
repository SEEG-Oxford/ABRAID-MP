package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
        writeStandardSimpleProperties(testFile, "initialValue1", "initialValue2");
        ConfigurationService target = new ConfigurationServiceImpl(testFile);

        String expectedUserName = "foo";
        String expectedPasswordHash = "bar";

        // Act
        target.setAuthenticationDetails(expectedUserName, expectedPasswordHash);

        // Assert
        assertThat(testFile).hasContent("auth.username=" + expectedUserName + System.lineSeparator() +
                "auth.password_hash=" + expectedPasswordHash);
    }

    private void writeStandardSimpleProperties(File testFile, String defaultUsername, String defaultPasswordHash) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(testFile, "UTF-8");
        writer.println("auth.username=" + defaultUsername);
        writer.println("auth.password_hash=" + defaultPasswordHash);
        writer.close();
    }

    @Test
    public void getAuthenticationUsernameReturnsCorrectValue() throws Exception {
        // Arrange
        File testFile = testFolder.newFile();
        String expectedUserName = "foo";
        writeStandardSimpleProperties(testFile, expectedUserName, "initialValue2");
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
        writeStandardSimpleProperties(testFile, "initialValue1", expectedPasswordHash);
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        // Act
        String result = s.getAuthenticationPasswordHash();

        // Assert
        assertThat(result).isEqualTo(expectedPasswordHash);
    }
}
