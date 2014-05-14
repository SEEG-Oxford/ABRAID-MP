package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the Configuration Service.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceTest {

    @Test
    public void getLastRetrievalDateReturnsNullForEmptyProperty() throws Exception {
        // Arrange
        File propertiesFile = createPropertiesFile();
        ConfigurationService service = new ConfigurationServiceImpl(propertiesFile);

        // Act
        LocalDateTime result = service.getLastRetrievalDate();
        propertiesFile.delete();

        // Assert
        assertThat(result).isNull();
    }

    private File createPropertiesFile() throws FileNotFoundException {
        File file = new File("foo");
        PrintWriter out = new PrintWriter(file);
        out.println("lastRetrievalDate =");
        out.close();
        return file;
    }

    @Test
    public void getLastRetrievalDateReturnsExpectedDate() throws Exception {
        // Arrange
        File file = new File("foo");
        ConfigurationService service = new ConfigurationServiceImpl(file);
        LocalDateTime expectedDate = LocalDateTime.now();
        service.setLastRetrievalDate(expectedDate);

        // Act
        LocalDateTime result = service.getLastRetrievalDate();
        file.delete();

        // Assert
        assertThat(result).isEqualTo(expectedDate);
    }
}
