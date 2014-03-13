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
        writeStandardSimpleProperties(testFile);
        ConfigurationService s = new ConfigurationServiceImpl(testFile);

        String expectedUserName = "foo";
        String expectedPasswordHash = "bar";

        // Act
        s.setAuthenticationDetails(expectedUserName, expectedPasswordHash);

        // Assert
        assertThat(testFile).hasContent("auth.username=" + expectedUserName + System.lineSeparator() +
                                        "auth.password_hash=" + expectedPasswordHash);
    }

    private void writeStandardSimpleProperties(File testFile) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(testFile, "UTF-8");
        writer.println("auth.username=initialValue1");
        writer.println("auth.password_hash=initialValue2");
        writer.close();
    }
}
