package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for JsonCovariateConfiguration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateConfigurationTest {
    @Test
    public void bindsFieldsCorrectly() {
        // Arrange
        List<JsonModelDisease> expectedDiseases = new ArrayList<>();
        List<JsonCovariateFile> expectedFiles = new ArrayList<>();

        // Act
        JsonCovariateConfiguration result = new JsonCovariateConfiguration(expectedDiseases, expectedFiles);

        // Assert
        assertThat(result.getDiseases()).isEqualTo(expectedDiseases);
        assertThat(result.getFiles()).isEqualTo(expectedFiles);
    }

}
