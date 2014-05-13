package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config;

import org.joda.time.DateTime;
import org.junit.Test;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the Configuration Service.
 * Copyright (c) 2014 University of Oxford
 */
public class ConfigurationServiceTest {
    @Test
    public void testSetLastRetrievalDate() throws Exception {
        // Arrange
        DateTime date = DateTime.now();
        File file = new File("foo");
        ConfigurationService service = new ConfigurationServiceImpl(file);

        // Act
        service.setLastRetrievalDate(date);

        // Assert
        assertThat(service.getLastRetrievalDate()).isEqualTo(date);
    }
}
